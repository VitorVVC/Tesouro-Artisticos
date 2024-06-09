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
        Log.d(ContentValues.TAG, "onCreate ( MainActivity ): userPath=$userPath")


        if (userPath == null) {
            Toast.makeText(this, "Erro ao obter caminho do usuário", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Configuração do NavHostFragment
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView5) as? NavHostFragment
                ?: throw IllegalStateException("NavHostFragment não encontrado")


        val navController = navHostFragment.navController
        val bottomNavView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavView.setupWithNavController(navController)

        // Passar o caminho do usuário para o fragmento "perfil" quando necessário
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.perfil) {
                val settings = getSharedPreferences("perfil", MODE_PRIVATE)
                val prefEditor = settings.edit()
                prefEditor.putString("chave","valor")
                prefEditor.apply()
                Log.d("Mandando userPath", "userPath: $userPath \n destination.id: ${destination.id} \n R.id.camera: ${R.id.perfil}")
//                val perfilFragment = Perfil.newInstance(userPath!!)
//                supportFragmentManager.beginTransaction()
//                    .replace(R.id.fragmentContainerView5, perfilFragment)
//                    .addToBackStack(null)
//                    .commit()
            }
        }
        bottomNavView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.colecao -> {
                    // Passar o userPath para a tela de coleção usando o ViewModel
                    userViewModel.userPath?.let { userPath ->
                        val bundle = Bundle().apply {
                            putString("USER_PATH", userPath)
                        }
                        navController.navigate(R.id.colecao, bundle)
                    }
                    true
                }
                R.id.camera -> {
                    userViewModel.userPath?.let { userPath ->
                        val bundle = Bundle().apply {
                            putString("USER_PATH", userPath)
                        }
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
}
