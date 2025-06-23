package com.alquimia.ui.viewmodels

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
class SettingsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository // Injetar UserRepository
) : ViewModel() {

    private val _logoutState = MutableStateFlow<LogoutUiState>(LogoutUiState.Initial)
    val logoutState: StateFlow<LogoutUiState> = _logoutState.asStateFlow()

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    private val _isLoadingProfile = MutableStateFlow(false)
    val isLoadingProfile: StateFlow<Boolean> = _isLoadingProfile.asStateFlow()

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage.asStateFlow()

    init {
        loadCurrentUserProfile()
    }

    fun loadCurrentUserProfile() {
        _isLoadingProfile.value = true
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
            _isLoadingProfile.value = false
        }
    }

    fun signOut() {
        _logoutState.value = LogoutUiState.Loading
        viewModelScope.launch {
            val result = authRepository.signOut()
            _logoutState.value = if (result.isSuccess) {
                LogoutUiState.Success
            } else {
                LogoutUiState.Error(result.exceptionOrNull()?.message ?: "Erro ao fazer logout")
            }
        }
    }

    fun resetLogoutState() {
        _logoutState.value = LogoutUiState.Initial
    }

    fun resetErrorMessage() {
        _errorMessage.value = ""
    }
}

sealed class LogoutUiState {
    object Initial : LogoutUiState()
    object Loading : LogoutUiState()
    object Success : LogoutUiState()
    data class Error(val message: String) : LogoutUiState()
}
