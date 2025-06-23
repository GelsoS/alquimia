package com.alquimia.data.repository

import com.alquimia.data.model.AuthResponse
import com.alquimia.data.model.LoginRequest
import com.alquimia.data.model.RegisterRequest
import com.alquimia.data.network.ApiService
import com.alquimia.utils.Resource
import com.google.gson.Gson
import javax.inject.Inject

interface AuthRepository {
    suspend fun loginUser(request: LoginRequest, token: String): Resource<AuthResponse>
    suspend fun registerUser(request: RegisterRequest, token: String): Resource<AuthResponse>
    suspend fun loginWithGoogle(googleToken: String, token: String): Resource<AuthResponse>
    suspend fun loginWithFacebook(facebookToken: String, token: String): Resource<AuthResponse>
    suspend fun forgotPassword(email: String, token: String): Resource<AuthResponse>
}

class AuthRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : AuthRepository {

    override suspend fun loginUser(request: LoginRequest, token: String): Resource<AuthResponse> {
        return try {
            val response = apiService.loginUser(request, token)
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

    override suspend fun registerUser(request: RegisterRequest, token: String): Resource<AuthResponse> {
        return try {
            val response = apiService.registerUser(request, token)
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

    override suspend fun loginWithGoogle(googleToken: String, token: String): Resource<AuthResponse> {
        return try {
            val response = apiService.loginWithGoogle(googleToken, token)
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

    override suspend fun loginWithFacebook(facebookToken: String, token: String): Resource<AuthResponse> {
        return try {
            val response = apiService.loginWithFacebook(facebookToken, token)
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

    override suspend fun forgotPassword(email: String, token: String): Resource<AuthResponse> {
        return try {
            val response = apiService.forgotPassword(email, token)
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
