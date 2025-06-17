package com.alquimia.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alquimia.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _logoutState = MutableStateFlow<LogoutUiState>(LogoutUiState.Initial)
    val logoutState: StateFlow<LogoutUiState> = _logoutState.asStateFlow()

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
}

sealed class LogoutUiState {
    object Initial : LogoutUiState()
    object Loading : LogoutUiState()
    object Success : LogoutUiState()
    data class Error(val message: String) : LogoutUiState()
}
