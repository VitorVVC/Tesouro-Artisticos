package com.example.tesourosartsticos

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.example.tesourosartsticos.models.Obra

private const val ARG_NOME = "nome"
private const val ARG_IMAGE_URL = "imageUrl"
private const val ARG_AUTOR = "autor"
private const val ARG_DESCRICAO = "descricao"

class FigurinhaObra : Fragment() {
    private var nome: String? = null
    private var imageUrl: String? = null
    private var autor: String? = null
    private var descricao: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            nome = it.getString(ARG_NOME)
            imageUrl = it.getString(ARG_IMAGE_URL)
            autor = it.getString(ARG_AUTOR)
            descricao = it.getString(ARG_DESCRICAO)
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_figurinha_obra, container, false)

        val nomeTextView: TextView = view.findViewById(R.id.headerObra)
        val imagemImageView: ImageView = view.findViewById(R.id.obraImage)
        val autorTextView: TextView = view.findViewById(R.id.autor)
        val detalhesTextView: TextView = view.findViewById(R.id.descricao)
        val bordaImage: Button = view.findViewById(R.id.borda)

        nomeTextView.text = nome
        autorTextView.text = autor
        detalhesTextView.text = descricao

        Glide.with(this)
            .load(imageUrl)
            .override(200, 200)
            .into(imagemImageView)

        val btnVoltar = view.findViewById<Button>(R.id.btnVoltar)
        btnVoltar.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.colecao)
        }
        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(nome: String, imageUrl: String, autor: String, detalhes: String) =
            FigurinhaObra().apply {
                arguments = Bundle().apply {
                    putString(ARG_NOME, nome)
                    putString(ARG_IMAGE_URL, imageUrl)
                    putString(ARG_AUTOR, autor)
                    putString(ARG_DESCRICAO, detalhes)
                }
            }
    }



}
