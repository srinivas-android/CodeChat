package com.example.codechat.data.repository

import com.example.codechat.core.network.ProfileApi
import com.example.codechat.data.model.ProfileImageResponse
import com.example.codechat.domain.model.User
import com.example.codechat.domain.usecase.ProfileRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val api: ProfileApi
): ProfileRepository {

    override suspend fun getUsers(): List<User> = api.getUsers()

    override suspend fun updateProfileImage(
        userId: RequestBody,
        profileImage: MultipartBody.Part
    ): ProfileImageResponse {
       return api.updateProfileImage(userId, profileImage)
    }

}