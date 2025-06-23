package com.alquimia.ui.main

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.alquimia.R
import com.alquimia.data.remote.TokenManager
import com.alquimia.ui.login.LoginActivity
import com.alquimia.ui.profile.UserProfileActivity // Importar a nova Activity de perfil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Você precisará criar este layout

        val welcomeText: TextView = findViewById(R.id.tv_welcome)
        val logoutButton: Button = findViewById(R.id.btn_logout)
        val profileButton: Button = findViewById(R.id.btn_profile) // Botão para o perfil

        val userId = TokenManager.currentUserId
        welcomeText.text = "Bem-vindo! Seu ID: ${userId ?: "N/A"}"

        logoutButton.setOnClickListener {
            TokenManager.clearSession() // Limpa o token e o ID do usuário
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        profileButton.setOnClickListener {
            val intent = Intent(this, UserProfileActivity::class.java)
            startActivity(intent)
        }
    }
}
