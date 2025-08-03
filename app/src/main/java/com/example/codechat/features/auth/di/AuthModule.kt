package com.example.codechat.features.auth.di

import com.example.codechat.core.network.AuthApiService
import com.example.codechat.data.repository.AuthRepositoryImpl
import com.example.codechat.domain.usecase.AuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {
    @Provides
    fun provideAuthRepository(api: AuthApiService): AuthRepository {
        return AuthRepositoryImpl(api)
    }
}