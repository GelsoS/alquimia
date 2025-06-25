package com.alquimia.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alquimia.data.remote.models.LoginRequest
import com.alquimia.data.remote.models.SocialLoginRequest
import com.alquimia.data.repository.AuthRepository
import com.alquimia.data.remote.TokenManager
import com.alquimia.util.Resource
import com.alquimia.data.remote.models.AuthResponse

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _loginState = MutableLiveData<LoginState>()
    val loginState: LiveData<LoginState>
        get() = _loginState

    fun loginUser(request: LoginRequest) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            when (val result = authRepository.loginUser(request)) {
                is Resource.Success<AuthResponse> -> {
                    // Usar 'let' para garantir que 'authResponse' não é nulo dentro do bloco
                    result.data?.let { authResponse ->
                        TokenManager.authToken = authResponse.token
                        TokenManager.currentUserId = authResponse.userId
                        _loginState.value = LoginState.Success(authResponse.message)
                    } ?: run {
                        // Caso inesperado: Resource.Success mas data é nulo
                        _loginState.value = LoginState.Error("Dados de login nulos inesperados.")
                    }
                }
                is Resource.Error<AuthResponse> -> {
                    _loginState.value = LoginState.Error(result.message ?: "Erro desconhecido")
                }
                is Resource.Loading<AuthResponse> -> {
                    // Já tratado acima
                }
            }
        }
    }

    fun loginWithGoogle(googleToken: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            val request = SocialLoginRequest(googleToken)
            when (val result = authRepository.loginWithGoogle(request)) {
                is Resource.Success<AuthResponse> -> {
                    result.data?.let { authResponse ->
                        TokenManager.authToken = authResponse.token
                        TokenManager.currentUserId = authResponse.userId
                        _loginState.value = LoginState.Success(authResponse.message)
                    } ?: run {
                        _loginState.value = LoginState.Error("Dados de login Google nulos inesperados.")
                    }
                }
                is Resource.Error<AuthResponse> -> {
                    _loginState.value = LoginState.Error(result.message ?: "Erro desconhecido")
                }
                is Resource.Loading<AuthResponse> -> {
                    // Já tratado acima
                }
            }
        }
    }

    fun loginWithFacebook(facebookToken: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            val request = SocialLoginRequest(facebookToken)
            when (val result = authRepository.loginWithFacebook(request)) {
                is Resource.Success<AuthResponse> -> {
                    result.data?.let { authResponse ->
                        TokenManager.authToken = authResponse.token
                        TokenManager.currentUserId = authResponse.userId
                        _loginState.value = LoginState.Success(authResponse.message)
                    } ?: run {
                        _loginState.value = LoginState.Error("Dados de login Facebook nulos inesperados.")
                    }
                }
                is Resource.Error<AuthResponse> -> {
                    _loginState.value = LoginState.Error(result.message ?: "Erro desconhecido")
                }
                is Resource.Loading<AuthResponse> -> {
                    // Já tratado acima
                }
            }
        }
    }
}

sealed class LoginState {
    object Loading : LoginState()
    data class Success(val message: String) : LoginState()
    data class Error(val message: String) : LoginState()
}
