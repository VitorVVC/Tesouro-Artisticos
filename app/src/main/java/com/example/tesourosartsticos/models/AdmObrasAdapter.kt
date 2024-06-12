package com.example.tesourosartsticos.models

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tesourosartsticos.R

class AdmObrasAdapter(private var obraList: List<Obra>) :
    RecyclerView.Adapter<AdmObrasAdapter.ViewHolderClass>() {

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
                    putString("obraPath", obra.documentPath) // Passando o caminho do documento
                    putString("userPath", obra.userPath) // Passando o caminho do usuário
                }
                itemView.findNavController()
                    .navigate(R.id.navigateToAdmFigurinhaObra, bundle)
            }
        }
    }

    // Método para atualizar a lista de obras
    fun updateObras(newObras: List<Obra>) {
        obraList = newObras
        notifyDataSetChanged()
    }
}
