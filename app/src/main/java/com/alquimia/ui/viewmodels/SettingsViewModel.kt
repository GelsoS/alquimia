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
class SettingsViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _userProfile = MutableLiveData<Resource<UserData>>()
    val userProfile: LiveData<Resource<UserData>> = _userProfile

    fun fetchUserProfile() {
        viewModelScope.launch {
            _userProfile.value = Resource.Loading()
            val userId = TokenManager.currentUserId
            val token = TokenManager.authToken
            if (userId == null || token == null) {
                _userProfile.value = Resource.Error("Usuário não logado ou token não encontrado.")
                return@launch
            }
            _userProfile.value = userRepository.getUserProfile(userId, token)
        }
    }

    fun logout() {
        TokenManager.clearSession()
        _userProfile.value = Resource.Success(null) // Agora Resource.Success aceita nulo
    }
}
