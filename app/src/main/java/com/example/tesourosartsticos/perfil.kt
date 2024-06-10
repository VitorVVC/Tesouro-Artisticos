package com.example.tesourosartsticos

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Perfil : Fragment() {

    private var userPath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userPath = it.getString("USER_PATH")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar a view primeiro
        val view = inflater.inflate(R.layout.fragment_perfil, container, false)

        val btnVoltar = view.findViewById<Button>(R.id.btnVoltar)
        btnVoltar.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.home)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Recuperar o nome do usuário do SharedPreferences
        val settings = view.context.getSharedPreferences("perfil", AppCompatActivity.MODE_PRIVATE)
        val userName = settings.getString("user_name", "Nome não encontrado")

        // Definir o nome do usuário no TextView
        view.findViewById<TextView>(R.id.userName)?.text = userName

        // Carregar os dados do perfil do usuário do Firestore
        loadUserProfile()
    }

    private fun loadUserProfile() {
        userPath?.let { path ->
            val db = Firebase.firestore
            db.collection("Logins").document(path)
                .get()
                .addOnSuccessListener { document ->
                    val userName = document.getString("nome")
                    val userType = if (document.id == "A4DHpnfLZ9PIZzM7zuqB") "Admin" else "User"

                    // Salvar o nome do usuário no SharedPreferences
                    val settings = activity?.getSharedPreferences("perfil", Context.MODE_PRIVATE)
                    val prefEditor = settings?.edit()
                    prefEditor?.putString("user_name", userName)
                    prefEditor?.apply()

                    // Definir os valores no TextView
                    view?.findViewById<TextView>(R.id.userName)?.text = userName
                    view?.findViewById<TextView>(R.id.userType)?.text = userType
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Erro ao carregar dados do usuário: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(userPath: String) = Perfil().apply {
            arguments = Bundle().apply {
                putString("USER_PATH", userPath)
            }
        }
    }
}
