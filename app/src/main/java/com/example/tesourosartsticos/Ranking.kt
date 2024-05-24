package com.example.tesourosartsticos

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Ranking : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_ranking, container, false)
        val btnVoltar = view.findViewById<Button>(R.id.btnVoltar)
        val topUmTextView = view.findViewById<TextView>(R.id.topUm)
        val topDoisTextView = view.findViewById<TextView>(R.id.topDois)
        val topTresTextView = view.findViewById<TextView>(R.id.topTres)
        val topQuatroTextView = view.findViewById<TextView>(R.id.topQuatro)
        val topCincoTextView = view.findViewById<TextView>(R.id.topCinco)

        btnVoltar.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.backToHome)
        }

        val db = Firebase.firestore
        db.collection("Logins")
            .whereEqualTo("albumCompleto", true)
            .get()
            .addOnSuccessListener { result ->
                val topUsers = result.documents.take(5)

                val rankingTexts = topUsers.mapIndexed { index, user ->
                    "Pessoa ${index + 1}: ${user.getString("nome") ?: "Unknown"}"
                }

                if (rankingTexts.size >= 5) {
                    topUmTextView.text = rankingTexts[0]
                    topDoisTextView.text = rankingTexts[1]
                    topTresTextView.text = rankingTexts[2]
                    topQuatroTextView.text = rankingTexts[3]
                    topCincoTextView.text = rankingTexts[4]
                } else {
                    // Se houver menos de cinco usuários no ranking, apenas defina o texto disponível
                    for (i in rankingTexts.indices) {
                        when (i) {
                            0 -> topUmTextView.text = rankingTexts[i]
                            1 -> topDoisTextView.text = rankingTexts[i]
                            2 -> topTresTextView.text = rankingTexts[i]
                            3 -> topQuatroTextView.text = rankingTexts[i]
                            4 -> topCincoTextView.text = rankingTexts[i]
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.w("Ranking", "Error getting documents: ", exception)
            }

        return view
    }
}
