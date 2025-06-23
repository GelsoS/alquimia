package com.alquimia.data.remote.models

import com.google.gson.annotations.SerializedName

// Resposta de usuários
data class UsersResponse(
    val success: Boolean,
    val users: List<UserData>,
    val pagination: PaginationData
)

// Resposta de perfil de usuário
data class UserProfileResponse(
    val success: Boolean,
    val user: UserData
)

// Dados do usuário
data class UserData(
    val id: String,
    val email: String,
    val name: String,
    val age: Int,
    val city: String,
    val gender: String,
    @SerializedName("profile_picture")
    val profilePicture: String?,
    val interests: List<String>?,
    @SerializedName("last_seen")
    val lastSeen: String?,
    @SerializedName("created_at")
    val createdAt: String?
)

// Requisição de atualização de usuário
data class UpdateUserRequest(
    val name: String?,
    val age: Int?,
    val city: String?,
    val gender: String?,
    val interests: List<String>?
)

// Resposta de upload de imagem
data class UploadPictureResponse(
    val success: Boolean,
    val message: String,
    @SerializedName("imageUrl")
    val imageUrl: String?
)

// Dados de paginação
data class PaginationData(
    val page: Int,
    val limit: Int,
    val total: Int
)
