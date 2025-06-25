package com.alquimia.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alquimia.data.remote.models.AuthResponse
import com.alquimia.data.remote.models.ForgotPasswordRequest
import com.alquimia.data.repository.AuthRepository
import com.alquimia.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _resetPasswordState = MutableLiveData<Resource<AuthResponse>>()
    val resetPasswordState: LiveData<Resource<AuthResponse>> = _resetPasswordState

    fun resetPassword(email: String) {
        viewModelScope.launch {
            _resetPasswordState.value = Resource.Loading()
            val request = ForgotPasswordRequest(email)
            when (val result = authRepository.forgotPassword(request)) {
                is Resource.Success -> {
                    _resetPasswordState.value = Resource.Success(result.data!!)
                }
                is Resource.Error -> {
                    _resetPasswordState.value = Resource.Error(result.message ?: "Erro desconhecido")
                }
                is Resource.Loading -> {
                    // Já tratado acima
                }
            }
        }
    }
}
