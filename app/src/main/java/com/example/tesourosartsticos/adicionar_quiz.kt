package com.example.tesourosartsticos

import android.content.Intent
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

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [adicionar_quiz.newInstance] factory method to
 * create an instance of this fragment.
 */
class adicionar_quiz : Fragment() {
    // TODO: Rename and change types of parameters
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
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_adicionar_quiz, container, false)

        val btnVoltar = view.findViewById<Button>(R.id.btnVoltar)
        btnVoltar.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.backToAdm)
        }
        val perguntaQuiz = view.findViewById<EditText>(R.id.perguntaQuiz);

        val opCorreta = view.findViewById<EditText>(R.id.opCorreta);
        val opErradaUm = view.findViewById<EditText>(R.id.opErradaUm);
        val opErradaDois = view.findViewById<EditText>(R.id.opErradaDois);
        val opErradaTres = view.findViewById<EditText>(R.id.opErradaTres);

        val btnCriarQuiz = view.findViewById<Button>(R.id.btnCriar);

        btnCriarQuiz.setOnClickListener {
            val perguntaQuizText = perguntaQuiz.text.toString();
            val opCorretaText = opCorreta.text.toString();
            val opErradaUmText = opErradaUm.text.toString();
            val opErradaDoisText = opErradaDois.text.toString();
            val opErradaTresText = opErradaTres.text.toString();

            if (TextUtils.isEmpty(perguntaQuizText) || TextUtils.isEmpty(opCorretaText) || TextUtils.isEmpty(
                    opErradaUmText
                ) || TextUtils.isEmpty(opErradaDoisText) || TextUtils.isEmpty(opErradaTresText)
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
                            "perguntaQuiz" to perguntaQuizText,
                            "opCorreta" to opCorretaText,
                            "opErradaUm" to opErradaUmText,
                            "opErradaDois" to opErradaDoisText,
                            "opErradaTres" to opErradaTresText
                        )
                    )

                    Toast.makeText(requireContext(), "Obra criada com sucesso", Toast.LENGTH_SHORT)
                        .show()
                    Navigation.findNavController(view).navigate(R.id.backToAdm);
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        requireContext(),
                        "Erro ao criar Obra.",
                        Toast.LENGTH_SHORT
                    ).show()
                }


        }

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment adicionar_figurinha.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            adicionar_quiz().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}