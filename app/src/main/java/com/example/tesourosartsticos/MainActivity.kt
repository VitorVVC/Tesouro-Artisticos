package com.example.tesourosartsticos

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.tesourosartsticos.databinding.ActivityMainBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import android.util.Log
import android.view.View


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
//        replaceFragment(Home())

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
        // Persitencia de dados LOGIN \\
        // Capturando inputs
        var nomeLogin = findViewById<EditText>(R.id.senhaInput);
        var senhaLogin = findViewById<EditText>(R.id.inputName);

        var errorTextView = findViewById<TextView>(R.id.errorTextView);

        // Capturando botão
        var btnLogar = findViewById<Button>(R.id.loginButton);

        btnLogar.setOnClickListener {
            val nome = nomeLogin.text.toString()
            val senha = senhaLogin.text.toString()

            Firebase.firestore.collection("Logins").document(nome).get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val senhaCorreta = documentSnapshot.getString("senhaTesouro")

                        if (senha == senhaCorreta) {
                            // Senha correta, redirecionar para a Home
                            replaceFragment(Home())
                        } else {
                            // Senha incorreta, mostrar mensagem de erro
                            errorTextView.apply {
                                text = "Senha incorreta. Por favor, verifique suas credenciais."
                                visibility = View.VISIBLE
                            }
                        }
                    } else {
                        // Documento não encontrado para o nome de usuário fornecido
                        errorTextView.apply {
                            text = "Usuário não encontrado. Por favor, verifique suas credenciais."
                            visibility = View.VISIBLE
                        }
                    }
                }.addOnFailureListener { exception ->
                // Ocorreu algum erro ao buscar o documento
                Log.e("MainActivity", "Erro ao fazer login: $exception")
                // Mostrar mensagem de erro genérica
                errorTextView.apply {
                    text =
                        "Erro ao fazer login. Por favor, tente novamente mais tarde." // BUG FIX: Ao tentar logar, está caindo aqui
                    visibility = View.VISIBLE
                }
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }


}