package com.example.tesourosartsticos

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

//Activity do Usuário
class UserActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentContainerView5) as NavHostFragment
        val navController = navHostFragment.navController

        val bottomNavView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        bottomNavView.setupWithNavController(navController)

    }
}