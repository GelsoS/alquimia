package com.alquimia.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alquimia.data.models.User
import com.alquimia.data.repository.AuthRepository
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
    private val chatRepository: ChatRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _chatTime = MutableStateFlow(0)
    val chatTime: StateFlow<Int> = _chatTime.asStateFlow()

    private val _blurStatus = MutableStateFlow(100)
    val blurStatus: StateFlow<Int> = _blurStatus.asStateFlow()

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage.asStateFlow()

    fun loadUserProfile(profileUserId: String) {
        _isLoading.value = true
        _errorMessage.value = ""

        viewModelScope.launch {
            // Carregar dados do usuário do perfil
            val userResult = userRepository.getUserById(profileUserId)
            userResult.onSuccess { userProfile ->
                _user.value = userProfile
            }.onFailure { e ->
                _errorMessage.value = e.message ?: "Erro ao carregar perfil do usuário"
            }

            // Carregar dados da conversa (se existir)
            val currentUserId = authRepository.getCurrentSession()?.user?.id
            if (currentUserId != null && currentUserId != profileUserId) { // Não tentar criar conversa consigo mesmo
                val conversationResult = chatRepository.getOrCreateConversation(currentUserId, profileUserId)
                conversationResult.onSuccess { conversation ->
                    _chatTime.value = conversation.total_chat_time
                    _blurStatus.value = conversation.blur_status
                }.onFailure { e ->
                    _errorMessage.value = _errorMessage.value + "\n" + (e.message ?: "Erro ao carregar dados da conversa")
                }
            } else if (currentUserId == profileUserId) {
                // Se for o próprio usuário, não há conversa para carregar aqui
                _chatTime.value = 0
                _blurStatus.value = 100
            } else {
                _errorMessage.value = _errorMessage.value + "\n" + "Usuário não autenticado para carregar dados da conversa."
            }

            _isLoading.value = false
        }
    }

    fun resetErrorMessage() {
        _errorMessage.value = ""
    }
}
