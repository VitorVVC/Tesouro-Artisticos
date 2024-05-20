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
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.UUID;

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [adicionar_figurinha.newInstance] factory method to
 * create an instance of this fragment.
 */
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
            val titulo = view.findViewById<EditText>(R.id.editTitle);
            val autor = view.findViewById<EditText>(R.id.editAuthor);
            val desc = view.findViewById<EditText>(R.id.editDescription);

            val tituloTxT = titulo.text.toString();
            val autorTxT = autor.text.toString();
            val descTxT = desc.text.toString();

            val myUuid = UUID.randomUUID()
            val myUuidAsString = myUuid.toString()

            if (TextUtils.isEmpty(tituloTxT) || TextUtils.isEmpty(autorTxT) || TextUtils.isEmpty(
                    descTxT
                )
            ) {
                Toast.makeText(
                    requireContext(),
                    "Preencha todos os campos corretamente",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            Firebase.firestore.collection("Obras").document("Bngg2db56a8bHhqFTg1j").get()
                .addOnSuccessListener { documentSnapshot ->
                    Firebase.firestore.collection("Obras").add(
                        mapOf(
                            "id" to myUuidAsString,
                            "titulo" to tituloTxT,
                            "autor" to autorTxT,
                            "descricao" to descTxT,
                        )
                    )

                    Toast.makeText(requireContext(), "Figurinha criada com sucesso", Toast.LENGTH_SHORT)
                        .show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        requireContext(),
                        "Erro ao criar Figurinha.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            Navigation.findNavController(view).navigate(R.id.navToAddQuiz)
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