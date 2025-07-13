package com.alquimia.di

import android.content.Context
import com.alquimia.data.local.SharedPreferencesManager
import com.alquimia.data.remote.ApiService
import com.alquimia.data.remote.AuthInterceptor
import com.alquimia.data.remote.TokenManager
import com.alquimia.data.repository.AuthRepository
import com.alquimia.data.repository.AuthRepositoryImpl // Certifique-se de que esta importação está presente
import com.alquimia.data.repository.ChatRepository
import com.alquimia.data.repository.ChatRepositoryImpl
import com.alquimia.data.repository.UserRepository
import com.alquimia.data.repository.UserRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideSharedPreferencesManager(
        @ApplicationContext context: Context
    ): SharedPreferencesManager {
        return SharedPreferencesManager(context)
    }

    @Provides
    @Singleton
    fun provideAuthInterceptor(
        sharedPreferencesManager: SharedPreferencesManager
    ): AuthInterceptor {
        TokenManager.initialize(sharedPreferencesManager)
        return AuthInterceptor()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        authInterceptor: AuthInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://192.168.3.19:3000/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    // CORREÇÃO AQUI: Fornecer AuthRepositoryImpl como implementação de AuthRepository
    @Provides
    @Singleton
    fun provideAuthRepository(apiService: ApiService): AuthRepository {
        return AuthRepositoryImpl(apiService)
    }

    // CORREÇÃO AQUI: Fornecer UserRepositoryImpl como implementação de UserRepository
    @Provides
    @Singleton
    fun provideUserRepository(apiService: ApiService): UserRepository {
        return UserRepositoryImpl(apiService)
    }

    // CORREÇÃO AQUI: Fornecer ChatRepositoryImpl como implementação de ChatRepository
    @Provides
    @Singleton
    fun provideChatRepository(apiService: ApiService): ChatRepository {
        return ChatRepositoryImpl(apiService)
    }
}
