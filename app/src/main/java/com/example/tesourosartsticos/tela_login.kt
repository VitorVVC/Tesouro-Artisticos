package com.example.tesourosartsticos

import android.content.Intent
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
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.example.tesourosartsticos.databinding.ActivityMainBinding
import com.google.firebase.firestore.ktx.firestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [tela_login.newInstance] factory method to
 * create an instance of this fragment.
 */
class tela_login : Fragment() {

    private lateinit var senhaLogin: EditText
    private lateinit var nomeLogin: EditText
    private lateinit var btnLogar: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_tela_login, container, false)

        // Capturando inputs
        senhaLogin = view.findViewById(R.id.senhaInput)
        nomeLogin = view.findViewById(R.id.inputName)

        // Capturando botão
        btnLogar = view.findViewById(R.id.loginButton)



        btnLogar.setOnClickListener {
            val nome = nomeLogin.text.toString()
            val senha = senhaLogin.text.toString()

            if (TextUtils.isEmpty(nome) || TextUtils.isEmpty(senha)) {
                Toast.makeText(requireContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Firebase.firestore.collection("Logins").document("A4DHpnfLZ9PIZzM7zuqB").get()
                .addOnSuccessListener { documentSnapshot ->
                    val loginCorreto = documentSnapshot.getString("nome")
                    val senhaCorreta = documentSnapshot.getString("senha")

                    if (nome == loginCorreto && senha == senhaCorreta) {
                        Toast.makeText(requireContext(), "Login bem-sucedido", Toast.LENGTH_SHORT).show()
                        // Aqui você pode navegar para a próxima tela ou executar outras ações
                        Navigation.findNavController(view).navigate(R.id.sucessoLogin)

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

}
