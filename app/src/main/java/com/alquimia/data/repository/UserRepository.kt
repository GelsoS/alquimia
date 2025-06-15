package com.alquimia.data.repository

import com.alquimia.data.models.User
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.storage.Storage
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val postgrest: Postgrest,
    private val storage: Storage
) {

    suspend fun getAllUsers(): List<User> {
        return try {
            postgrest["users"]
                .select(columns = Columns.ALL)
                .decodeList<User>()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getUserById(userId: String): User? {
        return try {
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

    suspend fun updateUser(user: User): Result<User> {
        return try {
            postgrest["users"]
                .update(user) {
                    filter {
                        eq("id", user.id)
                    }
                }
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun uploadProfilePicture(userId: String, imageData: ByteArray, fileName: String): Result<String> {
        return try {
            val bucket = storage["profile-pictures"]
            val path = "$userId/$fileName"

            // Upload da imagem
            bucket.upload(path, imageData)

            // Obter URL pública
            val publicUrl = bucket.publicUrl(path)

            Result.success(publicUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addProfilePicture(userId: String, imageUrl: String): Result<Unit> {
        return try {
            // Buscar usuário atual
            val currentUser = getUserById(userId)
            if (currentUser != null) {
                // Adicionar nova foto à lista
                val updatedPictures = currentUser.profile_pictures + imageUrl
                val updatedUser = currentUser.copy(profile_pictures = updatedPictures)

                // Atualizar no banco
                updateUser(updatedUser)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Usuário não encontrado"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun removeProfilePicture(userId: String, imageUrl: String): Result<Unit> {
        return try {
            // Buscar usuário atual
            val currentUser = getUserById(userId)
            if (currentUser != null) {
                // Remover foto da lista
                val updatedPictures = currentUser.profile_pictures.filter { it != imageUrl }
                val updatedUser = currentUser.copy(profile_pictures = updatedPictures)

                // Atualizar no banco
                updateUser(updatedUser)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Usuário não encontrado"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
