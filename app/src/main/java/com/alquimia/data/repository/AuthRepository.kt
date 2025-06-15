package com.alquimia.data.repository

import com.alquimia.data.models.User
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.postgrest.Postgrest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val goTrue: Auth,
    private val postgrest: Postgrest
) {

    suspend fun signInWithEmail(email: String, password: String): Result<User> {
        return try {
            // Fazer login no Supabase Auth
            goTrue.signInWith(Email) {
                this.email = email
                this.password = password
            }

            // Buscar dados do usuário na tabela users
            val currentUser = getCurrentUser()
            if (currentUser != null) {
                Result.success(currentUser)
            } else {
                Result.failure(Exception("Usuário não encontrado na base de dados"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signUpWithEmail(email: String, password: String, name: String): Result<User> {
        return try {
            // Criar conta no Supabase Auth
            val response = goTrue.signUpWith(Email) {
                this.email = email
                this.password = password
            }

            // Na versão atual do SDK, precisamos acessar o ID do usuário de forma diferente
            val session = goTrue.currentSessionOrNull()
            val userId = session?.user?.id ?: throw Exception("ID do usuário não encontrado")

            // Criar perfil do usuário na tabela users
            val user = User(
                id = userId,
                email = email,
                name = name,
                age = 25, // Valor padrão, pode ser editado depois
                bio = "Novo no Alquimia!",
                location = "Brasil",
                profile_pictures = emptyList(),
                interests = emptyList(),
                description = "Olá! Acabei de me juntar ao Alquimia."
            )

            postgrest["users"].insert(user)

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            // O método correto é resetPasswordForEmail
            goTrue.resetPasswordForEmail(email)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCurrentUser(): User? {
        return try {
            val userId = goTrue.currentSessionOrNull()?.user?.id ?: return null

            postgrest["users"]
                .select {
                    filter {
                        eq("id", userId)
                    }
                }
                .decodeSingle<User>()
        } catch (e: Exception) {
            null
        }
    }


    suspend fun signOut(): Result<Unit> {
        return try {
            // O método correto é signOut, não logout
            goTrue.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun isUserLoggedIn(): Boolean {
        return goTrue.currentSessionOrNull() != null
    }
}
