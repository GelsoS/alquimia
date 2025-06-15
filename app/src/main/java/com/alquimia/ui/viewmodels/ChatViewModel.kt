package com.alquimia.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alquimia.data.models.Message
import com.alquimia.data.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var currentConversationId: String? = null

    fun loadMessages(userId: String) {
        _isLoading.value = true

        viewModelScope.launch {
            val conversationResult = chatRepository.getOrCreateConversation("current_user_id", userId)

            conversationResult.onSuccess { conversation ->
                currentConversationId = conversation.id

                val messagesResult = chatRepository.getMessagesForConversation(conversation.id) // Alterado para getMessagesForConversation
                messagesResult.onSuccess { messageList ->
                    _messages.value = messageList
                }.onFailure {
                    // Opcional: logar erro ao carregar mensagens
                }
            }.onFailure {
                // Opcional: logar erro ao criar/obter conversa
            }

            _isLoading.value = false
        }
    }

    fun sendMessage(userId: String, content: String) {
        val conversationId = currentConversationId ?: return

        viewModelScope.launch {
            val result = chatRepository.sendMessage(
                conversationId = conversationId,
                senderId = "current_user_id",
                receiverId = userId,
                content = content
            )

            result.onSuccess { newMessage ->
                _messages.value = _messages.value + newMessage
            }.onFailure {
                // Opcional: logar erro ao enviar mensagem
            }
        }
    }
}
