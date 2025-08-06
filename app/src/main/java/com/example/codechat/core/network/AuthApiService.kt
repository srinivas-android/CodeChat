package com.example.codechat.core.network

import com.example.codechat.data.model.LoginRequest
import com.example.codechat.data.model.LoginResponse
import com.example.codechat.data.model.RegisterRequest
import com.example.codechat.data.model.RegisterResponse
import com.example.codechat.domain.model.User
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface AuthApiService {
    @POST("verify-user")
    suspend fun login(@Body body: LoginRequest): LoginResponse

    @POST("register-user")
    suspend fun registerUser(@Body body: RegisterRequest): RegisterResponse

    // New function for getting user profile
    @GET("user")
    suspend fun getMyProfile(@Query("id") id: String): User
}