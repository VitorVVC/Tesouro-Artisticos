package com.example.tesourosartsticos

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.Navigation
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.UUID

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class adicionar_figurinha : Fragment() {
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
        val view = inflater.inflate(R.layout.fragment_adicionar_figurinha, container, false)

        val btnProximo = view.findViewById<Button>(R.id.button6)

        btnProximo.setOnClickListener {
            val titulo = view.findViewById<EditText>(R.id.editTitle)
            val autor = view.findViewById<EditText>(R.id.editAuthor)
            val desc = view.findViewById<EditText>(R.id.editDescription)

            val tituloTxT = titulo.text.toString()
            val autorTxT = autor.text.toString()
            val descTxT = desc.text.toString()

            val myUuid = UUID.randomUUID()
            val myUuidAsString = myUuid.toString()

            if (TextUtils.isEmpty(tituloTxT) || TextUtils.isEmpty(autorTxT) || TextUtils.isEmpty(descTxT)) {
                Toast.makeText(
                    requireContext(),
                    "Preencha todos os campos corretamente",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val obra = mapOf(
                "obraId" to myUuidAsString,
                "titulo" to tituloTxT,
                "autor" to autorTxT,
                "descricao" to descTxT,
                "dataCriacao" to FieldValue.serverTimestamp()  // Adiciona a data de criação
            )

            // Adicionar obra ao Firestore
            val db = Firebase.firestore
            db.collection("Obras").add(obra)
                .addOnSuccessListener { documentReference ->
                    val pathGerado = documentReference.id // Captura o path gerado

                    Toast.makeText(requireContext(), "Figurinha criada com sucesso", Toast.LENGTH_SHORT).show()

                    // Passar o path para o fragmento adicionar_quiz
                    val bundle = Bundle()
                    bundle.putString("pathObra", pathGerado)
                    Navigation.findNavController(view).navigate(R.id.navToAddQuiz, bundle)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Erro ao criar figurinha: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            adicionar_figurinha().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
