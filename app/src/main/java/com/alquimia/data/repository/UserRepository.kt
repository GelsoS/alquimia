package com.alquimia.data.repository

import com.alquimia.data.models.User
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val postgrest: Postgrest,
    private val storage: Storage
) {

    suspend fun getAllUsers(): Result<List<User>> {
        return try {
            val users = postgrest["users"]
                .select()
                .decodeList<User>()
            Result.success(users)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserById(userId: String): Result<User?> {
        return try {
            val users = postgrest["users"]
                .select { eq("id", userId) }
                .decodeList<User>()

            Result.success(users.firstOrNull())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Nova função para inserir um usuário na tabela 'users'
    suspend fun insertUser(user: User): Result<User> {
        return try {
            val insertedUsers = postgrest["users"]
                .insert(user)
                .decodeList<User>()
            Result.success(insertedUsers.first())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUser(user: User): Result<User> {
        return try {
            postgrest["users"]
                .update(user) {
                    eq("id", user.id)
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

            bucket.upload(path, imageData)

            val publicUrl = bucket.publicUrl(path)

            Result.success(publicUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProfilePicture(userId: String, imageUrl: String?): Result<Unit> {
        return try {
            val currentUserResult = getUserById(userId)
            currentUserResult.getOrThrow()?.let { currentUser ->
                val updatedUser = currentUser.copy(profile_picture = imageUrl)
                updateUser(updatedUser).getOrThrow()
                Result.success(Unit)
            } ?: Result.failure(Exception("Usuário não encontrado"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
