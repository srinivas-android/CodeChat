package com.example.codechat.domain.repository // New package

import android.net.Uri
import com.example.codechat.data.model.ProfileImageResponse
import com.example.codechat.domain.model.User
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface ProfileRepository {
    suspend fun getMyProfile(userId: String): User
    suspend fun getUsers(): List<User>
    suspend fun updateProfileImage(userId: String, profileImage: Uri): ProfileImageResponse
}
