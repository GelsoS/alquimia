package com.alquimia.data.repository

import com.alquimia.data.models.Conversation
import com.alquimia.data.models.Message
import io.github.jan.supabase.postgrest.Postgrest
//import io.github.jan.supabase.postgrest.query.Columns
import javax.inject.Inject
import javax.inject.Singleton
import io.github.jan.supabase.postgrest.query.Order
@Singleton
class ChatRepository @Inject constructor(
    private val postgrest: Postgrest
) {

    suspend fun getConversationsForUser(userId: String): List<Conversation> {
        return try {
            postgrest["conversations"]
                .select {
                    filter {
                        or {
                            eq("user1_id", userId)
                            eq("user2_id", userId)
                        }
                    }
                }
                .decodeList<Conversation>()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getMessagesForConversation(conversationId: String): List<Message> {
        return try {
            postgrest["messages"]
                .select {
                    filter {
                        eq("conversation_id", conversationId)
                    }
                    order(column = "timestamp", order = Order.ASCENDING) // ou DESCENDING se quiser do mais novo para o mais antigo
                }
                .decodeList<Message>()
        } catch (e: Exception) {
            emptyList()
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

            val insertedMessage = postgrest["messages"]
                .insert(message)
                .decodeSingle<Message>()

            Result.success(insertedMessage)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getOrCreateConversation(userId1: String, userId2: String): Conversation {
        return try {
            val existingConversation = postgrest["conversations"]
                .select {
                    filter {
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
                }
                .decodeSingleOrNull<Conversation>()

            if (existingConversation != null) {
                existingConversation
            } else {
                val newConversation = Conversation(
                    user1_id = userId1,
                    user2_id = userId2
                )

                postgrest["conversations"]
                    .insert(newConversation)
                    .decodeSingle<Conversation>()
            }
        } catch (e: Exception) {
            Conversation(
                id = "temp_${System.currentTimeMillis()}",
                user1_id = userId1,
                user2_id = userId2
            )
        }
    }
}
