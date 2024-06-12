package com.example.tesourosartsticos

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

private const val ARG_TITULO = "titulo"
private const val ARG_IMAGE_URL = "imageUrl"
private const val ARG_AUTOR = "autor"
private const val ARG_DESCRICAO = "descricao"
private const val USER_PATH = "USER_PATH"
private const val COMPLETE_QUIZ = "completeQuiz"

class AdmFigurinhaObra : Fragment() {
    private var titulo: String? = null
    private var imageUrl: String? = null
    private var autor: String? = null
    private var descricao: String? = null
    private var userPath: String? = null
    private var obraPath: String? = null // Adicionando a variável para o caminho da obra
    private var completeQuiz: Boolean =
        false // Adicionando a variável para indicar se o quiz foi completado
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            titulo = it.getString(ARG_TITULO)
            imageUrl = it.getString(ARG_IMAGE_URL)
            autor = it.getString(ARG_AUTOR)
            descricao = it.getString(ARG_DESCRICAO)
            userPath = it.getString("userPath")
            obraPath = it.getString("obraPath") // Obtendo o caminho da obra
            completeQuiz = it.getBoolean(COMPLETE_QUIZ) // Recuperando completeQuiz do Bundle
        }
        firestore = FirebaseFirestore.getInstance()
        fetchCompleteQuiz() // Recuperando completeQuiz do banco de dados
        Log.d(TAG, "onCreate (admFigurinhaObra.kt): userPath=$userPath, obraPath=$obraPath")
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_adm_figurinha_obra, container, false)

        val progressBar: ProgressBar = view.findViewById(R.id.progressBar)
        val tituloEditText: EditText = view.findViewById(R.id.editTitle)
        val imagemImageView: ImageView = view.findViewById(R.id.obraImage)
        val autorEditText: EditText = view.findViewById(R.id.editAuthor)
        val detalhesEditText: EditText = view.findViewById(R.id.editDescription)

        val btnRemove: Button = view.findViewById(R.id.btnRemove)
        val btnConfirm: Button = view.findViewById(R.id.btnConfirma)

        tituloEditText.setText(titulo)
        autorEditText.setText(autor)
        detalhesEditText.setText(descricao)

        btnRemove.setOnClickListener {
            removeFigurinha()
        }

        btnConfirm.setOnClickListener {
            val newTitulo = tituloEditText.text.toString()
            val newAutor = autorEditText.text.toString()
            val newDescricao = detalhesEditText.text.toString()

            if (newTitulo.isEmpty() || newAutor.isEmpty() || newDescricao.isEmpty()) {
                Toast.makeText(requireContext(), "Preencha todos os campos", Toast.LENGTH_SHORT)
                    .show()
            } else {
                updateFigurinha(newTitulo, newAutor, newDescricao)

            }

        }

        Glide.with(this)
            .load(imageUrl)
            .override(300, 300)
            .into(imagemImageView)

        userPath?.let { userPath ->
            obraPath?.let { obraPath ->
                val obraDocumentRef =
                    firestore.collection("Logins/$userPath/ObrasUser").document(obraPath)
                obraDocumentRef.get()
                    .addOnSuccessListener { document ->
                        if (document != null) {
                            titulo = document.getString("titulo")
                            imageUrl = document.getString("imageUrl")
                            autor = document.getString("autor")
                            descricao = document.getString("descricao")
                            val progress = document.getLong("progresso")?.toInt() ?: 0

                            progressBar.progress = when (progress) {
                                0 -> 0
                                1 -> progressBar.max / 2
                                2 -> progressBar.max
                                else -> 75
                            }
                            tituloEditText.setText(titulo)
                            autorEditText.setText(autor)
                            detalhesEditText.setText(descricao)
                            Glide.with(this@AdmFigurinhaObra)
                                .load(imageUrl)
                                .override(300, 300)
                                .into(imagemImageView)
                            completeQuiz = document.getBoolean("completeQuiz")
                                ?: false // Recuperando completeQuiz do banco de dados

                        } else {
                            Log.e("ERROR", "Documento não encontrado.")
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.e("ERROR", "Erro ao obter obra do usuário", exception)
                    }
            }
        }

        return view
    }

    private fun fetchCompleteQuiz() {
        userPath?.let { userPath ->
            obraPath?.let { obraPath ->
                val obraDocumentRef =
                    firestore.collection("Logins/$userPath/ObrasUser").document(obraPath)
                obraDocumentRef.get()
                    .addOnSuccessListener { document ->
                        completeQuiz = document.getBoolean("completeQuiz")
                            ?: false // Recuperando completeQuiz do banco de dados
                    }
                    .addOnFailureListener { exception ->
                        Log.e("ERROR", "Erro ao obter completeQuiz do banco de dados", exception)
                    }
            }
        }
    }

    private fun updateFigurinha(newTitulo: String, newAutor: String, newDescricao: String) {
        userPath?.let { userPath ->
            obraPath?.let { obraPath ->
                val obraDocumentRef =
                    firestore.collection("Logins/$userPath/ObrasUser").document(obraPath)

                val updatedData = mapOf(
                    "titulo" to newTitulo,
                    "autor" to newAutor,
                    "descricao" to newDescricao,
                    "dataAtualizacao" to FieldValue.serverTimestamp()
                )

                obraDocumentRef.update(updatedData)
                    .addOnSuccessListener {
                        Toast.makeText(
                            requireContext(),
                            "Figurinha atualizada com sucesso",
                            Toast.LENGTH_SHORT
                        ).show()
                        val bundle = Bundle().apply {
                            putString("USER_PATH", userPath)
                        }
                        findNavController().navigate(R.id.backToGerenciarObras, bundle)
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(
                            requireContext(),
                            "Erro ao atualizar figurinha: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
        }
    }

    private fun removeFigurinha() {
        userPath?.let { userPath ->
            obraPath?.let { obraPath ->
                val obraDocumentRef =
                    firestore.collection("Logins/$userPath/ObrasUser").document(obraPath)

                obraDocumentRef.delete()
                    .addOnSuccessListener {
                        Toast.makeText(
                            requireContext(),
                            "Figurinha removida com sucesso",
                            Toast.LENGTH_SHORT
                        ).show()
                        findNavController().navigate(R.id.backToGerenciarObras)
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(
                            requireContext(),
                            "Erro ao remover figurinha: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(
            titulo: String,
            imageUrl: String,
            autor: String,
            descricao: String,
            userPath: String,
            obraPath: String, // Adicionando o novo parâmetro
            completeQuiz: Boolean // Adicionando o novo parâmetro
        ) =
            AdmFigurinhaObra().apply {
                arguments = Bundle().apply {
                    putString(ARG_TITULO, titulo)
                    putString(ARG_IMAGE_URL, imageUrl)
                    putString(ARG_AUTOR, autor)
                    putString(ARG_DESCRICAO, descricao)
                    putString(USER_PATH, userPath)
                    putString("obraPath", obraPath) // Adicionando ao Bundle
                    putBoolean(COMPLETE_QUIZ, completeQuiz) // Adicionando ao Bundle
                }
            }
    }
}
