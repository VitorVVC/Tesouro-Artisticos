package com.example.tesourosartsticos

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Receber dados do Intent
        val userName = intent.getStringExtra("USER_NAME")
        val userType = intent.getStringExtra("USER_TYPE")

        // Verifique se fragmentContainerView5 está presente no layout
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
}
