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

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [UserSupport.newInstance] factory method to
 * create an instance of this fragment.
 */
class UserSupport : Fragment() {
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
        val view = inflater.inflate(R.layout.fragment_user_support, container, false)
        val nomeUser = view.findViewById<EditText>(R.id.nameText);
        val titulo = view.findViewById<EditText>(R.id.titulo);
        val descricao = view.findViewById<EditText>(R.id.descricao);
        val btnWpp = view.findViewById<Button>(R.id.btnWpp);
        val btnConcluido = view.findViewById<Button>(R.id.btnConcluido);

        val nomeUserTxT = nomeUser.text.toString();
        val tituloTxT = titulo.text.toString();
        val descricaoTxT = descricao.text.toString();

        btnConcluido.setOnClickListener {
            if (TextUtils.isEmpty(tituloTxT) || TextUtils.isEmpty(nomeUserTxT) || TextUtils.isEmpty(
                    descricaoTxT
                )
            ) {
                Toast.makeText(
                    requireContext(),
                    "Preencha todos os campos e selecione uma imagem",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            Navigation.findNavController(view).navigate(R.id.backToUserSettings)
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

        val sup = mapOf(
            "nome" to nomeUserTxT,
            "titulo" to tituloTxT,
            "descricao" to descricaoTxT,
            "dataCriacao" to FieldValue.serverTimestamp()
        )

        // Adicionar obra ao Firestore
        val db = Firebase.firestore
        db.collection("Suporte").add(sup)
            .addOnSuccessListener { documentReference ->
                val pathGerado = documentReference.id // Captura o path gerado
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Erro adicionar pedido de suporte: ${e.message}", Toast.LENGTH_SHORT).show()
            }

        return view;
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment UserSupport.
         */
        // TODO: Rename and change types and number of parameters
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