// Perfil Fragment
package com.example.tesourosartsticos

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.Navigation

private const val ARG_USER_NAME = "userName"
private const val ARG_USER_TYPE = "userType"

class Perfil : Fragment() {
    private var userName: String? = null
    private var userType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userName = it.getString(ARG_USER_NAME)
            userType = it.getString(ARG_USER_TYPE)
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_perfil, container, false)

        // Exibir informações do usuário
        val userNameTextView = view.findViewById<TextView>(R.id.userName);
        val userTypeTextView = view.findViewById<TextView>(R.id.userType);
        userNameTextView.text = userName
        userTypeTextView.text = userType

        val btnVoltar = view.findViewById<ImageView>(R.id.imageView6)
        btnVoltar.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.backToHome)
        }

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(userName: String?, userType: String?) =
            Perfil().apply {
                arguments = Bundle().apply {
                    putString(ARG_USER_NAME, userName)
                    putString(ARG_USER_TYPE, userType)
                }
            }
    }
}
