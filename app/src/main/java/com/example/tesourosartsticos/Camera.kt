package com.example.tesourosartsticos
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.annotation.OptIn
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.ExecutionException

class Camera : Fragment() {
    private lateinit var qrCodeTxt: EditText
    private lateinit var previewView: PreviewView
    private lateinit var cameraProviderListenableFuture: ListenableFuture<ProcessCameraProvider>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar o layout do fragmento
        val view = inflater.inflate(R.layout.fragment_camera, container, false)

        // Inicializar as visualizações
        qrCodeTxt = view.findViewById(R.id.qrCodeTxt)
        previewView = view.findViewById(R.id.cameraPreview)

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Verificar permissões da câmera
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            initScan()
        } else {
            // Pedir permissão
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), 101)
        }
    }

    private fun initScan() {
        val cameraProviderListenableFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderListenableFuture.addListener({
            try {
                val cameraProvider = cameraProviderListenableFuture.get()
                bindImageAnalysis(cameraProvider)
            } catch (e: ExecutionException) {
                e.printStackTrace()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    @OptIn(ExperimentalGetImage::class) private fun bindImageAnalysis(processCameraProvider: ProcessCameraProvider) {
        val imageAnalysis = ImageAnalysis.Builder()

            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(requireActivity()), ImageAnalysis.Analyzer { image ->
            val mediaImage = image.image

            if (mediaImage != null) {
                val image2 = InputImage.fromMediaImage(mediaImage, image.imageInfo.rotationDegrees)

                val scanner = BarcodeScanning.getClient()

                val results = scanner.process(image2)

                results.addOnSuccessListener { barcodes ->
                    for (barcode in barcodes) {
                        val getValue = barcode.rawValue

                        qrCodeTxt.setText(getValue)
                    }

                    image.close()
                    mediaImage.close()
                }
            }
        })

        val preview = Preview.Builder().build()
        val cameraSelector = CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
        preview.setSurfaceProvider(previewView.surfaceProvider)
        processCameraProvider.bindToLifecycle(this, cameraSelector, imageAnalysis, preview)
    }
}
