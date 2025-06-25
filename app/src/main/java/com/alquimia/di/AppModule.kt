package com.alquimia.di

import com.alquimia.data.remote.ApiService
import com.alquimia.data.repository.AuthRepository
import com.alquimia.data.repository.AuthRepositoryImpl // Importar a implementação
import com.alquimia.data.repository.ChatRepository
import com.alquimia.data.repository.ChatRepositoryImpl // Importar a implementação
import com.alquimia.data.repository.UserRepository
import com.alquimia.data.repository.UserRepositoryImpl // Importar a implementação
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // Remover provideSupabaseClient
    // @Provides
    // @Singleton
    // fun provideSupabaseClient(): SupabaseClient { ... }

    // Remover provideGoTrue, providePostgrest, provideStorage
    // @Provides
    // @Singleton
    // fun provideGoTrue(client: SupabaseClient): GoTrue { ... }
    // @Provides
    // @Singleton
    // fun providePostgrest(client: SupabaseClient): Postgrest { ... }
    // @Provides
    // @Singleton
    // fun provideStorage(client: SupabaseClient): Storage { ... }

    @Provides
    @Singleton
    fun provideAuthRepository(apiService: ApiService): AuthRepository { // Injetar ApiService
        return AuthRepositoryImpl(apiService) // Retornar a implementação
    }

    @Provides
    @Singleton
    fun provideUserRepository(apiService: ApiService): UserRepository { // Injetar ApiService
        return UserRepositoryImpl(apiService) // Retornar a implementação
    }

    @Provides
    @Singleton
    fun provideChatRepository(apiService: ApiService): ChatRepository { // Injetar ApiService
        return ChatRepositoryImpl(apiService) // Retornar a implementação
    }
}
