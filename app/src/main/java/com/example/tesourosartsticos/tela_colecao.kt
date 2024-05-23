package com.example.tesourosartsticos

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tesourosartsticos.models.Obra
import com.example.tesourosartsticos.models.ObrasAdapter
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val TAG = "TelaColecaoFragment"

class tela_colecao : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

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
        val view = inflater.inflate(R.layout.fragment_tela_colecao, container, false)

        val btnMod = view.findViewById<Button>(R.id.button)
        btnMod.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.navigateToModificarCarta)
        }

        val db = Firebase.firestore
        db.collection("Obras")
            .get()
            .addOnSuccessListener { result ->
                val obrasList = mutableListOf<Obra>()
                for (document in result) {
                    val nome = document.getString("titulo")
                    val imageUrl = document.getString("imageUrl")
                    val autor = document.getString("autor")
                    val detalhes = document.getString("detalhes")
                    if (nome != null && imageUrl != null) {
                        val obra = Obra(nome, imageUrl, autor, detalhes)
                        obrasList.add(obra)
                    }
                }
                setupRecyclerView(view, obrasList)
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error getting documents: ", exception)
            }

        return view
    }

    private fun setupRecyclerView(view: View, obrasList: List<Obra>) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycleItems)
        val adapter = ObrasAdapter(obrasList)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            tela_colecao().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
