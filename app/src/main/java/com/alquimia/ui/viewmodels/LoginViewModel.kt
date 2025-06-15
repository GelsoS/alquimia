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
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Initial)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun signIn(email: String, password: String) {
        _uiState.value = LoginUiState.Loading

        viewModelScope.launch {
            val signInResult = authRepository.signInWithEmail(email, password)

            if (signInResult.isSuccess) {
                val currentUserAuth = authRepository.goTrue.currentSessionOrNull()?.user
                val userId = currentUserAuth?.id
                val userEmail = currentUserAuth?.email

                if (userId != null && userEmail != null) {
                    val userProfileResult = userRepository.getUserById(userId)
                    userProfileResult.onSuccess { userProfile ->
                        if (userProfile == null) {
                            // Perfil não existe na tabela 'users', criar agora.
                            val newUserProfile = User(
                                id = userId,
                                email = userEmail,
                                name = currentUserAuth.userMetadata?.get("name") as? String ?: "Novo Usuário",
                                age = currentUserAuth.userMetadata?.get("age") as? Int ?: 0,
                                city = currentUserAuth.userMetadata?.get("city") as? String ?: "Desconhecida",
                                gender = currentUserAuth.userMetadata?.get("gender") as? String ?: "Não Informado",
                                profile_picture = null,
                                last_seen = null, // Definido como null para usar o DEFAULT NOW() do DB
                                created_at = null // Definido como null para usar o DEFAULT NOW() do DB
                            )
                            val insertResult = userRepository.insertUser(newUserProfile)
                            if (insertResult.isSuccess) {
                                _uiState.value = LoginUiState.Success
                            } else {
                                _uiState.value = LoginUiState.Error(insertResult.exceptionOrNull()?.message ?: "Erro ao criar perfil do usuário")
                            }
                        } else {
                            // Perfil já existe
                            _uiState.value = LoginUiState.Success
                        }
                    }.onFailure {
                        _uiState.value = LoginUiState.Error(it.message ?: "Erro ao verificar perfil do usuário")
                    }
                } else {
                    _uiState.value = LoginUiState.Error("Dados do usuário autenticado incompletos.")
                }
            } else {
                _uiState.value = LoginUiState.Error(signInResult.exceptionOrNull()?.message ?: "Erro no login")
            }
        }
    }

    fun signInWithGoogle() {
        _uiState.value = LoginUiState.Error("Login com Google não implementado.")
    }

    fun signInWithFacebook() {
        _uiState.value = LoginUiState.Error("Login com Facebook não implementado.")
    }

    fun resetState() {
        _uiState.value = LoginUiState.Initial
    }
}

sealed class LoginUiState {
    object Initial : LoginUiState()
    object Loading : LoginUiState()
    object Success : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}
