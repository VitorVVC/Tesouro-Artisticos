package com.example.tesourosartsticos

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.tesourosartsticos.databinding.ActivityMainBinding
import com.example.tesourosartsticos.databinding.TelaLoginBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tela_login)


// Intent básica em um botão. Transição de tela
//        val intent: Intent = Intent(
//            this,
//            tela_colecao::class.java
//        )
//        startActivity(intent)

        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
        replaceFragment(tela_login());

        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
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

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }


}