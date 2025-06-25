package com.alquimia.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.alquimia.R
import com.alquimia.data.remote.TokenManager
import com.alquimia.databinding.ActivityMainBinding
import com.alquimia.ui.login.LoginActivity
import com.alquimia.ui.profile.UserProfileActivity // Manter se UserProfileActivity ainda for uma Activity separada
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar NavController para Fragments
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Configurar BottomNavigationView com NavController (se você tiver uma)
        // Certifique-se de que o ID 'bottom_navigation' existe em activity_main.xml
        // e que você tem um menu 'bottom_nav_menu' em res/menu/
        // binding.bottomNavigation.setupWithNavController(navController)

        val userId = TokenManager.currentUserId
        binding.tvWelcome.text = "Bem-vindo! Seu ID: ${userId ?: "N/A"}"

        binding.btnLogout.setOnClickListener {
            TokenManager.clearSession()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        binding.btnProfile.setOnClickListener {
            // Se UserProfileActivity for uma Activity separada, mantenha esta linha
            val intent = Intent(this, UserProfileActivity::class.java)
            startActivity(intent)
            // Se UserProfileActivity for um Fragment, você navegaria assim:
            // navController.navigate(R.id.userProfileFragment) // Substitua pelo ID do seu fragmento de perfil
        }
    }
}
