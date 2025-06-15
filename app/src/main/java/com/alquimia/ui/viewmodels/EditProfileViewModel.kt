package com.alquimia.ui.viewmodels

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alquimia.data.models.User
import com.alquimia.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isUploading = MutableStateFlow(false)
    val isUploading: StateFlow<Boolean> = _isUploading.asStateFlow()

    private val _uploadSuccess = MutableStateFlow(false)
    val uploadSuccess: StateFlow<Boolean> = _uploadSuccess.asStateFlow()

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess: StateFlow<Boolean> = _saveSuccess.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        _isLoading.value = true

        viewModelScope.launch {
            // Usando "current_user_id" como ID do usuário atual (mock)
            // TODO: Substituir por ID do usuário logado real
            val result = userRepository.getUserById("current_user_id")

            result.onSuccess { userProfile ->
                _user.value = userProfile
            }.onFailure {
                // Opcional: logar o erro ou mostrar uma mensagem
            }

            _isLoading.value = false
        }
    }

    fun uploadProfilePicture(context: Context, imageUri: Uri) {
        _isUploading.value = true
        _uploadSuccess.value = false

        viewModelScope.launch {
            try {
                val inputStream = context.contentResolver.openInputStream(imageUri)
                val bytes = inputStream?.readBytes() ?: throw Exception("Erro ao ler imagem")

                val fileName = "profile_${System.currentTimeMillis()}.jpg"
                val userId = _user.value?.id ?: "current_user_id" // Usar ID do usuário real se disponível

                val result = userRepository.uploadProfilePicture(userId, bytes, fileName)

                result.onSuccess { imageUrl ->
                    _uploadSuccess.value = true
                    // Atualizar o perfil do usuário com a nova URL da imagem
                    viewModelScope.launch {
                        val updateResult = userRepository.updateProfilePicture(userId, imageUrl)
                        updateResult.onSuccess {
                            _user.value = _user.value?.copy(profile_picture = imageUrl)
                        }.onFailure {
                            // Opcional: logar erro ao atualizar URL da imagem no perfil
                        }
                    }
                }.onFailure {
                    _uploadSuccess.value = false
                    // Opcional: logar o erro ou mostrar uma mensagem
                }

            } catch (e: Exception) {
                _uploadSuccess.value = false
                // Opcional: logar o erro ou mostrar uma mensagem
            } finally {
                _isUploading.value = false
            }
        }
    }

    fun updateUserProfile(name: String, age: Int, city: String, gender: String) {
        _isLoading.value = true
        _saveSuccess.value = false

        viewModelScope.launch {
            val currentUser = _user.value ?: return@launch

            val updatedUser = currentUser.copy(
                name = name,
                age = age,
                city = city,
                gender = gender
            )

            val result = userRepository.updateUser(updatedUser)

            result.onSuccess {
                _user.value = updatedUser
                _saveSuccess.value = true
            }.onFailure {
                // Opcional: logar o erro ou mostrar uma mensagem
            }

            _isLoading.value = false
        }
    }
}
