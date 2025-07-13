package com.alquimia.util

// Classe selada para representar o estado de um recurso (dados, erro, carregamento)
sealed class Resource<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T?) : Resource<T>(data) // Alterado para aceitar data nulo
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Loading<T>(data: T? = null) : Resource<T>(data)
}
