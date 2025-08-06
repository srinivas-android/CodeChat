package com.example.codechat.data.repository

import com.example.codechat.core.network.AuthApiService
import com.example.codechat.core.network.ProfileApi
import com.example.codechat.data.model.ProfileImageResponse
import com.example.codechat.domain.model.User
import com.example.codechat.domain.repository.ProfileRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val profileApi: ProfileApi,
    private val authApiService: AuthApiService
): ProfileRepository {

    override suspend fun getMyProfile(userId: String): User {
        val response = authApiService.getMyProfile(id = userId)
        return User(
            id = response.id,
            name = response.name ?: "Unknown",
            email = response.email ?: "",
            token = response.token ?: "",
            profileImage = response.profileImage ?: ""
        )
    }

    override suspend fun getUsers(): List<User> = profileApi.getUsers()

    override suspend fun updateProfileImage(
        userId: RequestBody,
        profileImage: MultipartBody.Part
    ): ProfileImageResponse {
       return profileApi.updateProfileImage(userId, profileImage)
    }

}
