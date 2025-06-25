package com.alquimia.ui.profile

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.alquimia.data.remote.TokenManager
import com.alquimia.databinding.ActivityUserProfileBinding
import com.alquimia.util.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserProfileBinding
    private val userProfileViewModel: UserProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userId = TokenManager.currentUserId
        if (userId != null) {
            userProfileViewModel.fetchUserProfile(userId)
        } else {
            Toast.makeText(this, "ID do usuário não encontrado. Faça login novamente.", Toast.LENGTH_LONG).show()
            finish()
        }

        setupObservers()
    }

    private fun setupObservers() {
        userProfileViewModel.userProfile.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.tvProfileInfo.text = "Carregando perfil..."
                }
                is Resource.Success -> {
                    resource.data?.let { user -> // Usar ?.let para acessar 'user' de forma segura
                        binding.tvProfileInfo.text = """
                            Nome: ${user.name}
                            Email: ${user.email}
                            Idade: ${user.age}
                            Cidade: ${user.city}
                            Gênero: ${user.gender}
                            Interesses: ${user.interests?.joinToString(", ") ?: "Nenhum"}
                            Foto: ${user.profilePicture ?: "N/A"}
                        """.trimIndent()
                    } ?: run {
                        binding.tvProfileInfo.text = "Erro: Dados do perfil nulos inesperados."
                        Toast.makeText(this, "Erro: Dados do perfil nulos inesperados.", Toast.LENGTH_LONG).show()
                    }
                }
                is Resource.Error -> {
                    binding.tvProfileInfo.text = "Erro ao carregar perfil: ${resource.message}"
                    Toast.makeText(this, resource.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
