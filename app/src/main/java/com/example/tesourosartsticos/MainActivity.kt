package com.example.tesourosartsticos

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.tesourosartsticos.models.UserViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private var userPath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Receber o caminho do usuário do Intent
        userPath = intent.getStringExtra("USER_PATH")

        // Inicializar o ViewModel
        val userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        // Configurar o userPath no ViewModel
        userViewModel.userPath = userPath
        Log.d(ContentValues.TAG, "onCreate (MainActivity): userPath=$userPath")

        if (userPath == null) {
            Toast.makeText(this, "Erro ao obter caminho do usuário", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Carregar o nome do usuário e salvar em SharedPreferences
        loadUserName(userPath!!)

        // Configuração do NavHostFragment
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView5) as? NavHostFragment
                ?: throw IllegalStateException("NavHostFragment não encontrado")

        val navController = navHostFragment.navController
        val bottomNavView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavView.setupWithNavController(navController)

        bottomNavView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.colecao -> {
                    // Passar o userPath para a tela de coleção usando o ViewModel
                    userViewModel.userPath?.let { userPath ->
                        val bundle = Bundle().apply {
                            putString("USER_PATH", userPath)
                        }
                        Log.d("userPath Colecao","userPath $userPath")
                        navController.navigate(R.id.colecao, bundle)
                    }
                    true
                }
                R.id.camera -> {
                    userViewModel.userPath?.let { userPath ->
                        val bundle = Bundle().apply {
                            putString("USER_PATH", userPath)
                        }
                        Log.d("userPath camera","userPath $userPath")
                        navController.navigate(R.id.camera, bundle)
                    }
                    true
                }
                R.id.settings -> {
                    navController.navigate(R.id.settings)
                    true
                }
                R.id.home -> {
                    userViewModel.userPath?.let { userPath ->
                        val bundle = Bundle().apply {
                            putString("USER_PATH", userPath)
                        }
                        navController.navigate(R.id.home, bundle)
                    }

                    true
                }
                else -> false
            }
        }
    }

    private fun loadUserName(userPath: String) {
        val db = Firebase.firestore
        db.collection("Logins").document(userPath)
            .get()
            .addOnSuccessListener { document ->
                val userName = document.getString("nome")
                if (userName != null) {
                    val settings = getSharedPreferences("perfil", MODE_PRIVATE)
                    val prefEditor = settings.edit()
                    prefEditor.putString("user_name", userName)
                    prefEditor.apply()
                } else {
                    Toast.makeText(this, "Nome do usuário não encontrado", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao carregar dados do usuário: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
