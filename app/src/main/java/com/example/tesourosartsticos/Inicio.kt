package com.example.tesourosartsticos;

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tesourosartsticos.MainActivity
import com.example.tesourosartsticos.R
import com.example.tesourosartsticos.UserActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class InicioActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio)

        val firebasePathAdm = "A4DHpnfLZ9PIZzM7zuqB"

        // Capturando inputs
        val senhaLogin = findViewById<EditText>(R.id.senhaInput)
        val nomeLogin = findViewById<EditText>(R.id.inputName)

        // Capturando botão
        val btnLogar = findViewById<Button>(R.id.loginButton)

        btnLogar.setOnClickListener {
            val nome = nomeLogin.text.toString()
            val senha = senhaLogin.text.toString()

            if (TextUtils.isEmpty(nome) || TextUtils.isEmpty(senha)) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Firebase.firestore.collection("Logins")
                .get()
                .addOnSuccessListener { querySnapshot ->
                    var isAdmin = false
                    var loginEncontrado = false
                    var userId: String? = null

                    for (document in querySnapshot.documents) {
                        val loginCorreto = document.getString("nome")
                        val senhaCorreta = document.getString("senha")

                        if (nome == loginCorreto && senha == senhaCorreta) {
                            loginEncontrado = true
                            userId = document.id
                            Log.d("USER_PATH", "Valor de userPath: $userId")
                            if (userId == firebasePathAdm) {
                                isAdmin = true
                            }
                            break
                        }
                    }
                    if (loginEncontrado) {
                        val intent: Intent
                        if (isAdmin) {
                            Toast.makeText(this, "Login como administrador bem-sucedido", Toast.LENGTH_SHORT).show()
                            intent = Intent(this, MainActivity::class.java)
                        } else {
                            Toast.makeText(this, "Login como usuário bem-sucedido", Toast.LENGTH_SHORT).show()
                            intent = Intent(this, UserActivity::class.java)
                        }
                        intent.putExtra("USER_PATH", userId)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "Nome de usuário ou senha incorretos", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Erro ao realizar login. Tente novamente mais tarde.", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
