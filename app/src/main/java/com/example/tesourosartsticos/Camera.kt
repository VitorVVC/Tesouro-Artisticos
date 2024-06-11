package com.example.tesourosartsticos

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.common.util.concurrent.ListenableFuture
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.ExecutionException

class Camera : Fragment() {
    private lateinit var qrCodeTxt: EditText
    private lateinit var previewView: PreviewView
    private lateinit var cameraProviderListenableFuture: ListenableFuture<ProcessCameraProvider>
    private var cameraProvider: ProcessCameraProvider? = null
    private var camera: androidx.camera.core.Camera? = null
    private var userPath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userPath = it.getString("USER_PATH")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_camera, container, false)

        qrCodeTxt = view.findViewById(R.id.qrCodeTxt)
        previewView = view.findViewById(R.id.cameraPreview)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the camera
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            initCamera()
        } else {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), 101)
        }
    }

    override fun onResume() {
        super.onResume()
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            initCamera()
        }
    }

    override fun onPause() {
        super.onPause()
        releaseCamera()
    }

    private fun initCamera() {

        cameraProviderListenableFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderListenableFuture.addListener({
            try {
                cameraProvider = cameraProviderListenableFuture.get()
                bindPreviewAndAnalysis(cameraProvider!!)
            } catch (e: ExecutionException) {
                e.printStackTrace()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun releaseCamera() {
        cameraProvider?.unbindAll()
    }

    @androidx.annotation.OptIn(ExperimentalGetImage::class)
    private fun bindPreviewAndAnalysis(cameraProvider: ProcessCameraProvider) {

        val preview = Preview.Builder()
            .build()

        val imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(requireActivity()), ImageAnalysis.Analyzer { image ->
            val mediaImage = image.image

            if (mediaImage != null) {
                val inputImage = InputImage.fromMediaImage(mediaImage, image.imageInfo.rotationDegrees)
                val scanner = BarcodeScanning.getClient()

                val results = scanner.process(inputImage)

                results.addOnSuccessListener { barcodes ->
                    for (barcode in barcodes) {
                        val qrCodeValue = barcode.rawValue
                        if (qrCodeValue != null) {
                            lifecycleScope.launch {
                                checkQrCodeInDatabase(qrCodeValue)
                            }
                        }
                    }

                    image.close()
                    mediaImage.close()
                }
            }
        })

        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        try {
            cameraProvider.unbindAll()

            camera = cameraProvider.bindToLifecycle(
                viewLifecycleOwner, cameraSelector, preview, imageAnalysis
            )
            preview.setSurfaceProvider(previewView.surfaceProvider)
        } catch (exc: IllegalArgumentException) {
            Toast.makeText(requireContext(), "Erro ao configurar a câmera: ${exc.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private suspend fun checkQrCodeInDatabase(qrCodeValue: String) = withContext(Dispatchers.Main) {
        val db = Firebase.firestore
        db.collection("Logins/$userPath/ObrasUser").document(qrCodeValue).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val obraTitle = document.getString("titulo") ?: "Título não encontrado"
                    val obraImageUrl = document.getString("imageUrl")
                    val obraAutor = document.getString("autor")
                    val obraDescricao = document.getString("descricao")
                    val progresso = document.getLong("progresso")?.toInt() ?:0


                    if(progresso == 0){
                        db.collection("Logins/$userPath/ObrasUser")
                            .document(qrCodeValue)
                            .update("progresso", 1)
                    }


                    val bundle = Bundle().apply {
                        putString("titulo", obraTitle)
                        putString("imageUrl", obraImageUrl)
                        putString("autor", obraAutor)
                        putString("descricao", obraDescricao)
                        putString("userPath", userPath)
                        putString("obraPath", qrCodeValue)
                    }

                    findNavController().navigate(R.id.fragment_figurinha_obra, bundle)
                } else {
                    qrCodeTxt.setText("QR code não corresponde a nenhuma obra")
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Erro ao acessar o banco de dados: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    companion object {
        @JvmStatic
        fun newInstance(userPath: String) = Camera().apply {
            arguments = Bundle().apply {
                putString("USER_PATH", userPath)
            }
            Log.d("GetView(instancia)","View: $view \n userPath: $userPath \n \n")
        }
    }
}