package com.example.codechat.core.network

import com.example.codechat.data.model.ProfileImageResponse
import com.example.codechat.domain.model.User
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ProfileApi {

    @GET("get-users")
    suspend fun getUsers(): List<User>

    @Multipart
    @POST("update-profile-image")
    suspend fun updateProfileImage(
        @Part("id") userId: RequestBody,
        @Part profile: MultipartBody.Part
    ): ProfileImageResponse

}