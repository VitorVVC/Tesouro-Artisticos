package com.example.tesourosartsticos

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.example.tesourosartsticos.models.UserViewModel

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val TAG = "ADM VIEW"

class Administrador : Fragment() {
    private var param1: String? = null
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            userViewModel.userPath = it.getString("USER_PATH")
            Log.d(TAG, "User path recebido ADM View: ${userViewModel.userPath}")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_administrador, container, false)

        val btnAdicionarFigurinha = view.findViewById<Button>(R.id.btnAdicionarFigurinha)
        val btnChamados = view.findViewById<Button>(R.id.btnChamados);
        val btnRanking = view.findViewById<Button>(R.id.btnGerenciarRank)
        val btnGerenciarObras = view.findViewById<Button>(R.id.btnGerenciarObras)

        val bundle = Bundle().apply {
            putString("USER_PATH", userViewModel.userPath)
        }
        btnRanking.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_administrador_to_gerenciar_ranking)
        }

        btnAdicionarFigurinha.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.navToAddFigurinha)
        }

        val btnVoltar = view.findViewById<Button>(R.id.btnVoltar)
        btnVoltar.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.backToSettings)
        }

        btnChamados.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_administrador_to_admSupport)
        }

        btnGerenciarObras.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_administrador_to_gerenciarObras,bundle)
        }

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Administrador().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

}