package com.alquimia.data.remote

import com.alquimia.data.local.SharedPreferencesManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

// Gerenciador de token que agora usa SharedPreferences para persistência
@Singleton
object TokenManager {
    private lateinit var sharedPreferencesManager: SharedPreferencesManager

    fun initialize(manager: SharedPreferencesManager) {
        sharedPreferencesManager = manager
    }

    var authToken: String?
        get() = sharedPreferencesManager.getAuthToken()
        set(value) {
            if (value != null) {
                sharedPreferencesManager.saveAuthToken(value)
            } else {
                sharedPreferencesManager.clearAuthToken()
            }
        }

    var currentUserId: String?
        get() = sharedPreferencesManager.getUserId()
        set(value) {
            if (value != null) {
                sharedPreferencesManager.saveUserId(value)
            } else {
                sharedPreferencesManager.clearUserId()
            }
        }

    fun clearSession() {
        sharedPreferencesManager.clearAll()
    }
}

@Singleton
class AuthInterceptor @Inject constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = TokenManager.authToken

        val requestBuilder = originalRequest.newBuilder()

        // Adiciona o token JWT ao cabeçalho Authorization se ele existir
        // E se o cabeçalho ainda não foi adicionado (para evitar duplicação)
        if (token != null && originalRequest.header("Authorization") == null) {
            requestBuilder.header("Authorization", "Bearer $token")
        }

        return chain.proceed(requestBuilder.build())
    }
}
