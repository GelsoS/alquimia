package com.alquimia.di

import com.alquimia.data.remote.ApiService
import com.alquimia.data.repository.AuthRepository
import com.alquimia.data.repository.ChatRepository
import com.alquimia.data.repository.UserRepository
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
        return AuthRepository(apiService)
    }

    @Provides
    @Singleton
    fun provideUserRepository(apiService: ApiService): UserRepository { // Injetar ApiService
        // A implementação de UserRepository precisará ser atualizada para usar ApiService
        return UserRepository(apiService)
    }

    @Provides
    @Singleton
    fun provideChatRepository(apiService: ApiService): ChatRepository { // Injetar ApiService
        // A implementação de ChatRepository precisará ser atualizada para usar ApiService
        return ChatRepository(apiService)
    }
}
