package com.example.tesourosartsticos

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.Navigation
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class SupportAdmFragment : Fragment() {
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
        val view = inflater.inflate(R.layout.fragment_support_adm, container, false)

        val probUmTitle = view.findViewById<TextView>(R.id.probUmTitle)
        val probUmDesc = view.findViewById<TextView>(R.id.probUmDesc)
        val probDoisTitle = view.findViewById<TextView>(R.id.probDoisTitle)
        val probDoisDesc = view.findViewById<TextView>(R.id.probDoisDesc)
        val probTresTitle = view.findViewById<TextView>(R.id.probTresTitle)
        val probTresDesc = view.findViewById<TextView>(R.id.probTresDesc)
        val probQuatroTitle = view.findViewById<TextView>(R.id.probQuatroTitle)
        val probQuatroDesc = view.findViewById<TextView>(R.id.probQuatroDesc)
        val probCincoTitle = view.findViewById<TextView>(R.id.probCincoTitle)
        val probCincoDesc = view.findViewById<TextView>(R.id.probCincoDesc)
        val btnVoltar = view.findViewById<Button>(R.id.btnVoltar)

        btnVoltar.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.backToHome)
        }

        val db = Firebase.firestore
        db.collection("Reclamacoes")
            .get()
            .addOnSuccessListener { result ->
                val topReclamacoes = result.documents.take(5)

                val reclamacoesTexts = topReclamacoes.map { reclamacao ->
                    reclamacao.getString("titulo") to reclamacao.getString("descricao")
                }

                reclamacoesTexts.forEachIndexed { index, (titulo, descricao) ->
                    when (index) {
                        0 -> {
                            probUmTitle.text = titulo
                            probUmDesc.text = descricao
                        }
                        1 -> {
                            probDoisTitle.text = titulo
                            probDoisDesc.text = descricao
                        }
                        2 -> {
                            probTresTitle.text = titulo
                            probTresDesc.text = descricao
                        }
                        3 -> {
                            probQuatroTitle.text = titulo
                            probQuatroDesc.text = descricao
                        }
                        4 -> {
                            probCincoTitle.text = titulo
                            probCincoDesc.text = descricao
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.w("SupportAdmFragment", "Error getting documents: ", exception)
            }

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SupportAdmFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
