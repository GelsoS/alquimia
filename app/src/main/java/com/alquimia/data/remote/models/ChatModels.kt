package com.alquimia.data.remote.models

import com.google.gson.annotations.SerializedName

// Resposta de conversas
data class ConversationsResponse(
    val success: Boolean,
    val conversations: List<ConversationData>
)

// Resposta de conversa única
data class ConversationResponse(
    val success: Boolean,
    val conversation: ConversationData
)

// Dados da conversa
data class ConversationData(
    val id: String,
    @SerializedName("user1_id")
    val user1Id: String? = null,
    @SerializedName("user2_id")
    val user2Id: String? = null,
    @SerializedName("other_user")
    val otherUser: OtherUserData? = null,
    @SerializedName("last_message")
    val lastMessage: String?,
    @SerializedName("last_message_time")
    val lastMessageTime: String?,
    @SerializedName("total_chat_time")
    val totalChatTime: Int,
    @SerializedName("chemistry_level")
    val chemistryLevel: Int,
    @SerializedName("started_at")
    val startedAt: String?
)

// Dados do outro usuário na conversa
data class OtherUserData(
    val id: String,
    val name: String,
    @SerializedName("profile_picture")
    val profilePicture: String?
)

// Resposta de mensagens
data class MessagesResponse(
    val success: Boolean,
    val messages: List<MessageData>,
    val pagination: PaginationData
)

// Resposta de mensagem única
data class MessageResponse(
    val success: Boolean,
    val message: String,
    val data: MessageData
)

// Dados da mensagem
data class MessageData(
    val id: String,
    @SerializedName("conversation_id")
    val conversationId: String,
    @SerializedName("sender_id")
    val senderId: String,
    @SerializedName("receiver_id")
    val receiverId: String,
    val content: String,
    val timestamp: String,
    @SerializedName("sender_name")
    val senderName: String?
)

// Requisição para enviar mensagem
data class SendMessageRequest(
    val content: String
)

// Requisição para atualizar métricas do chat
data class ChatMetricsRequest(
    @SerializedName("chatTime")
    val chatTime: Int?,
    @SerializedName("chemistryLevel")
    val chemistryLevel: Int?
)
