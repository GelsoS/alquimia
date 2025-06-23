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
        gender: String,
        interests: List<String>? = null // Adicionar interesses
    ) {
        _uiState.value = RegisterUiState.Loading

        viewModelScope.launch {
            // Enviar todos os dados para o backend para criar a conta e o perfil
            val signUpResult = authRepository.signUpWithEmail(
                email = email,
                password = password,
                name = name,
                age = age,
                city = city,
                gender = gender,
                interests = interests
            )

            if (signUpResult.isSuccess) {
                _uiState.value = RegisterUiState.Success
            } else {
                _uiState.value = RegisterUiState.Error(
                    signUpResult.exceptionOrNull()?.message ?: "Erro no cadastro"
                )
            }
        }
    }

    // Remover createUserProfile, pois o perfil será criado no backend junto com o registro
    // suspend fun createUserProfile(...) { ... }

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
