package com.alquimia.ui.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alquimia.data.remote.models.ConversationData
import com.alquimia.data.repository.ChatRepository
import com.alquimia.data.remote.TokenManager
import com.alquimia.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatListViewModel @Inject constructor(
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _conversations = MutableLiveData<Resource<List<ConversationData>>>()
    val conversations: LiveData<Resource<List<ConversationData>>> = _conversations

    fun fetchUserConversations() {
        viewModelScope.launch {
            _conversations.value = Resource.Loading<List<ConversationData>>()
            val token = TokenManager.authToken
            if (token == null) {
                _conversations.value = Resource.Error<List<ConversationData>>("Token de autenticação não encontrado.")
                return@launch
            }
            when (val result = chatRepository.getUserConversations(token)) {
                is Resource.Success -> {
                    _conversations.value = Resource.Success(result.data.conversations)
                }
                is Resource.Error -> {
                    _conversations.value = Resource.Error<List<ConversationData>>(result.message ?: "Erro ao buscar conversas")
                }
                is Resource.Loading -> {
                    // Já tratado acima
                }
            }
        }
    }
}
