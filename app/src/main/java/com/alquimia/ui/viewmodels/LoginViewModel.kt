package com.alquimia.ui.viewmodels

import android.content.Context
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

    // Variáveis para armazenar dados de registro temporariamente
    private var registeredName: String? = null
    private var registeredAge: Int? = null
    private var registeredCity: String? = null
    private var registeredGender: String? = null

    // Método para inicializar dados de registro passados do RegisterActivity
    fun initRegistrationData(name: String?, age: Int?, city: String?, gender: String?) {
        this.registeredName = name
        this.registeredAge = age
        this.registeredCity = city
        this.registeredGender = gender
    }

    fun signIn(email: String, password: String) {
        _uiState.value = LoginUiState.Loading

        viewModelScope.launch {
            val signInResult = authRepository.signInWithEmail(email, password)

            if (signInResult.isSuccess) {
                // Após o login bem-sucedido, verificar e criar o perfil se necessário
                checkAndCreateUserProfile(email)
            } else {
                _uiState.value = LoginUiState.Error(signInResult.exceptionOrNull()?.message ?: "Erro no login")
            }
        }
    }

    fun signInWithGoogle(context: Context) {
        _uiState.value = LoginUiState.Loading

        viewModelScope.launch {
            val signInResult = authRepository.signInWithGoogle(context)

            if (signInResult.isSuccess) {
                // Após o login com Google, verificar e criar o perfil se necessário
                val session = authRepository.getCurrentSession()
                val email = session?.user?.email // Obter email da sessão do Google
                if (email != null) {
                    checkAndCreateUserProfile(email)
                } else {
                    _uiState.value = LoginUiState.Error("Não foi possível obter o email do usuário logado.")
                }
            } else {
                _uiState.value = LoginUiState.Error(signInResult.exceptionOrNull()?.message ?: "Erro no login com Google")
            }
        }
    }

    private suspend fun checkAndCreateUserProfile(email: String) {
        val session = authRepository.getCurrentSession()
        val userId = session?.user?.id

        if (userId != null) {
            val userProfileResult = userRepository.getUserById(userId)
            if (userProfileResult.isSuccess && userProfileResult.getOrNull() == null) {
                // Perfil não existe, criar um novo
                val newUser = User(
                    id = userId,
                    email = email,
                    name = registeredName ?: "Novo Usuário", // Usar nome registrado ou padrão
                    age = registeredAge ?: 18, // Usar idade registrada ou padrão
                    city = registeredCity ?: "Desconhecida", // Usar cidade registrada ou padrão
                    gender = registeredGender ?: "Não Informado" // Usar gênero registrado ou padrão
                )
                val insertResult = userRepository.insertUser(newUser)
                if (insertResult.isSuccess) {
                    _uiState.value = LoginUiState.Success
                } else {
                    _uiState.value = LoginUiState.Error(insertResult.exceptionOrNull()?.message ?: "Erro ao criar perfil do usuário.")
                }
            } else if (userProfileResult.isSuccess) {
                // Perfil já existe, login bem-sucedido
                _uiState.value = LoginUiState.Success
            } else {
                // Erro ao verificar perfil
                _uiState.value = LoginUiState.Error(userProfileResult.exceptionOrNull()?.message ?: "Erro ao verificar perfil do usuário.")
            }
        } else {
            _uiState.value = LoginUiState.Error("Usuário não autenticado após login.")
        }
    }

    fun resetState() {
        _uiState.value = LoginUiState.Initial
        // Limpar dados de registro após o uso
        registeredName = null
        registeredAge = null
        registeredCity = null
        registeredGender = null
    }
}

sealed class LoginUiState {
    object Initial : LoginUiState()
    object Loading : LoginUiState()
    object Success : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}
