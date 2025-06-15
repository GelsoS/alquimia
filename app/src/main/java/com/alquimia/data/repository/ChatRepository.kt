package com.alquimia.data.repository

import com.alquimia.data.models.Conversation
import com.alquimia.data.models.Message
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Order
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(
    private val postgrest: Postgrest
) {

    suspend fun getConversationsForUser(userId: String): Result<List<Conversation>> {
        return try {
            val conversations = postgrest["conversations"]
                .select {
                    or {
                        eq("user1_id", userId)
                        eq("user2_id", userId)
                    }
                }
                .decodeList<Conversation>()
            Result.success(conversations)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMessagesForConversation(conversationId: String): Result<List<Message>> {
        return try {
            val messages = postgrest["messages"]
                .select {
                    eq("conversation_id", conversationId)
                    order("timestamp", Order.ASCENDING) // Sintaxe correta para order
                }
                .decodeList<Message>()
            Result.success(messages)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendMessage(conversationId: String, senderId: String, receiverId: String, content: String): Result<Message> {
        return try {
            val message = Message(
                conversation_id = conversationId,
                sender_id = senderId,
                receiver_id = receiverId,
                content = content
            )

            val insertedMessages = postgrest["messages"]
                .insert(message)
                .decodeList<Message>()

            val insertedMessage = insertedMessages.firstOrNull()
                ?: throw Exception("Falha ao inserir mensagem")

            Result.success(insertedMessage)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getOrCreateConversation(userId1: String, userId2: String): Result<Conversation> {
        return try {
            val existingConversations = postgrest["conversations"]
                .select {
                    or {
                        and {
                            eq("user1_id", userId1)
                            eq("user2_id", userId2)
                        }
                        and {
                            eq("user1_id", userId2)
                            eq("user2_id", userId1)
                        }
                    }
                }
                .decodeList<Conversation>()

            val existingConversation = existingConversations.firstOrNull()

            if (existingConversation != null) {
                Result.success(existingConversation)
            } else {
                val newConversation = Conversation(
                    user1_id = userId1,
                    user2_id = userId2
                )

                val insertedConversations = postgrest["conversations"]
                    .insert(newConversation)
                    .decodeList<Conversation>()

                Result.success(insertedConversations.firstOrNull() ?: newConversation)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
