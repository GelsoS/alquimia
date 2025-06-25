package com.alquimia.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alquimia.data.remote.models.ConversationData
import com.alquimia.data.remote.models.UserData
import com.alquimia.data.repository.ChatRepository
import com.alquimia.data.repository.UserRepository
import com.alquimia.data.remote.TokenManager
import com.alquimia.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileDetailViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _userProfile = MutableLiveData<Resource<UserData>>()
    val userProfile: LiveData<Resource<UserData>> = _userProfile

    private val _conversationState = MutableLiveData<Resource<ConversationData>>()
    val conversationState: LiveData<Resource<ConversationData>> = _conversationState

    fun fetchUserProfile(userId: String) {
        viewModelScope.launch {
            _userProfile.value = Resource.Loading()
            val token = TokenManager.authToken
            if (token == null) {
                _userProfile.value = Resource.Error("Token de autenticação não encontrado.")
                return@launch
            }
            _userProfile.value = userRepository.getUserProfile(userId, token)
        }
    }

    fun getOrCreateConversation(otherUserId: String) {
        viewModelScope.launch {
            _conversationState.value = Resource.Loading()
            val currentUserId = TokenManager.currentUserId
            val token = TokenManager.authToken
            if (currentUserId == null || token == null) {
                _conversationState.value = Resource.Error("Usuário não logado ou token não encontrado.")
                return@launch
            }
            _conversationState.value = chatRepository.getOrCreateConversation(currentUserId, otherUserId, token)
        }
    }
}
