package com.alquimia.data.repository

import com.alquimia.data.remote.ApiService
import com.alquimia.data.remote.models.ErrorResponse // Importar o modelo de erro
import com.alquimia.data.remote.models.UpdateUserRequest
import com.alquimia.data.remote.models.UserData
import com.alquimia.data.remote.models.UserProfileResponse
import com.alquimia.data.remote.models.UsersResponse
import com.alquimia.util.Resource // Importar a classe Resource
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

interface UserRepository {
    suspend fun getUserProfile(userId: String, token: String): Resource<UserData>
    suspend fun getAllUsers(
        token: String,
        page: Int? = null,
        limit: Int? = null,
        city: String? = null,
        gender: String? = null,
        minAge: Int? = null,
        maxAge: Int? = null
    ): Resource<UsersResponse>
    suspend fun updateUserProfile(userId: String, request: UpdateUserRequest, token: String): Resource<UserData>
    suspend fun uploadProfilePicture(userId: String, imageFile: File, token: String): Resource<String>
}

class UserRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : UserRepository {

    override suspend fun getUserProfile(userId: String, token: String): Resource<UserData> {
        return try {
            val response = apiService.getUserProfile(userId, "Bearer $token")
            if (response.isSuccessful) {
                Resource.Success(response.body()!!.user)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = try {
                    Gson().fromJson(errorBody, ErrorResponse::class.java).message
                } catch (e: Exception) {
                    "Erro desconhecido ao buscar perfil"
                }
                Resource.Error(errorMessage, null)
            }
        } catch (e: Exception) {
            Resource.Error("Erro de rede: ${e.localizedMessage}", null)
        }
    }

    override suspend fun getAllUsers(
        token: String,
        page: Int?,
        limit: Int?,
        city: String?,
        gender: String?,
        minAge: Int?,
        maxAge: Int?
    ): Resource<UsersResponse> {
        return try {
            val response = apiService.getAllUsers(
                page ?: 1,
                limit ?: 20,
                city,
                gender,
                minAge,
                maxAge,
                "Bearer $token"
            )
            if (response.isSuccessful) {
                Resource.Success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = try {
                    Gson().fromJson(errorBody, ErrorResponse::class.java).message
                } catch (e: Exception) {
                    "Erro desconhecido ao buscar usuários"
                }
                Resource.Error(errorMessage, null)
            }
        } catch (e: Exception) {
            Resource.Error("Erro de rede: ${e.localizedMessage}", null)
        }
    }

    override suspend fun updateUserProfile(userId: String, request: UpdateUserRequest, token: String): Resource<UserData> {
        return try {
            val response = apiService.updateUserProfile(userId, request, "Bearer $token")
            if (response.isSuccessful) {
                Resource.Success(response.body()!!.user)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = try {
                    Gson().fromJson(errorBody, ErrorResponse::class.java).message
                } catch (e: Exception) {
                    "Erro desconhecido ao atualizar perfil"
                }
                Resource.Error(errorMessage, null)
            }
        } catch (e: Exception) {
            Resource.Error("Erro de rede: ${e.localizedMessage}", null)
        }
    }

    override suspend fun uploadProfilePicture(userId: String, imageFile: File, token: String): Resource<String> {
        return try {
            val requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("picture", imageFile.name, requestFile)
            val response = apiService.uploadProfilePicture(userId, body, "Bearer $token")

            if (response.isSuccessful) {
                Resource.Success(response.body()?.imageUrl ?: "Upload bem-sucedido, mas URL não retornada")
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = try {
                    Gson().fromJson(errorBody, ErrorResponse::class.java).message
                } catch (e: Exception) {
                    "Erro desconhecido ao fazer upload da imagem"
                }
                Resource.Error(errorMessage, null)
            }
        } catch (e: Exception) {
            Resource.Error("Erro de rede: ${e.localizedMessage}", null)
        }
    }
}
