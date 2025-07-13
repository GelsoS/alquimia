package com.alquimia.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.alquimia.R
import com.alquimia.data.remote.TokenManager
import com.alquimia.databinding.ActivityMainBinding
import com.alquimia.ui.login.LoginActivity
import com.alquimia.ui.profile.UserProfileActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Verificar se o usuário está logado
        if (TokenManager.authToken == null) {
            redirectToLogin()
            return
        }

        setupNavigation()
        setupUI()
        setupListeners()
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        binding.bottomNavigation.setupWithNavController(navController)

        // Remover os botões de perfil e logout da MainActivity, pois a navegação inferior agora os gerencia
        binding.btnProfile.visibility = View.GONE
        binding.btnLogout.visibility = View.GONE
    }

    private fun setupUI() {
        val userId = TokenManager.currentUserId
        binding.tvWelcome.text = "Bem-vindo! Seu ID: ${userId ?: "N/A"}"
    }

    private fun setupListeners() {
        // Os listeners para btnLogout e btnProfile foram removidos daqui,
        // pois a navegação inferior agora gerencia esses fluxos através dos fragments.
        // O logout será tratado no SettingsFragment.
    }

    private fun redirectToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
