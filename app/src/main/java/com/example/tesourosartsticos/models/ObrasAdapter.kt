package com.example.tesourosartsticos.models

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tesourosartsticos.R

data class Obra(
    val titulo: String,
    val imageUrl: String,
    val autor: String?,
    val descricao: String?,
)

class ObrasAdapter(private val obraList: List<Obra>) :
    RecyclerView.Adapter<ObrasAdapter.ViewHolderClass>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_obra, parent, false)
        return ViewHolderClass(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val currentObra = obraList[position]
        holder.bind(currentObra)
    }

    override fun getItemCount() = obraList.size

    inner class ViewHolderClass(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nomeTextView: TextView = itemView.findViewById(R.id.nomeTextView)
        private val imagemImageView: ImageView = itemView.findViewById(R.id.imagemImageView)

        fun bind(obra: Obra) {
            nomeTextView.text = obra.titulo
            Glide.with(itemView.context)
                .load(obra.imageUrl)
                .override(100, 100)
                .into(imagemImageView)

            itemView.setOnClickListener {
                val bundle = Bundle().apply {
                    putString("titulo", obra.titulo)
                    putString("imageUrl", obra.imageUrl)
                    putString("autor", obra.autor)
                    putString("descricao", obra.descricao)
                }
                itemView.findNavController()
                    .navigate(R.id.fragment_figurinha_obra, bundle)
            }
        }
    }

}
