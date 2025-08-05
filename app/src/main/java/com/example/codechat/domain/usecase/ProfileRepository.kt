package com.example.codechat.domain.usecase

import com.example.codechat.data.model.ProfileImageResponse
import com.example.codechat.domain.model.User
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface ProfileRepository {

    suspend fun getUsers(): List<User>
    suspend fun updateProfileImage(userId: RequestBody, profileImage: MultipartBody.Part): ProfileImageResponse



}