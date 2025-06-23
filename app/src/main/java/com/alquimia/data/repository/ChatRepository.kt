package com.alquimia.data.repository

import com.alquimia.data.remote.ApiService
import com.alquimia.data.remote.models.*
import com.alquimia.data.remote.models.ErrorResponse // Importar o novo modelo de erro
import com.alquimia.util.Resource
import com.google.gson.Gson
import javax.inject.Inject

interface ChatRepository {
    suspend fun getOrCreateConversation(userId1: String, userId2: String, token: String): Resource<ConversationData>
    suspend fun getUserConversations(token: String): Resource<ConversationsResponse>
    suspend fun getMessages(conversationId: String, token: String, page: Int? = null, limit: Int? = null): Resource<MessagesResponse>
    suspend fun sendMessage(conversationId: String, content: String, token: String): Resource<MessageData>
    suspend fun updateChatMetrics(conversationId: String, chatTime: Int?, chemistryLevel: Int?, token: String): Resource<AuthResponse>
}

class ChatRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : ChatRepository {

    override suspend fun getOrCreateConversation(userId1: String, userId2: String, token: String): Resource<ConversationData> {
        return try {
            val response = apiService.getOrCreateConversation(userId1, userId2, "Bearer $token")
            if (response.isSuccessful) {
                Resource.Success(response.body()!!.conversation)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = try {
                    Gson().fromJson(errorBody, ErrorResponse::class.java).message
                } catch (e: Exception) {
                    "Erro desconhecido ao obter/criar conversa"
                }
                Resource.Error(errorMessage, null)
            }
        } catch (e: Exception) {
            Resource.Error("Erro de rede: ${e.localizedMessage}", null)
        }
    }

    override suspend fun getUserConversations(token: String): Resource<ConversationsResponse> {
        return try {
            val response = apiService.getUserConversations("Bearer $token")
            if (response.isSuccessful) {
                Resource.Success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = try {
                    Gson().fromJson(errorBody, ErrorResponse::class.java).message
                } catch (e: Exception) {
                    "Erro desconhecido ao buscar conversas"
                }
                Resource.Error(errorMessage, null)
            }
        } catch (e: Exception) {
            Resource.Error("Erro de rede: ${e.localizedMessage}", null)
        }
    }

    override suspend fun getMessages(conversationId: String, token: String, page: Int?, limit: Int?): Resource<MessagesResponse> {
        return try {
            val response = apiService.getMessages(conversationId, page ?: 1, limit ?: 50, "Bearer $token")
            if (response.isSuccessful) {
                Resource.Success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = try {
                    Gson().fromJson(errorBody, ErrorResponse::class.java).message
                } catch (e: Exception) {
                    "Erro desconhecido ao buscar mensagens"
                }
                Resource.Error(errorMessage, null)
            }
        } catch (e: Exception) {
            Resource.Error("Erro de rede: ${e.localizedMessage}", null)
        }
    }

    override suspend fun sendMessage(conversationId: String, content: String, token: String): Resource<MessageData> {
        return try {
            val request = SendMessageRequest(content)
            val response = apiService.sendMessage(conversationId, request, "Bearer $token")
            if (response.isSuccessful) {
                Resource.Success(response.body()!!.data)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = try {
                    Gson().fromJson(errorBody, ErrorResponse::class.java).message
                } catch (e: Exception) {
                    "Erro desconhecido ao enviar mensagem"
                }
                Resource.Error(errorMessage, null)
            }
        } catch (e: Exception) {
            Resource.Error("Erro de rede: ${e.localizedMessage}", null)
        }
    }

    override suspend fun updateChatMetrics(conversationId: String, chatTime: Int?, chemistryLevel: Int?, token: String): Resource<AuthResponse> {
        return try {
            val request = ChatMetricsRequest(chatTime, chemistryLevel)
            val response = apiService.updateChatMetrics(conversationId, request, "Bearer $token")
            if (response.isSuccessful) {
                Resource.Success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = try {
                    Gson().fromJson(errorBody, ErrorResponse::class.java).message
                } catch (e: Exception) {
                    "Erro desconhecido ao atualizar métricas"
                }
                Resource.Error(errorMessage, null)
            }
        } catch (e: Exception) {
            Resource.Error("Erro de rede: ${e.localizedMessage}", null)
        }
    }
}
