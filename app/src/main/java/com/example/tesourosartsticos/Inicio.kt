package com.example.tesourosartsticos

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_inicio, container, false)

        val firebasePathAdm = "A4DHpnfLZ9PIZzM7zuqB"

        // Capturando inputs
        val senhaLogin = view.findViewById<EditText>(R.id.senhaInput)
        val nomeLogin = view.findViewById<EditText>(R.id.inputName)

        // Capturando botão
        val btnLogar = view.findViewById<Button>(R.id.loginButton)

        btnLogar.setOnClickListener {
            val nome = nomeLogin.text.toString()
            val senha = senhaLogin.text.toString()

            if (TextUtils.isEmpty(nome) || TextUtils.isEmpty(senha)) {
                Toast.makeText(requireContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Firebase.firestore.collection("Logins")
                .get()
                .addOnSuccessListener { querySnapshot ->
                    var isAdmin = false
                    var loginEncontrado = false
                    var userId: String? = null

                    for (document in querySnapshot.documents) {
                        val loginCorreto = document.getString("nome")
                        val senhaCorreta = document.getString("senha")

                        if (nome == loginCorreto && senha == senhaCorreta) {
                            loginEncontrado = true
                            userId = document.id
                            if (userId == firebasePathAdm) {
                                isAdmin = true
                            }
                            break
                        }
                    }
                    if (loginEncontrado) {
                        saveUserPath(userId!!)
                        val intent = if (isAdmin) {
                            Toast.makeText(requireContext(), "Login como administrador bem-sucedido", Toast.LENGTH_SHORT).show()
                            Intent(requireContext(), MainActivity::class.java)
                        } else {
                            Toast.makeText(requireContext(), "Login como usuário bem-sucedido", Toast.LENGTH_SHORT).show()
                            Intent(requireContext(), UserActivity::class.java)
                        }
                        startActivity(intent)
                        requireActivity().finish()
                    } else {
                        Toast.makeText(requireContext(), "Nome de usuário ou senha incorretos", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Erro ao realizar login. Tente novamente mais tarde.", Toast.LENGTH_SHORT).show()
                }
        }

        return view
    }

    private fun saveUserPath(userId: String) {
        val db = Firebase.firestore
        val userPath = mapOf(
            "userPath" to userId
        )

        db.collection("Logins").document("currentUser")
            .set(userPath)
            .addOnSuccessListener {
                // Path do usuário salvo com sucesso
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Erro ao salvar o caminho do usuário: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
