package com.example.tesourosartsticos

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.Navigation

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class Administrador : Fragment() {
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
        val view = inflater.inflate(R.layout.fragment_administrador, container, false)

        val btnAdicionarFigurinha = view.findViewById<Button>(R.id.btnAdicionarFigurinha)

        btnAdicionarFigurinha.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.navToAddFigurinha)
        }

        val btnVoltar = view.findViewById<Button>(R.id.btnVoltar)
        btnVoltar.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.backToSettings)
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