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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tesourosartsticos.R
import com.example.tesourosartsticos.models.Obra
import com.example.tesourosartsticos.models.ObrasAdapter
import com.example.tesourosartsticos.models.UserViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

private const val TAG = "GerenciarObrasFragment"

class GerenciarObras : Fragment() {
    private val userViewModel: UserViewModel by viewModels()
    private var isOrdenadoPorNome = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userViewModel.userPath = it.getString("USER_PATH")
            Log.d(TAG, "User path recebido em GERENCIAR OBRAS!: ${userViewModel.userPath}")
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_gerenciar_obras, container, false)

        // Setup RecyclerView
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycleItems)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val btnOrdenar = view.findViewById<Button>(R.id.btnOrdenar)
        btnOrdenar.setOnClickListener {
            if (isOrdenadoPorNome) {
                loadObras()
            } else {
                // Ordenar obras por nome em ordem alfabética
                userViewModel.userPath?.let { userPath ->
                    val db = Firebase.firestore
                    db.collection("Logins").document(userPath).collection("ObrasUser")
                        .orderBy("titulo")
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
                                    val obra =
                                        Obra(
                                            nome,
                                            imageUrl,
                                            autor,
                                            descricao,
                                            document.id,
                                            userPath
                                        )
                                    obrasList.add(obra)
                                } else {
                                    Log.w(TAG, "Obra com informações faltando: $document")
                                }
                            }
                            Log.d(TAG, "Número de obras carregadas: ${obrasList.size}")
                            // Configurar o adaptador da RecyclerView
                            setupRecyclerView(obrasList)
                        }
                        .addOnFailureListener { exception ->
                            Log.e(TAG, "Erro ao obter documentos: ", exception)
                        }
                }
            }
            // Atualizar o estado de ordenação
            isOrdenadoPorNome = !isOrdenadoPorNome
        }

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
                    // Configurar o adaptador da RecyclerView
                    setupRecyclerView(obrasList)
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Erro ao obter documentos: ", exception)
                }
        }
    }

    private fun setupRecyclerView(obrasList: List<Obra>) {
        val adapter = ObrasAdapter(obrasList)
        val recyclerView = requireView().findViewById<RecyclerView>(R.id.recycleItems)
        recyclerView.adapter = adapter
    }

    companion object {
        @JvmStatic
        fun newInstance(userP: String) =
            GerenciarObras().apply {
                arguments = Bundle().apply {
                    putString("USER_PATH", userP)
                }
            }
    }
}
