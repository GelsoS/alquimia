package com.alquimia.data.repository

import com.alquimia.data.remote.ApiService
import com.alquimia.data.remote.models.AuthResponse
import com.alquimia.data.remote.models.ForgotPasswordRequest
import com.alquimia.data.remote.models.LoginRequest
import com.alquimia.data.remote.models.RegisterRequest
import com.alquimia.data.remote.models.SocialLoginRequest
import com.alquimia.util.Resource
import com.google.gson.Gson
import javax.inject.Inject

interface AuthRepository {
    // Removido o parâmetro 'token'
    suspend fun loginUser(request: LoginRequest): Resource<AuthResponse>
    suspend fun registerUser(request: RegisterRequest): Resource<AuthResponse>
    suspend fun loginWithGoogle(request: SocialLoginRequest): Resource<AuthResponse>
    suspend fun loginWithFacebook(request: SocialLoginRequest): Resource<AuthResponse>
    suspend fun forgotPassword(request: ForgotPasswordRequest): Resource<AuthResponse>
}

class AuthRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : AuthRepository {

    override suspend fun loginUser(request: LoginRequest): Resource<AuthResponse> {
        return try {
            val response = apiService.loginUser(request) // Removido o argumento 'token'
            if (response.isSuccessful) {
                Resource.Success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = try {
                    Gson().fromJson(errorBody, AuthResponse::class.java).message
                } catch (e: Exception) {
                    "Erro desconhecido no login"
                }
                Resource.Error(errorMessage)
            }
        } catch (e: Exception) {
            Resource.Error("Erro de rede: ${e.localizedMessage}")
        }
    }

    override suspend fun registerUser(request: RegisterRequest): Resource<AuthResponse> {
        return try {
            val response = apiService.registerUser(request) // Removido o argumento 'token'
            if (response.isSuccessful) {
                Resource.Success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = try {
                    Gson().fromJson(errorBody, AuthResponse::class.java).message
                } catch (e: Exception) {
                    "Erro desconhecido no registro"
                }
                Resource.Error(errorMessage)
            }
        } catch (e: Exception) {
            Resource.Error("Erro de rede: ${e.localizedMessage}")
        }
    }

    override suspend fun loginWithGoogle(request: SocialLoginRequest): Resource<AuthResponse> {
        return try {
            val response = apiService.loginWithGoogle(request) // Removido o argumento 'token'
            if (response.isSuccessful) {
                Resource.Success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = try {
                    Gson().fromJson(errorBody, AuthResponse::class.java).message
                } catch (e: Exception) {
                    "Erro desconhecido no login com Google"
                }
                Resource.Error(errorMessage)
            }
        } catch (e: Exception) {
            Resource.Error("Erro de rede: ${e.localizedMessage}")
        }
    }

    override suspend fun loginWithFacebook(request: SocialLoginRequest): Resource<AuthResponse> {
        return try {
            val response = apiService.loginWithFacebook(request) // Removido o argumento 'token'
            if (response.isSuccessful) {
                Resource.Success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = try {
                    Gson().fromJson(errorBody, AuthResponse::class.java).message
                } catch (e: Exception) {
                    "Erro desconhecido no login com Facebook"
                }
                Resource.Error(errorMessage)
            }
        } catch (e: Exception) {
            Resource.Error("Erro de rede: ${e.localizedMessage}")
        }
    }

    override suspend fun forgotPassword(request: ForgotPasswordRequest): Resource<AuthResponse> {
        return try {
            val response = apiService.forgotPassword(request) // Removido o argumento 'token'
            if (response.isSuccessful) {
                Resource.Success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = try {
                    Gson().fromJson(errorBody, AuthResponse::class.java).message
                } catch (e: Exception) {
                    "Erro desconhecido ao tentar recuperar a senha"
                }
                Resource.Error(errorMessage)
            }
        } catch (e: Exception) {
            Resource.Error("Erro de rede: ${e.localizedMessage}")
        }
    }
}
