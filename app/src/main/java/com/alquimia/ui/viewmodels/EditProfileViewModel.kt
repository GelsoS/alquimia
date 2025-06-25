package com.alquimia.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alquimia.data.remote.models.UpdateUserRequest
import com.alquimia.data.remote.models.UserData
import com.alquimia.data.repository.UserRepository
import com.alquimia.data.remote.TokenManager
import com.alquimia.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _userProfile = MutableLiveData<Resource<UserData>>()
    val userProfile: LiveData<Resource<UserData>> = _userProfile

    private val _updateProfileState = MutableLiveData<Resource<UserData>>()
    val updateProfileState: LiveData<Resource<UserData>> = _updateProfileState

    private val _uploadPictureState = MutableLiveData<Resource<String>>()
    val uploadPictureState: LiveData<Resource<String>> = _uploadPictureState

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

    fun updateProfile(name: String?, age: Int?, city: String?, gender: String?, interests: List<String>?) {
        viewModelScope.launch {
            _updateProfileState.value = Resource.Loading()
            val userId = TokenManager.currentUserId
            val token = TokenManager.authToken
            if (userId == null || token == null) {
                _updateProfileState.value = Resource.Error("Usuário não logado ou token não encontrado.")
                return@launch
            }
            val request = UpdateUserRequest(name, age, city, gender, interests)
            _updateProfileState.value = userRepository.updateUserProfile(userId, request, token)
        }
    }

    fun uploadProfilePicture(imageFile: File) {
        viewModelScope.launch {
            _uploadPictureState.value = Resource.Loading()
            val userId = TokenManager.currentUserId
            val token = TokenManager.authToken
            if (userId == null || token == null) {
                _uploadPictureState.value = Resource.Error("Usuário não logado ou token não encontrado.")
                return@launch
            }
            _uploadPictureState.value = userRepository.uploadProfilePicture(userId, imageFile, token)
        }
    }
}
