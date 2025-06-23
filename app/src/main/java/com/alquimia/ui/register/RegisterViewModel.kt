package com.alquimia.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alquimia.data.remote.models.RegisterRequest
import com.alquimia.data.repository.AuthRepository
import com.alquimia.data.remote.TokenManager
import com.alquimia.util.Resource
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
            when (val result = authRepository.registerUser(request, "")) { // Token é adicionado pelo interceptor
                is Resource.Success -> {
                    TokenManager.authToken = result.data.token
                    TokenManager.currentUserId = result.data.userId // Salvar o ID do usuário
                    _registerState.value = RegisterState.Success(result.data.message)
                }
                is Resource.Error -> {
                    _registerState.value = RegisterState.Error(result.message ?: "Erro desconhecido")
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
