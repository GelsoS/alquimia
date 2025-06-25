package com.alquimia.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alquimia.data.remote.models.ConversationData
import com.alquimia.data.remote.models.MessageData
import com.alquimia.data.repository.ChatRepository
import com.alquimia.data.remote.TokenManager
import com.alquimia.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _conversation = MutableLiveData<Resource<ConversationData>>()
    val conversation: LiveData<Resource<ConversationData>> = _conversation

    private val _messages = MutableLiveData<Resource<List<MessageData>>>()
    val messages: LiveData<Resource<List<MessageData>>> = _messages

    private val _sendMessageState = MutableLiveData<Resource<MessageData>>()
    val sendMessageState: LiveData<Resource<MessageData>> = _sendMessageState

    fun getOrCreateConversation(userId1: String, userId2: String) {
        viewModelScope.launch {
            _conversation.value = Resource.Loading()
            val token = TokenManager.authToken
            if (token == null) {
                _conversation.value = Resource.Error("Token de autenticação não encontrado.")
                return@launch
            }
            _conversation.value = chatRepository.getOrCreateConversation(userId1, userId2, token)
        }
    }

    fun fetchMessages(conversationId: String, page: Int = 1, limit: Int = 50) {
        viewModelScope.launch {
            _messages.value = Resource.Loading()
            val token = TokenManager.authToken
            if (token == null) {
                _messages.value = Resource.Error("Token de autenticação não encontrado.")
                return@launch
            }
            when (val result = chatRepository.getMessages(conversationId, token, page, limit)) {
                is Resource.Success -> {
                    _messages.value = Resource.Success(result.data!!.messages)
                }
                is Resource.Error -> {
                    _messages.value = Resource.Error(result.message ?: "Erro ao buscar mensagens")
                }
                is Resource.Loading -> {
                    // Já tratado acima
                }
            }
        }
    }

    fun sendMessage(conversationId: String, content: String) {
        viewModelScope.launch {
            _sendMessageState.value = Resource.Loading()
            val token = TokenManager.authToken
            if (token == null) {
                _sendMessageState.value = Resource.Error("Token de autenticação não encontrado.")
                return@launch
            }
            when (val result = chatRepository.sendMessage(conversationId, content, token)) {
                is Resource.Success -> {
                    // Adiciona a nova mensagem à lista existente
                    val currentMessages = _messages.value?.data.orEmpty().toMutableList()
                    result.data?.let { newMessage ->
                        currentMessages.add(newMessage)
                        _messages.value = Resource.Success(currentMessages)
                        _sendMessageState.value = Resource.Success(newMessage)
                    } ?: run {
                        _sendMessageState.value = Resource.Error("Mensagem enviada, mas dados nulos inesperados.")
                    }
                }
                is Resource.Error -> {
                    _sendMessageState.value = Resource.Error(result.message ?: "Erro ao enviar mensagem")
                }
                is Resource.Loading -> {
                    // Já tratado acima
                }
            }
        }
    }
}
