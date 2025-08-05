package com.example.codechat.core.di

import com.example.codechat.core.network.ProfileApi
import com.example.codechat.data.repository.ProfileRepositoryImpl
import com.example.codechat.domain.usecase.ProfileRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideProfileRepository(api: ProfileApi): ProfileRepository {
        return ProfileRepositoryImpl(api)
    }
}