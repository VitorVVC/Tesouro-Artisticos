package com.example.tesourosartsticos

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private var userName: String? = null
    private var userType: String? = null
    private var userPath: String? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Receber o caminho do usuário do Intent
        userPath = intent.getStringExtra("USER_PATH")

        if (userPath != null) {
            // Buscar dados do usuário no Firestore
            fetchUserData(userPath!!)
        } else {
            Toast.makeText(this, "Erro ao obter caminho do usuário", Toast.LENGTH_SHORT).show()
        }

        // Configuração do NavHostFragment
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView5) as? NavHostFragment
            ?: throw IllegalStateException("NavHostFragment não encontrado")

        val navController = navHostFragment.navController

        val bottomNavView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavView.setupWithNavController(navController)

        // Passar os dados para os fragmentos quando necessário
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.perfil) {
                val bundle = Bundle().apply {
                    putString("USER_NAME", userName)
                    putString("USER_TYPE", userType)
                }
                navController.navigate(destination.id, bundle)
            }
        }
    }

    private fun fetchUserData(userPath: String) {
        val db = Firebase.firestore
        db.collection("Logins").document(userPath).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    userName = document.getString("nome")
                    userType = if (document.id == "A4DHpnfLZ9PIZzM7zuqB") "Admin" else "User"
                } else {
                    Toast.makeText(this, "Erro ao buscar dados do usuário", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao buscar dados do usuário: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
