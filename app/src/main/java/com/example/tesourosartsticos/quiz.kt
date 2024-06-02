package com.example.tesourosartsticos

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

private const val USER_PATH = "USER_PATH"
private const val COMPLETE_QUIZ = "completeQuiz"

class quiz : Fragment() {
    private var userPath: String? = null
    private var obraPath: String? = null
    private var completeQuiz: Boolean = false
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userPath = it.getString(USER_PATH)
            obraPath = it.getString("obraPath")
            completeQuiz = it.getBoolean(COMPLETE_QUIZ)
        }
        firestore = Firebase.firestore
        Log.d(TAG, "onCreate (quiz.kt): userPath=$userPath, obraPath=$obraPath, completeQuiz=$completeQuiz")
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
                Toast.makeText(context, "Resposta correta!", Toast.LENGTH_SHORT).show()
                // Atualizar Firestore com completeQuiz e progresso
                userPath?.let { userPath ->
                    obraPath?.let { obraPath ->
                        val documentReference =
                            firestore.collection("Logins/$userPath/ObrasUser").document(obraPath)

                        documentReference.update("completeQuiz", true, "progresso", 2)
                            .addOnSuccessListener {
                                Log.d("FirestoreUpdate", "Documento atualizado com sucesso")
                                Toast.makeText(
                                    context,
                                    "Quiz enviado com sucesso!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                // Redirecionar para a tela da figurinha
                                findNavController().navigate(R.id.home)
                            }
                            .addOnFailureListener { e ->
                                Log.w("FirestoreUpdate", "Erro ao atualizar o documento", e)
                                Toast.makeText(
                                    context,
                                    "Erro ao enviar o quiz.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                }
            } else {
                Toast.makeText(context, "Resposta incorreta. Tente novamente.", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(userPath: String, obraPath: String, completeQuiz: Boolean) =
            quiz().apply {
                arguments = Bundle().apply {
                    putString(USER_PATH, userPath)
                    putString("obraPath", obraPath)
                    putBoolean(COMPLETE_QUIZ, completeQuiz)
                }
            }
    }
}
