package com.alquimia.data.remote.models

import com.google.gson.annotations.SerializedName

// Resposta de autenticação (login/registro)
data class AuthResponse(
    val success: Boolean,
    val message: String,
    val token: String? = null,
    @SerializedName("userId")
    val userId: String? = null,
    val email: String? = null
)

// Requisição de login
data class LoginRequest(
    val email: String,
    val password: String
)

// Requisição de registro
data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String,
    val age: Int,
    val city: String,
    val gender: String,
    val interests: List<String>? // Tornar interests anulável para flexibilidade
)

// Requisição de login social (Google/Facebook)
data class SocialLoginRequest(
    val token: String
)

// Requisição de recuperação de senha
data class ForgotPasswordRequest(
    val email: String
)

// Modelo para respostas de erro genéricas da API
data class ErrorResponse(
    val success: Boolean,
    val message: String,
    val errors: List<String>? = null // Para erros de validação, se o backend retornar
)
