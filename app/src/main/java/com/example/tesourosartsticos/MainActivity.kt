package com.example.tesourosartsticos

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private var userPath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Receber o caminho do usuário do Intent
        userPath = intent.getStringExtra("USER_PATH")

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
                val perfilFragment = Perfil.newInstance(userPath!!)
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView5, perfilFragment)
                    .addToBackStack(null)
                    .commit()
            }
        }
    }
}
