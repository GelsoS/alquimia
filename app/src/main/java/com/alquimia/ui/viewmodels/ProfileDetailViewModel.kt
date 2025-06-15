package com.alquimia.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alquimia.data.models.User
import com.alquimia.data.repository.ChatRepository
import com.alquimia.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileDetailViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _chatTime = MutableStateFlow(0)
    val chatTime: StateFlow<Int> = _chatTime.asStateFlow()

    private val _chemistryLevel = MutableStateFlow(100)
    val chemistryLevel: StateFlow<Int> = _chemistryLevel.asStateFlow()

    fun loadUserProfile(userId: String) {
        _isLoading.value = true

        viewModelScope.launch {
            // Carregar dados do usuário
            val userResult = userRepository.getUserById(userId)
            userResult.onSuccess { userProfile ->
                _user.value = userProfile
            }

            // Carregar dados da conversa (se existir)
            val conversationResult = chatRepository.getOrCreateConversation("current_user_id", userId)
            conversationResult.onSuccess { conversation ->
                _chatTime.value = conversation.total_chat_time
                _chemistryLevel.value = conversation.chemistry_level
            }

            _isLoading.value = false
        }
    }
}
