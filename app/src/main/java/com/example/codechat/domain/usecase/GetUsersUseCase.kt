package com.example.codechat.domain.usecase

import com.example.codechat.domain.model.User
import com.example.codechat.domain.repository.ProfileRepository // Added import
// Unused imports related to ProfileImageResponse and okhttp3 can be removed if not used elsewhere in this file.
// For now, keeping them as they were in the original read.
import com.example.codechat.data.model.ProfileImageResponse 
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class GetUsersUseCase @Inject constructor(
    private val repo: ProfileRepository) {
    suspend operator fun invoke (): List<User> = repo.getUsers()
//    suspend operator fun invoke (userId: RequestBody, profileImage: MultipartBody.Part): ProfileImageResponse {
//        return repo.updateProfileImage(userId, profileImage)
//    }
}