package com.alquimia.ui.profile

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.alquimia.data.remote.TokenManager
import com.alquimia.databinding.ActivityUserProfileBinding // Você precisará criar este binding
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
                    val user = resource.data
                    binding.tvProfileInfo.text = """
                        Nome: ${user.name}
                        Email: ${user.email}
                        Idade: ${user.age}
                        Cidade: ${user.city}
                        Gênero: ${user.gender}
                        Interesses: ${user.interests?.joinToString(", ") ?: "Nenhum"}
                        Foto: ${user.profilePicture ?: "N/A"}
                    """.trimIndent()
                }
                is Resource.Error -> {
                    binding.tvProfileInfo.text = "Erro ao carregar perfil: ${resource.message}"
                    Toast.makeText(this, resource.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
