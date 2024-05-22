package com.example.tesourosartsticos

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.Navigation
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val PICK_IMAGE_REQUEST = 1

class adicionar_figurinha : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_adicionar_figurinha, container, false)

        val btnProximo = view.findViewById<Button>(R.id.button6)
        val btnSelectImage = view.findViewById<Button>(R.id.inputImage)

        btnSelectImage.setOnClickListener {
            openImageChooser()
        }

        btnProximo.setOnClickListener {
            val titulo = view.findViewById<EditText>(R.id.editTitle)
            val autor = view.findViewById<EditText>(R.id.editAuthor)
            val desc = view.findViewById<EditText>(R.id.editDescription)

            val tituloTxT = titulo.text.toString()
            val autorTxT = autor.text.toString()
            val descTxT = desc.text.toString()

            val myUuid = UUID.randomUUID()
            val myUuidAsString = myUuid.toString()

            if (TextUtils.isEmpty(tituloTxT) || TextUtils.isEmpty(autorTxT) || TextUtils.isEmpty(descTxT) || imageUri == null) {
                Toast.makeText(
                    requireContext(),
                    "Preencha todos os campos e selecione uma imagem",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val obra = mapOf(
                "obraId" to myUuidAsString,
                "titulo" to tituloTxT,
                "autor" to autorTxT,
                "descricao" to descTxT,
                "dataCriacao" to FieldValue.serverTimestamp()
            )

            // Adicionar obra ao Firestore
            val db = Firebase.firestore
            db.collection("Obras").add(obra)
                .addOnSuccessListener { documentReference ->
                    val pathGerado = documentReference.id // Captura o path gerado
                    uploadImageToFirebase(pathGerado, imageUri!!, view)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Erro ao criar figurinha: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        return view
    }

    private fun openImageChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
        }
    }
    private fun uploadImageToFirebase(pathObra: String, filePath: Uri, view: View) {
        val storageReference = FirebaseStorage.getInstance().reference
        val ref = storageReference.child("InputsImages/$pathObra/${UUID.randomUUID()}")
        val uploadTask = ref.putFile(filePath)

        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    Log.e("UploadError", "Erro durante o upload: ${it.message}", it)
                    throw it
                }
            }
            ref.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                saveImageUrlToFirestore(pathObra, downloadUri.toString(), view)
            } else {
                Log.e("UploadError", "Erro ao obter URL de download", task.exception)
                Toast.makeText(requireContext(), "Erro ao fazer upload da imagem", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun saveImageUrlToFirestore(pathObra: String, imageUrl: String, view: View) {
        val db = Firebase.firestore
        db.collection("Obras").document(pathObra)
            .update("imageUrl", imageUrl)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Imagem carregada com sucesso", Toast.LENGTH_SHORT).show()
                // Passar o path para o fragmento adicionar_quiz
                val bundle = Bundle()
                bundle.putString("pathObra", pathObra)
                Navigation.findNavController(view).navigate(R.id.navToAddQuiz, bundle)
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Erro ao salvar URL da imagem: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            adicionar_figurinha().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
