package com.example.todoapp.ui


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.todoapp.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        varifyLogin()

    }

    private fun varifyLogin() {
        emailEditText = binding.emailEditText
        passwordEditText = binding.passwordEditText
        loginButton = binding.loginButton

        auth = FirebaseAuth.getInstance()

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            // Realizar o login com email e senha
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val intent = Intent(this,MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(
                            this, "E-mail/Senha inválidos! Por favor tente novamente", Toast.LENGTH_LONG)
                            .show()
                    }
                }
        }
    }
}
