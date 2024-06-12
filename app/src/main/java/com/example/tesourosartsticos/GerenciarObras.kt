package com.example.tesourosartsticos

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tesourosartsticos.models.AdmObrasAdapter
import com.example.tesourosartsticos.models.Obra
import com.example.tesourosartsticos.models.UserViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

private const val TAG = "TelaColecaoFragment"

class GerenciarObras : Fragment() {
    private val userViewModel: UserViewModel by viewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdmObrasAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userViewModel.userPath = it.getString("USER_PATH")
            Log.d(TAG, "User path recebido: ${userViewModel.userPath}")
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_gerenciar_obras, container, false)

        // Setup RecyclerView
        recyclerView = view.findViewById(R.id.recycleItems)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = AdmObrasAdapter(emptyList())
        recyclerView.adapter = adapter

        // Carregar e configurar as obras
        loadObras()

        return view
    }

    private fun loadObras() {
        userViewModel.userPath?.let { userPath ->
            val db = Firebase.firestore
            db.collection("Logins").document(userPath).collection("ObrasUser")
                .get()
                .addOnSuccessListener { result ->
                    val obrasList = mutableListOf<Obra>()
                    for (document in result) {
                        Log.d(TAG, "Documento: ${document.id}")
                        val nome = document.getString("titulo")
                        val imageUrl = document.getString("imageUrl")
                        val autor = document.getString("autor")
                        val descricao = document.getString("descricao")
                        if (nome != null && imageUrl != null) {
                            val obra = Obra(nome, imageUrl, autor, descricao, document.id, userPath)
                            obrasList.add(obra)
                        } else {
                            Log.w(TAG, "Obra com informações faltando: $document")
                        }
                    }
                    Log.d(TAG, "Número de obras carregadas: ${obrasList.size}")
                    // Atualizar o adaptador com novos dados
                    adapter.updateObras(obrasList)
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Erro ao obter documentos: ", exception)
                }
        }
    }
}

