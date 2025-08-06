package com.example.codechat.domain.usecase

import com.example.codechat.domain.model.User
import com.example.codechat.domain.repository.AuthRepository // Added import
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import javax.inject.Inject


@Module
@InstallIn(ViewModelComponent::class)
class RegisterUseCase @Inject constructor(
    private val repo: AuthRepository
) {
    suspend operator fun invoke(name: String, email: String, password: String): User {
       return repo.registerUser(name, email, password)
    }
}