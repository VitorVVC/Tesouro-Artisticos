package com.example.tesourosartsticos

import android.content.Intent
import android.os.Bundle
<<<<<<< HEAD
import androidx.appcompat.app.AppCompatActivity

=======
import androidx.fragment.app.Fragment
import com.example.tesourosartsticos.databinding.ActivityMainBinding
>>>>>>> 978ad7b484612c6db4e65bc9cf4a1cefa43c3144

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
<<<<<<< HEAD
        setContentView(R.layout.tela_login)
        // Intent básica em um botão. Transição de tela
//        val intent: Intent = Intent(
//            this,
//            tela_colecao::class.java
//        )
//        startActivity(intent)
=======
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(Home())

        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.home -> replaceFragment(Home())
                R.id.album -> replaceFragment(Album())
                R.id.camera -> replaceFragment(Camera())
                R.id.settings -> replaceFragment(Settings())

                else -> {

                }
            }
            true
        }
    }

    private fun replaceFragment(fragment : Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout,fragment)
        fragmentTransaction.commit()
>>>>>>> 978ad7b484612c6db4e65bc9cf4a1cefa43c3144
    }


}