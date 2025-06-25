package com.alquimia.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alquimia.data.remote.models.UserData
import com.alquimia.data.repository.UserRepository
import com.alquimia.data.remote.TokenManager
import com.alquimia.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfilesViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _profiles = MutableLiveData<Resource<List<UserData>>>()
    val profiles: LiveData<Resource<List<UserData>>> = _profiles

    fun fetchAllProfiles() {
        viewModelScope.launch {
            _profiles.value = Resource.Loading()
            val token = TokenManager.authToken
            if (token == null) {
                _profiles.value = Resource.Error("Token de autenticação não encontrado.")
                return@launch
            }
            // O backend já exclui o usuário atual automaticamente
            when (val result = userRepository.getAllUsers(token)) {
                is Resource.Success -> {
                    _profiles.value = Resource.Success(result.data!!.users)
                }
                is Resource.Error -> {
                    _profiles.value = Resource.Error(result.message ?: "Erro ao buscar perfis.")
                }
                is Resource.Loading -> {
                    // Já tratado acima
                }
            }
        }
    }
}
