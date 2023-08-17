package com.example.todoapp.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.animation.AccelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.example.todoapp.databinding.ActivitySplashScreenBinding

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding
    private val splashTimeout: Long = 200

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val logoImageView = binding.containerScreen
        logoImageView.alpha = 0f // Inicialmente torna o logo transparente

        Handler().postDelayed({
            // Animação de aparecimento gradual do logo
            logoImageView.animate()
                .alpha(1f)
                .setDuration(5000) // Duração da animação em milissegundos
                .setInterpolator(AccelerateInterpolator()) // Interpolador para suavizar a animação
                .withEndAction {
                    val mainIntent = Intent(this, LoginActivity::class.java)
                    startActivity(mainIntent)
                    finish()
                }
                .start()
        }, splashTimeout)
    }
}
