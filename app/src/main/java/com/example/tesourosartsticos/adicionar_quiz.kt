package com.example.tesourosartsticos

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
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

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class adicionar_quiz : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private var pathObra: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            pathObra = it.getString("pathObra") // Receber o path passado
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_adicionar_quiz, container, false)

        val btnVoltar = view.findViewById<Button>(R.id.btnVoltar)
        btnVoltar.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.backToAdm)
        }

        val perguntaQuiz = view.findViewById<EditText>(R.id.perguntaQuiz)
        val opCorreta = view.findViewById<EditText>(R.id.opCorreta)
        val opErradaUm = view.findViewById<EditText>(R.id.opErradaUm)
        val opErradaDois = view.findViewById<EditText>(R.id.opErradaDois)
        val opErradaTres = view.findViewById<EditText>(R.id.opErradaTres)
        val btnCriarQuiz = view.findViewById<Button>(R.id.btnCriar)

        btnCriarQuiz.setOnClickListener {
            val perguntaQuizText = perguntaQuiz.text.toString()
            val opCorretaText = opCorreta.text.toString()
            val opErradaUmText = opErradaUm.text.toString()
            val opErradaDoisText = opErradaDois.text.toString()
            val opErradaTresText = opErradaTres.text.toString()

            if (TextUtils.isEmpty(perguntaQuizText) || TextUtils.isEmpty(opCorretaText) ||
                TextUtils.isEmpty(opErradaUmText) || TextUtils.isEmpty(opErradaDoisText) ||
                TextUtils.isEmpty(opErradaTresText)
            ) {
                Toast.makeText(
                    requireContext(),
                    "Preencha todos os campos corretamente",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            pathObra?.let { path ->
                Firebase.firestore.collection("Obras").document(path).get()
                    .addOnSuccessListener { documentSnapshot ->
                        val docId = documentSnapshot.getString("obraId")
                        val dataCriacao = documentSnapshot.getTimestamp("dataCriacao")

                        if (docId != null) {
                            Firebase.firestore.collection("Quiz").add(
                                mapOf(
                                    "perguntaQuiz" to perguntaQuizText,
                                    "opCorreta" to opCorretaText,
                                    "opErradaUm" to opErradaUmText,
                                    "opErradaDois" to opErradaDoisText,
                                    "opErradaTres" to opErradaTresText,
                                    "idObraOriginal" to docId,
                                    "dataCriacao" to dataCriacao  // Adiciona a data de criação do quiz
                                )
                            ).addOnSuccessListener {
                                Toast.makeText(
                                    requireContext(),
                                    "Quiz criado com sucesso",
                                    Toast.LENGTH_SHORT
                                ).show()
                                Navigation.findNavController(view).navigate(R.id.backToAdm)
                            }.addOnFailureListener { e ->
                                Toast.makeText(
                                    requireContext(),
                                    "Erro ao criar o quiz.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Erro ao encontrar obra.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(
                            requireContext(),
                            "Erro ao encontrar obra.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            } ?: run {
                Toast.makeText(
                    requireContext(),
                    "Path da obra não encontrado.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String, pathObra: String) =
            adicionar_quiz().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                    putString("pathObra", pathObra)
                }
            }
    }
}
