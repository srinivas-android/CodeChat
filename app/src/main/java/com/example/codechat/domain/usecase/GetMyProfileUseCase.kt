package com.example.codechat.domain.usecase

import com.example.codechat.domain.model.User
import com.example.codechat.domain.repository.ProfileRepository
import javax.inject.Inject

class GetMyProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {

    suspend operator fun invoke(userId: String): User {
        return profileRepository.getMyProfile(userId)
    }
}