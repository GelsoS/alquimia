package com.alquimia.di

import io.github.jan.supabase.SupabaseClient
import com.alquimia.data.SupabaseInstance
import com.alquimia.data.repository.AuthRepository
import com.alquimia.data.repository.ChatRepository
import com.alquimia.data.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSupabaseClient(): SupabaseClient {
        return SupabaseInstance.client
    }

    @Provides
    @Singleton
    fun provideGoTrue(client: SupabaseClient): GoTrue {
        return client.gotrue
    }

    @Provides
    @Singleton
    fun providePostgrest(client: SupabaseClient): Postgrest {
        return client.postgrest
    }

    @Provides
    @Singleton
    fun provideStorage(client: SupabaseClient): Storage {
        return client.storage
    }

    @Provides
    @Singleton
    fun provideAuthRepository(goTrue: GoTrue, postgrest: Postgrest): AuthRepository {
        return AuthRepository(goTrue, postgrest)
    }

    @Provides
    @Singleton
    fun provideUserRepository(postgrest: Postgrest, storage: Storage): UserRepository {
        return UserRepository(postgrest, storage)
    }

    @Provides
    @Singleton
    fun provideChatRepository(postgrest: Postgrest): ChatRepository {
        return ChatRepository(postgrest)
    }
}
