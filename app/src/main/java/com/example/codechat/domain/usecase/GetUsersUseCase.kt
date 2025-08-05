package com.example.codechat.domain.usecase

import com.example.codechat.data.model.ProfileImageResponse
import com.example.codechat.domain.model.User
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