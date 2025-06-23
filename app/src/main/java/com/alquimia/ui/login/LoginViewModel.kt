package com.alquimia.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alquimia.data.remote.models.LoginRequest
import com.alquimia.data.repository.AuthRepository
import com.alquimia.data.remote.TokenManager
import com.alquimia.util.Resource
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
            when (val result = authRepository.loginUser(request, "")) { // Token é adicionado pelo interceptor
                is Resource.Success -> {
                    TokenManager.authToken = result.data.token
                    TokenManager.currentUserId = result.data.userId // Salvar o ID do usuário
                    _loginState.value = LoginState.Success(result.data.message)
                }
                is Resource.Error -> {
                    _loginState.value = LoginState.Error(result.message ?: "Erro desconhecido")
                }
            }
        }
    }

    fun loginWithGoogle(googleToken: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            when (val result = authRepository.loginWithGoogle(googleToken, "")) { // Token é adicionado pelo interceptor
                is Resource.Success -> {
                    TokenManager.authToken = result.data.token
                    TokenManager.currentUserId = result.data.userId // Salvar o ID do usuário
                    _loginState.value = LoginState.Success(result.data.message)
                }
                is Resource.Error -> {
                    _loginState.value = LoginState.Error(result.message ?: "Erro desconhecido")
                }
            }
        }
    }

    fun loginWithFacebook(facebookToken: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            when (val result = authRepository.loginWithFacebook(facebookToken, "")) { // Token é adicionado pelo interceptor
                is Resource.Success -> {
                    TokenManager.authToken = result.data.token
                    TokenManager.currentUserId = result.data.userId // Salvar o ID do usuário
                    _loginState.value = LoginState.Success(result.data.message)
                }
                is Resource.Error -> {
                    _loginState.value = LoginState.Error(result.message ?: "Erro desconhecido")
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
