package com.example.tesourosartsticos

import android.annotation.SuppressLint
import android.content.Intent
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
private const val TAG = "Settings"


class Settings : Fragment() {
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userViewModel.userPath = it.getString("USER_PATH")
            Log.d(TAG, "User path recebido SETTINGS: ${userViewModel.userPath}")

        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        val btnSuporte = view.findViewById<Button>(R.id.btnSuporte);
        val btnAdm = view.findViewById<Button>(R.id.btnAdm);
        val btnTema = view.findViewById<Button>(R.id.btnTema);
        val btnSobre = view.findViewById<Button>(R.id.btnSobre);
        val btnLogout = view.findViewById<Button>(R.id.btnLogout)


        val colorChange = false
        val bundle = Bundle().apply {
            putString("USER_PATH", userViewModel.userPath)
        }
        btnAdm.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.navigateToAdmin, bundle)

        }

        btnSobre.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.navToSobre)
        }

        btnLogout.setOnClickListener {
            val intent = Intent(activity, InicioActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            activity?.finish()
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
         * @return A new instance of fragment Settings.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Settings().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}