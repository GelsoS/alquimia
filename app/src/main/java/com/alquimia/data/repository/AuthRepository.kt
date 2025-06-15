package com.alquimia.data.repository

import com.alquimia.data.models.User
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.postgrest.Postgrest // Import corrigido
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    val goTrue: GoTrue,
    private val postgrest: Postgrest
) {

    suspend fun signInWithEmail(email: String, password: String): Result<User> {
        return try {
            goTrue.loginWith(Email) {
                this.email = email
                this.password = password
            }

            // Após o login bem-sucedido, buscar o perfil do usuário na tabela 'users'
            // Se o perfil não existir, ele será criado no LoginViewModel
            val currentUser = getCurrentUser().getOrThrow()
            if (currentUser != null) {
                Result.success(currentUser)
            } else {
                // Isso pode acontecer se o perfil ainda não foi criado na tabela 'users'
                // O LoginViewModel lidará com a criação do perfil neste caso.
                Result.failure(Exception("Perfil de usuário não encontrado na base de dados. Será criado."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Esta função agora apenas cria a conta de autenticação e envia o email de confirmação.
    // A criação do perfil na tabela 'users' será feita após o primeiro login/confirmação.
    suspend fun signUpWithEmail(email: String, password: String): Result<Unit> { // Retorna Result<Unit>
        return try {
            goTrue.signUpWith(Email) {
                this.email = email
                this.password = password
            }
            Result.success(Unit) // Sucesso na criação da conta de autenticação
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            goTrue.sendRecoveryEmail(email)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCurrentUser(): Result<User?> {
        return try {
            val session = goTrue.currentSessionOrNull()
            val userId = session?.user?.id

            if (userId != null) {
                val users = postgrest["users"]
                    .select { eq("id", userId) }
                    .decodeList<User>()

                Result.success(users.firstOrNull())
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signOut(): Result<Unit> {
        return try {
            goTrue.logout()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun isUserLoggedIn(): Boolean {
        return goTrue.currentSessionOrNull() != null
    }
}
