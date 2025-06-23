package com.alquimia.ui.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alquimia.data.repository.AuthRepository
import com.alquimia.data.repository.UserRepository // Manter para futuras operações de perfil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository // Manter para futuras operações de perfil
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Initial)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    // Variáveis para armazenar dados de registro temporariamente
    private var registeredName: String? = null
    private var registeredAge: Int? = null
    private var registeredCity: String? = null
    private var registeredGender: String? = null

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
                // Se o login for bem-sucedido, o backend já deve ter lidado com o perfil
                _uiState.value = LoginUiState.Success
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
                // Se o login com Google for bem-sucedido, o backend já deve ter lidado com o perfil
                _uiState.value = LoginUiState.Success
            } else {
                _uiState.value = LoginUiState.Error(signInResult.exceptionOrNull()?.message ?: "Erro no login com Google")
            }
        }
    }

    fun signInWithFacebook(accessToken: String) { // Mudado de redirectUrl para accessToken
        _uiState.value = LoginUiState.Loading

        viewModelScope.launch {
            val signInResult = authRepository.signInWithFacebook(accessToken) // Passar accessToken

            if (signInResult.isSuccess) {
                // Se o login com Facebook for bem-sucedido, o backend já deve ter lidado com o perfil
                _uiState.value = LoginUiState.Success
            } else {
                _uiState.value = LoginUiState.Error(signInResult.exceptionOrNull()?.message ?: "Erro no login com Facebook")
            }
        }
    }

    // Remover checkAndCreateUserProfile, pois o backend será responsável por isso
    // private suspend fun checkAndCreateUserProfile(email: String) { ... }

    fun resetState() {
        _uiState.value = LoginUiState.Initial
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
