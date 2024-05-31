package com.example.tesourosartsticos

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.tesourosartsticos.R
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class quiz : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_quiz, container, false)

        val btnEnviar: Button = view.findViewById(R.id.btnEnviarQuiz)
        val radioGroup: RadioGroup = view.findViewById(R.id.radioGroup)
        val opCorreta: RadioButton = view.findViewById(R.id.opCorreta)
        val opErradaUm: RadioButton = view.findViewById(R.id.opErradaUm)
        val opErradaDois: RadioButton = view.findViewById(R.id.opErradaDois)
        val opErradaTres: RadioButton = view.findViewById(R.id.opErradaTres)

        btnEnviar.setOnClickListener {
            val selectedRadioButtonId = radioGroup.checkedRadioButtonId
            if (selectedRadioButtonId == -1) {
                Toast.makeText(
                    context,
                    "Selecione uma resposta antes de enviar o quiz.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val selectedRadioButton = view.findViewById<RadioButton>(selectedRadioButtonId)
            if (selectedRadioButton.id == opCorreta.id) {
                // Resposta correta
                Toast.makeText(context, "Resposta correta!", Toast.LENGTH_SHORT).show()
                // TODO -> Modificar o progresso e o completeQuiz no banco + Redirecionar para a tela da figurinha do quiz.
            } else {
                // Resposta incorreta
                Toast.makeText(context, "Resposta incorreta. Tente novamente.", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        // Lógica do quiz
        // Quando o quiz é completado com sucesso:
        val obraPath = arguments?.getString("obraPath")
        obraPath?.let { path ->
            val db = Firebase.firestore
            db.collection("obras").document(path)
                .get()
                .addOnSuccessListener { document ->
                    val progresso = document.getLong("progresso") ?: 0
                    val novoProgresso = when (progresso) {
                        0L -> 1
                        1L -> 2
                        else -> progresso // Se o progresso for 2 ou outro valor, mantém o mesmo valor
                    }
                    db.collection("obras").document(path)
                        .update(
                            mapOf(
                                "progresso" to novoProgresso,
                                "completeQuiz" to true
                            )
                        )
                        .addOnSuccessListener {
                            Log.d(
                                TAG,
                                "Quiz completado com sucesso. Novo progresso: $novoProgresso"
                            )
                        }
                        .addOnFailureListener { e ->
                            Log.e(TAG, "Erro ao completar o quiz", e)
                        }
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Erro ao obter a obra do banco de dados", e)
                }
        }

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            quiz().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
