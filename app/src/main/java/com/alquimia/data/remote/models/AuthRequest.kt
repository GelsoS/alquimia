package com.alquimia.data.remote.models

import com.google.gson.annotations.SerializedName

// Requisição de Registro
data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String,
    val age: Int,
    val city: String,
    val gender: String,
    val interests: List<String>? = null
)

// Requisição de Login
data class LoginRequest(
    val email: String,
    val password: String
)

// Requisição de Login Social (Google/Facebook)
data class SocialLoginRequest(
    val token: String // Pode ser ID Token para Google, Access Token para Facebook
)

// Requisição de Recuperação de Senha
data class ForgotPasswordRequest(
    val email: String
)
