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
    val nome: String,
    val imageUrl: String,
    val autor: String?,
    val descricao: String?,
    var desbloqueada: Boolean = false,
    var quizCompleto: Boolean = false,
    var progresso: Int = 0
)

class ObrasAdapter(private val obraList: List<Obra>) :
    RecyclerView.Adapter<ObrasAdapter.ViewHolderClass>() {

    // Referência do adapter
    private lateinit var adapter: ObrasAdapter

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
        private val progressoProgressBar: ProgressBar = itemView.findViewById(R.id.progressBar)

        fun bind(obra: Obra) {
            nomeTextView.text = obra.nome
            Glide.with(itemView.context)
                .load(obra.imageUrl)
                .override(100, 100)
                .into(imagemImageView)

            // Define o progresso da ProgressBar conforme o progresso da figurinha
            progressoProgressBar.max = 2 // Máximo progresso da ProgressBar
            progressoProgressBar.progress = obra.progresso

            itemView.setOnClickListener {
                val bundle = Bundle().apply {
                    putString("nome", obra.nome)
                    putString("imageUrl", obra.imageUrl)
                    putString("autor", obra.autor)
                    putString("descricao", obra.descricao)
                }
                itemView.findNavController()
                    .navigate(R.id.fragment_figurinha_obra, bundle)
            }
        }
    }

    // Método para definir a referência do adapter
    fun setAdapter(adapter: ObrasAdapter) {
        this.adapter = adapter
    }

    // Lógica para desbloquear uma figurinha e atualizar seu progresso
    fun desbloquearFigurinha(obra: Obra) {
        obra.desbloqueada = true
        obra.progresso = 1
        adapter.notifyDataSetChanged() // Atualize o RecyclerView
    }

    fun completarQuiz(obra: Obra) {
        obra.quizCompleto = true
        obra.progresso = 2
        adapter.notifyDataSetChanged() // Atualize o RecyclerView
    }
}
