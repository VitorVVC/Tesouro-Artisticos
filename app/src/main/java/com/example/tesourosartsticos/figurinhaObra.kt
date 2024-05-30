package com.example.tesourosartsticos

import android.annotation.SuppressLint
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
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore

private const val ARG_TITULO = "titulo"
private const val ARG_IMAGE_URL = "imageUrl"
private const val ARG_AUTOR = "autor"
private const val ARG_DESCRICAO = "descricao"
private const val USER_PATH = "USER_PATH"

class FigurinhaObra : Fragment() {
    private var titulo: String? = null
    private var imageUrl: String? = null
    private var autor: String? = null
    private var descricao: String? = null
    private var userPath: String? = null
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            titulo = it.getString(ARG_TITULO)
            imageUrl = it.getString(ARG_IMAGE_URL)
            autor = it.getString(ARG_AUTOR)
            descricao = it.getString(ARG_DESCRICAO)
            userPath = it.getString(USER_PATH)
        }
        firestore = FirebaseFirestore.getInstance()
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_figurinha_obra, container, false)

        val tituloTextView: Button = view.findViewById(R.id.headerObra)
        val imagemImageView: ImageView = view.findViewById(R.id.obraImage)
        val autorTextView: TextView = view.findViewById(R.id.autor)
        val detalhesTextView: TextView = view.findViewById(R.id.descricao)
        val bordaImage: Button = view.findViewById(R.id.borda)
        val progressBar: ProgressBar = view.findViewById(R.id.progressBar)

        tituloTextView.text = titulo
        autorTextView.text = autor
        detalhesTextView.text = descricao

        Glide.with(this)
            .load(imageUrl)
            .override(300, 300)
            .into(imagemImageView)

        val btnVoltar = view.findViewById<Button>(R.id.btnVoltar)
        btnVoltar.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.colecao)
        }

        userPath?.let { userPath ->
            val obrasUserCollectionRef = firestore.collection("Logins/$userPath/ObrasUser")
            obrasUserCollectionRef.get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        // Atualizar as variáveis com os dados da primeira obra encontrada
                        titulo = document.getString("titulo")
                        imageUrl = document.getString("imageUrl")
                        autor = document.getString("autor")
                        descricao = document.getString("descricao")
                        val progress = document.getLong("progresso")?.toInt() ?: 0 // Valor padrão é 0

                        // Atualizar os campos na UI
                        tituloTextView.text = titulo
                        autorTextView.text = autor
                        detalhesTextView.text = descricao
                        Glide.with(this@FigurinhaObra)
                            .load(imageUrl)
                            .override(300, 300)
                            .into(imagemImageView)

                        // Atualizar a progressBar com base no progresso
                        progressBar.progress = when (progress) {
                            0 -> 0
                            1 -> progressBar.max / 2
                            2 -> progressBar.max
                            else -> 0 // Defina um valor padrão para outros casos
                        }

                        // Sair do loop após a primeira obra encontrada
                        break
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("ERROR", "Erro ao obter obras do usuário", exception)
                }
        }

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(
            titulo: String,
            imageUrl: String,
            autor: String,
            descricao: String,
            userPath: String
        ) =
            FigurinhaObra().apply {
                arguments = Bundle().apply {
                    putString(ARG_TITULO, titulo)
                    putString(ARG_IMAGE_URL, imageUrl)
                    putString(ARG_AUTOR, autor)
                    putString(ARG_DESCRICAO, descricao)
                    putString(USER_PATH, userPath)
                }
            }
    }
}
