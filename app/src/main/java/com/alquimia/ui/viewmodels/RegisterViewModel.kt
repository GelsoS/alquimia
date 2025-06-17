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
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository // Adicionado UserRepository
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
            // 1. Primeiro, criar a conta de autenticação
            val signUpResult = authRepository.signUpWithEmail(email, password)

            if (signUpResult.isSuccess) {
                // 2. Após confirmação do email e primeiro login, o perfil será criado
                // Por enquanto, apenas indicamos sucesso no registro de autenticação
                _uiState.value = RegisterUiState.Success
            } else {
                _uiState.value = RegisterUiState.Error(
                    signUpResult.exceptionOrNull()?.message ?: "Erro no cadastro"
                )
            }
        }
    }

    // Novo método para criar perfil após confirmação de email
    suspend fun createUserProfile(
        email: String,
        name: String,
        age: Int,
        city: String,
        gender: String
    ): Result<Unit> {
        return try {
            // Obter o ID do usuário autenticado
            val session = authRepository.getCurrentSession()
            val userId = session?.user?.id ?: throw Exception("Usuário não autenticado")

            val user = User(
                id = userId,
                email = email,
                name = name,
                age = age,
                city = city,
                gender = gender
            )

            userRepository.insertUser(user)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
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
