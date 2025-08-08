package com.example.codechat.domain.usecase

import android.net.Uri
import com.example.codechat.domain.repository.ProfileRepository // Ensure this import is correct
import javax.inject.Inject

class UpdateProfileImageUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(userId: String, profileImage: Uri): Result<String> {
        return try {
            val newImageUrl = profileRepository.updateProfileImage(userId, profileImage)
            Result.success(newImageUrl.url)
        } catch (e: Exception) {
            // Log the exception e.g., Timber.e(e, "Error updating profile image")
            Result.failure(e)
        }
    }
}
