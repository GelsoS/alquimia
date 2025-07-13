package com.alquimia.ui.profile

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.alquimia.data.remote.TokenManager
import com.alquimia.databinding.ActivityUserProfileBinding
import com.alquimia.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import com.bumptech.glide.Glide
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import java.io.File
import com.alquimia.data.remote.models.UpdateUserRequest // Para o exemplo de edição

@AndroidEntryPoint
class UserProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserProfileBinding
    private val userProfileViewModel: UserProfileViewModel by viewModels()

    private val pickImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageUri: Uri? = result.data?.data
            imageUri?.let { uri ->
                val filePath = getPathFromUri(uri)
                if (filePath != null) {
                    val imageFile = File(filePath)
                    val userId = TokenManager.currentUserId
                    if (userId != null) {
                        userProfileViewModel.uploadProfilePicture(userId, imageFile)
                    } else {
                        Toast.makeText(this, "ID do usuário não encontrado para upload.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Não foi possível obter o caminho do arquivo da imagem.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getPathFromUri(uri: Uri): String? {
        var filePath: String? = null
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndex(MediaStore.Images.Media.DATA)
                if (columnIndex != -1) {
                    filePath = it.getString(columnIndex)
                }
            }
        }
        return filePath
    }

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
        setupListeners() // Adicione esta linha
    }

    private fun setupObservers() {
        userProfileViewModel.userProfile.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.tvProfileInfo.text = "Carregando perfil..."
                }
                is Resource.Success -> {
                    resource.data?.let { user ->
                        binding.tvProfileInfo.text = """
                            Nome: ${user.name}
                            Email: ${user.email}
                            Idade: ${user.age}
                            Cidade: ${user.city}
                            Gênero: ${user.gender}
                            Interesses: ${user.interests?.joinToString(", ") ?: "Nenhum"}
                        """.trimIndent() // Removido a linha da foto daqui

                        // Carregar imagem de perfil com Glide
                        user.profilePicture?.let { imageUrl ->
                            Glide.with(binding.ivUserProfilePicture.context)
                                .load(imageUrl)
                                .placeholder(android.R.drawable.sym_def_app_icon)
                                .error(android.R.drawable.ic_menu_gallery)
                                .into(binding.ivUserProfilePicture)
                        } ?: run {
                            binding.ivUserProfilePicture.setImageResource(android.R.drawable.sym_def_app_icon)
                        }
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

    private fun setupListeners() {
        binding.btnEditProfileActivity.setOnClickListener {
            // Exemplo: Navegar para EditProfileFragment (se fosse um Fragment)
            // Ou abrir um diálogo de edição simples aqui
            Toast.makeText(this, "Funcionalidade de edição será implementada no EditProfileFragment", Toast.LENGTH_SHORT).show()
            // Para um exemplo rápido de edição (sem UI):
            // val userId = TokenManager.currentUserId
            // if (userId != null) {
            //     val updatedRequest = UpdateUserRequest(name = "Novo Nome", age = 30, city = "Nova Cidade", gender = "Não Informado", interests = listOf("Novo Interesse"))
            //     userProfileViewModel.updateProfile(userId, updatedRequest)
            // }
        }

        binding.btnUploadPictureActivity.setOnClickListener {
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickImage.launch(galleryIntent)
        }

        userProfileViewModel.updateProfileState.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> Toast.makeText(this, "Atualizando perfil...", Toast.LENGTH_SHORT).show()
                is Resource.Success -> {
                    Toast.makeText(this, "Perfil atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                    userProfileViewModel.fetchUserProfile(TokenManager.currentUserId!!) // Recarregar perfil
                }
                is Resource.Error -> Toast.makeText(this, "Erro ao atualizar perfil: ${resource.message}", Toast.LENGTH_LONG).show()
            }
        }

        userProfileViewModel.uploadPictureState.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> Toast.makeText(this, "Fazendo upload da imagem...", Toast.LENGTH_SHORT).show()
                is Resource.Success -> {
                    Toast.makeText(this, "Upload de imagem concluído!", Toast.LENGTH_SHORT).show()
                    userProfileViewModel.fetchUserProfile(TokenManager.currentUserId!!) // Recarregar perfil
                }
                is Resource.Error -> Toast.makeText(this, "Erro no upload da imagem: ${resource.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
