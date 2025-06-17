package com.alquimia.data.repository

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.alquimia.BuildConfig
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.gotrue.providers.Google
import io.github.jan.supabase.gotrue.user.UserSession
import io.github.jan.supabase.postgrest.Postgrest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val goTrue: GoTrue,
    private val postgrest: Postgrest
) {

    suspend fun signInWithEmail(email: String, password: String): Result<Unit> = try {
        goTrue.loginWith(Email) {
            this.email = email
            this.password = password
        }
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun signUpWithEmail(email: String, password: String): Result<Unit> = try {
        goTrue.signUpWith(Email) {
            this.email = email
            this.password = password
        }
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    /**
     * Realiza login com Google usando ID token obtido via Credentials API.
     */
    suspend fun signInWithGoogle(context: Context): Result<Unit> = try {
        val credentialManager = CredentialManager.create(context)

        val googleIdOption = GetGoogleIdOption.Builder()
            .setServerClientId(BuildConfig.GOOGLE_WEB_CLIENT_ID)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        val result = credentialManager.getCredential(context, request)
        val credential = result.credential
        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
        val idTokenValue = googleIdTokenCredential.idToken ?: throw Exception("ID Token is null")

        // Aqui passamos o token para o provider Google na autenticação do Supabase
        goTrue.signInWith(Google) {
            this.idToken = idTokenValue
        }
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun resetPassword(email: String): Result<Unit> = try {
        goTrue.sendRecoveryEmail(email)
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun signOut(): Result<Unit> = try {
        goTrue.logout()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    fun isUserLoggedIn(): Boolean = goTrue.currentSessionOrNull() != null

    fun getCurrentSession(): UserSession? = goTrue.currentSessionOrNull()
}
