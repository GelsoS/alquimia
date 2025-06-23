package com.alquimia.data.remote.models

import com.google.gson.annotations.SerializedName

// Resposta de Autenticação (Login/Registro)
data class AuthResponse(
    val success: Boolean,
    val message: String,
    val token: String? = null, // JWT token do backend
    val userId: String? = null,
    val email: String? = null
)

// Resposta de Erro Genérica
data class ErrorResponse(
    val success: Boolean,
    val message: String,
    val errors: List<String>? = null
)
