package com.example.tesourosartsticos

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
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
        loadUserProfile()
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_perfil, container, false)

        val btnVoltar = view.findViewById<ImageView>(R.id.imageView6)
        btnVoltar.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.backToHome)
        }

        return view
    }

    private fun loadUserProfile() {
        if (userPath != null) {
            val db = Firebase.firestore
            db.collection("Logins").document(userPath!!)
                .get()
                .addOnSuccessListener { document ->
                    val userName = document.getString("nome")
                    val userType = if (document.id == "A4DHpnfLZ9PIZzM7zuqB") "Admin" else "User"

                    val userNameTextView = view?.findViewById<TextView>(R.id.userName)
                    val userTypeTextView = view?.findViewById<TextView>(R.id.userType)
                    userNameTextView?.text = userName
                    userTypeTextView?.text = userType
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
