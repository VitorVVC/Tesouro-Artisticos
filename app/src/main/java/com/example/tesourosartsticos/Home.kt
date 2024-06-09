package com.example.tesourosartsticos

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Home.newInstance] factory method to
 * create an instance of this fragment.
 */
class Home : Fragment() {
    private var userPath: String? = null
    private var Value: String = "P1SjFADs7A1jh46gnRK3"

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


        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val btnRaking = view.findViewById<Button>(R.id.btn_ranking)
        btnRaking.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.navigateToRanking)

        }

        val btnPerfil = view.findViewById<ImageButton>(R.id.imageButton)
        btnPerfil.setOnClickListener{
            Navigation.findNavController(view).navigate(R.id.navigateToPerfil)
        }

        val lastObra = view.findViewById<Button>(R.id.lastObra)
        lastObra.setOnClickListener{
            val db = Firebase.firestore
            db.collection("Logins/$userPath/ObrasUser").document(Value).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val obraTitle = document.getString("titulo") ?: "Título não encontrado"
                        val obraImageUrl = document.getString("imageUrl")
                        val obraAutor = document.getString("autor")
                        val obraDescricao = document.getString("descricao")

                        val bundle = Bundle().apply {
                            putString("titulo", obraTitle)
                            putString("imageUrl", obraImageUrl)
                            putString("autor", obraAutor)
                            putString("descricao", obraDescricao)
                            putString("userPath", userPath)
                            putString("obraPath", Value)
                        }
                        findNavController().navigate(R.id.fragment_figurinha_obra, bundle)
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(requireContext(), "Erro ao acessar o banco de dados: ${exception.message}", Toast.LENGTH_SHORT).show()
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
         * @return A new instance of fragment Home.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(userPath: String) =
            Home().apply {
                arguments = Bundle().apply {
                    putString("USER_PATH", userPath)
                }
            }
    }
}