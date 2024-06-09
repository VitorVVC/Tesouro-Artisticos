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
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore

private const val ARG_TITULO = "titulo"
private const val ARG_IMAGE_URL = "imageUrl"
private const val ARG_AUTOR = "autor"
private const val ARG_DESCRICAO = "descricao"
private const val USER_PATH = "USER_PATH"
private const val COMPLETE_QUIZ = "completeQuiz"

class figurinhaObra : Fragment() {
    private var titulo: String? = null
    private var imageUrl: String? = null
    private var autor: String? = null
    private var descricao: String? = null
    private var userPath: String? = null
    private var obraPath: String? = null // Adicionando a variável para o caminho da obra
    private var completeQuiz: Boolean = false // Adicionando a variável para indicar se o quiz foi completado
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
        Log.d(TAG, "onCreate (figurinhaObra.kt): userPath=$userPath, obraPath=$obraPath")
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_figurinha_obra, container, false)

        val progressBar: ProgressBar = view.findViewById(R.id.progressBar)
        val tituloTextView: Button = view.findViewById(R.id.headerObra)
        val imagemImageView: ImageView = view.findViewById(R.id.obraImage)
        val autorTextView: TextView = view.findViewById(R.id.autor)
        val detalhesTextView: TextView = view.findViewById(R.id.descricao)
        val bordaImage: Button = view.findViewById(R.id.borda)

        val btnGoToQuiz: Button = view.findViewById(R.id.btnGoToQuiz)
        btnGoToQuiz.setOnClickListener {
            if (!completeQuiz) {
                // Navegar para o quiz da obra específica
                val bundle = Bundle().apply {
                    putString(USER_PATH, userPath)
                    putString("obraPath", obraPath)
                    putBoolean(COMPLETE_QUIZ, completeQuiz)
                }
                findNavController().navigate(R.id.quiz, bundle)
            } else {
                // Exibir mensagem informando que o usuário já completou o quiz
                Toast.makeText(context, "Você já completou este quiz.", Toast.LENGTH_SHORT).show()
            }
        }

        tituloTextView.text = titulo
        autorTextView.text = autor
        detalhesTextView.text = descricao

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
                            tituloTextView.text = titulo
                            autorTextView.text = autor
                            detalhesTextView.text = descricao
                            Glide.with(this@figurinhaObra)
                                .load(imageUrl)
                                .override(300, 300)
                                .into(imagemImageView)
                            completeQuiz = document.getBoolean("completeQuiz") ?: false // Recuperando completeQuiz do banco de dados

                        } else {
                            Log.e("ERROR", "Documento não encontrado.")
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.e("ERROR", "Erro ao obter obra do usuário", exception)
                    }
            }
        }

        val btnVoltar = view.findViewById<Button>(R.id.btnObraVoltar)
        btnVoltar.setOnClickListener {
            findNavController().popBackStack()
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
                        completeQuiz = document.getBoolean("completeQuiz") ?: false // Recuperando completeQuiz do banco de dados
                    }
                    .addOnFailureListener { exception ->
                        Log.e("ERROR", "Erro ao obter completeQuiz do banco de dados", exception)
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
            figurinhaObra().apply {
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
