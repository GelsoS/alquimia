package com.alquimia.di

import com.alquimia.data.remote.ApiService
import com.alquimia.data.repository.AuthRepository
import com.alquimia.data.repository.AuthRepositoryImpl
import com.alquimia.data.repository.ChatRepository
import com.alquimia.data.repository.ChatRepositoryImpl
import com.alquimia.data.repository.UserRepository
import com.alquimia.data.repository.UserRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // REMOVIDOS os provedores de repositório duplicados.
    // Eles agora são fornecidos exclusivamente no NetworkModule.kt
}
