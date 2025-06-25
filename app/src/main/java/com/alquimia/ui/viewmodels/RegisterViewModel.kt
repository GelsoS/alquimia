package com.alquimia.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alquimia.data.remote.models.RegisterRequest
import com.alquimia.data.repository.AuthRepository
import com.alquimia.data.remote.TokenManager
import com.alquimia.util.Resource
import com.alquimia.data.remote.models.AuthResponse

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _registerState = MutableLiveData<RegisterState>()
    val registerState: LiveData<RegisterState> = _registerState

    fun register(request: RegisterRequest) {
        viewModelScope.launch {
            _registerState.value = RegisterState.Loading
            when (val result = authRepository.registerUser(request)) {
                is Resource.Success<AuthResponse> -> {
                    result.data?.let { authResponse ->
                        TokenManager.authToken = authResponse.token
                        TokenManager.currentUserId = authResponse.userId
                        _registerState.value = RegisterState.Success(authResponse.message)
                    } ?: run {
                        _registerState.value = RegisterState.Error("Dados de registro nulos inesperados.")
                    }
                }
                is Resource.Error<AuthResponse> -> {
                    _registerState.value = RegisterState.Error(result.message ?: "Erro desconhecido")
                }
                is Resource.Loading<AuthResponse> -> {
                    // Já tratado acima
                }
            }
        }
    }
}

sealed class RegisterState {
    object Loading : RegisterState()
    data class Success(val message: String) : RegisterState()
    data class Error(val message: String) : RegisterState()
}
