package com.alquimia.ui.viewmodels

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alquimia.data.models.User
import com.alquimia.data.repository.AuthRepository
import com.alquimia.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
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

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        _isLoading.value = true
        _errorMessage.value = ""

        viewModelScope.launch {
            val currentUserId = authRepository.getCurrentSession()?.user?.id
            if (currentUserId != null) {
                val result = userRepository.getUserById(currentUserId)

                result.onSuccess { userProfile ->
                    _user.value = userProfile
                }.onFailure { e ->
                    _errorMessage.value = e.message ?: "Erro ao carregar perfil"
                }
            } else {
                _errorMessage.value = "Usuário não autenticado."
            }
            _isLoading.value = false
        }
    }

    fun uploadProfilePicture(context: Context, imageUri: Uri) {
        _isUploading.value = true
        _uploadSuccess.value = false
        _errorMessage.value = ""

        viewModelScope.launch {
            try {
                val inputStream = context.contentResolver.openInputStream(imageUri)
                val bytes = inputStream?.readBytes() ?: throw Exception("Erro ao ler imagem")

                val fileName = "profile_${System.currentTimeMillis()}.jpg"
                val userId = authRepository.getCurrentSession()?.user?.id ?: throw Exception("Usuário não autenticado para upload.")

                val result = userRepository.uploadProfilePicture(userId, bytes, fileName)

                result.onSuccess { imageUrl ->
                    _uploadSuccess.value = true
                    viewModelScope.launch {
                        val updateResult = userRepository.updateProfilePicture(userId, imageUrl)
                        updateResult.onSuccess {
                            _user.value = _user.value?.copy(profile_picture = imageUrl)
                        }.onFailure { e ->
                            _errorMessage.value = e.message ?: "Erro ao atualizar URL da imagem no perfil"
                        }
                    }
                }.onFailure { e ->
                    _errorMessage.value = e.message ?: "Erro ao fazer upload da imagem"
                }

            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Erro desconhecido ao processar imagem"
            } finally {
                _isUploading.value = false
            }
        }
    }

    fun updateUserProfile(name: String, age: Int, city: String, gender: String, interests: List<String>?) {
        _isLoading.value = true
        _saveSuccess.value = false
        _errorMessage.value = ""

        viewModelScope.launch {
            val currentUserId = authRepository.getCurrentSession()?.user?.id
            if (currentUserId == null) {
                _errorMessage.value = "Usuário não autenticado para salvar perfil."
                _isLoading.value = false
                return@launch
            }

            val currentUser = _user.value ?: User(id = currentUserId, email = authRepository.getCurrentSession()?.user?.email ?: "", name = "", age = 0, city = "", gender = "")

            val updatedUser = currentUser.copy(
                name = name,
                age = age,
                city = city,
                gender = gender,
                interests = interests // Atualizar interesses
            )

            val result = userRepository.updateUser(updatedUser)

            result.onSuccess {
                _user.value = updatedUser
                _saveSuccess.value = true
            }.onFailure { e ->
                _errorMessage.value = e.message ?: "Erro ao salvar perfil"
            }

            _isLoading.value = false
        }
    }

    fun resetUploadSuccess() {
        _uploadSuccess.value = false
    }

    fun resetSaveSuccess() {
        _saveSuccess.value = false
    }

    fun resetErrorMessage() {
        _errorMessage.value = ""
    }
}
