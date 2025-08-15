package com.example.codechat.core.di

import android.content.Context
import com.example.codechat.core.network.AuthApiService
import com.example.codechat.core.network.ChatApiService
import com.example.codechat.core.network.ProfileApi
import com.example.codechat.core.utils.TokenManager
import com.example.codechat.data.remote.PusherService
import com.example.codechat.data.repository.AuthRepositoryImpl
import com.example.codechat.data.repository.ChatRepositoryImpl
import com.example.codechat.data.repository.ProfileRepositoryImpl
import com.example.codechat.domain.repository.AuthRepository
import com.example.codechat.domain.repository.ChatRepository
import com.example.codechat.domain.repository.ProfileRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideAuthRepository(
        api: AuthApiService,
        tokenManager: TokenManager
    ): AuthRepository {
        return AuthRepositoryImpl(api, tokenManager)
    }

    @Provides
    @Singleton
    fun provideProfileRepository(
        profileApi: ProfileApi,
        authApiService: AuthApiService,
        @ApplicationContext context: Context
    ): ProfileRepository {
        return ProfileRepositoryImpl(profileApi, authApiService, context)
    }

    @Provides
    @Singleton
    fun provideChatRepository(
        chatApiService: ChatApiService,
        tokenManager: TokenManager,
        pusherService: PusherService
    ): ChatRepository {
        return ChatRepositoryImpl(chatApiService,tokenManager,pusherService)
    }
}
