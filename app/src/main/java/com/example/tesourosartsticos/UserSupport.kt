package com.example.tesourosartsticos

import android.content.Intent
import android.net.Uri
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
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class UserSupport : Fragment() {
    private var ARG_PARAM1: String? = null
    private var ARG_PARAM2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            ARG_PARAM1 = it.getString(ARG_PARAM1)
            ARG_PARAM2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_user_support, container, false)
        val nomeUser = view.findViewById<EditText>(R.id.nameText)
        val titulo = view.findViewById<EditText>(R.id.titulo)
        val descricao = view.findViewById<EditText>(R.id.descricao)
        val btnWpp = view.findViewById<Button>(R.id.btnWpp)
        val btnConcluido = view.findViewById<Button>(R.id.btnConcluido)

        btnConcluido.setOnClickListener {
            val nomeUserTxT = nomeUser.text.toString()
            val tituloTxT = titulo.text.toString()
            val descricaoTxT = descricao.text.toString()

            if (TextUtils.isEmpty(tituloTxT) || TextUtils.isEmpty(nomeUserTxT) || TextUtils.isEmpty(descricaoTxT)) {
                Toast.makeText(requireContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val sup = hashMapOf(
                "nome" to nomeUserTxT,
                "titulo" to tituloTxT,
                "descricao" to descricaoTxT,
                "dataCriacao" to FieldValue.serverTimestamp()
            )

            // Adicionar pedido de suporte ao Firestore
            val db = Firebase.firestore
            db.collection("Suporte").add(sup)
                .addOnSuccessListener { documentReference ->
                    val pathGerado = documentReference.id // Captura o path gerado
                    Navigation.findNavController(view).navigate(R.id.backToUserSettings)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Erro adicionar pedido de suporte: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        btnWpp.setOnClickListener {
            val phoneNumber = "5541999371713"
            val message = "Olá, preciso de suporte para: ${titulo.text}"

            val uri = Uri.parse("https://api.whatsapp.com/send?phone=$phoneNumber&text=$message")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.setPackage("com.whatsapp")

            if (intent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(intent)
            } else {
                Toast.makeText(requireContext(), "WhatsApp não instalado", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            UserSupport().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
