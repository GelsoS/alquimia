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
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<RegisterUiState>(RegisterUiState.Initial)
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun signUp(
        email: String,
        password: String,
        name: String,
        age: Int,
        city: String,
        gender: String
    ) {
        _uiState.value = RegisterUiState.Loading

        viewModelScope.launch {
            // A função signUpWithEmail agora só lida com a autenticação.
            // Os dados do perfil (name, age, city, gender) serão usados no LoginViewModel
            // para criar o perfil na tabela 'users' após o primeiro login/confirmação.
            val result = authRepository.signUpWithEmail(email, password)

            _uiState.value = if (result.isSuccess) {
                // Após o signup, o usuário precisa confirmar o email e fazer login.
                // Não navegamos para Profiles aqui, mas para Login.
                RegisterUiState.Success
            } else {
                RegisterUiState.Error(result.exceptionOrNull()?.message ?: "Erro no cadastro")
            }
        }
    }

    fun resetState() {
        _uiState.value = RegisterUiState.Initial
    }
}

sealed class RegisterUiState {
    object Initial : RegisterUiState()
    object Loading : RegisterUiState()
    object Success : RegisterUiState()
    data class Error(val message: String) : RegisterUiState()
}
