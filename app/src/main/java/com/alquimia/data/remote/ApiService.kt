package com.alquimia.data.remote

import com.alquimia.data.remote.models.* // Certifique-se de que esta importação está correta
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // === AUTENTICAÇÃO ===
    @POST("auth/register")
    suspend fun registerUser(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("auth/login")
    suspend fun loginUser(@Body request: LoginRequest): Response<AuthResponse>

    @POST("auth/google")
    suspend fun loginWithGoogle(@Body request: SocialLoginRequest): Response<AuthResponse>

    @POST("auth/facebook")
    suspend fun loginWithFacebook(@Body request: SocialLoginRequest): Response<AuthResponse>

    @POST("auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<AuthResponse>

    // === USUÁRIOS ===
    @GET("users")
    suspend fun getAllUsers(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20,
        @Query("city") city: String? = null,
        @Query("gender") gender: String? = null,
        @Query("minAge") minAge: Int? = null,
        @Query("maxAge") maxAge: Int? = null,
        @Header("Authorization") token: String
    ): Response<UsersResponse>

    @GET("users/{userId}")
    suspend fun getUserProfile(
        @Path("userId") userId: String,
        @Header("Authorization") token: String
    ): Response<UserProfileResponse>

    @PUT("users/{userId}")
    suspend fun updateUserProfile(
        @Path("userId") userId: String,
        @Body user: UpdateUserRequest,
        @Header("Authorization") token: String
    ): Response<UserProfileResponse>

    @Multipart
    @POST("users/{userId}/upload-picture")
    suspend fun uploadProfilePicture(
        @Path("userId") userId: String,
        @Part file: MultipartBody.Part,
        @Header("Authorization") token: String
    ): Response<UploadPictureResponse>

    // === CHAT ===
    @GET("chat/conversations")
    suspend fun getUserConversations(
        @Header("Authorization") token: String
    ): Response<ConversationsResponse>

    @GET("chat/conversations/{userId1}/{userId2}")
    suspend fun getOrCreateConversation(
        @Path("userId1") userId1: String,
        @Path("userId2") userId2: String,
        @Header("Authorization") token: String
    ): Response<ConversationResponse>

    @GET("chat/conversations/{conversationId}/messages")
    suspend fun getMessages(
        @Path("conversationId") conversationId: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 50,
        @Header("Authorization") token: String
    ): Response<MessagesResponse>

    @POST("chat/conversations/{conversationId}/messages")
    suspend fun sendMessage(
        @Path("conversationId") conversationId: String,
        @Body message: SendMessageRequest,
        @Header("Authorization") token: String
    ): Response<MessageResponse>

    @PUT("chat/conversations/{conversationId}/metrics")
    suspend fun updateChatMetrics(
        @Path("conversationId") conversationId: String,
        @Body metrics: ChatMetricsRequest,
        @Header("Authorization") token: String
    ): Response<AuthResponse>
}
