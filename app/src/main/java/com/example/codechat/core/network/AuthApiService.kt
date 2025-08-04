package com.example.codechat.core.network

import com.example.codechat.data.model.LoginRequest
import com.example.codechat.data.model.LoginResponse
import com.example.codechat.data.model.RegisterRequest
import com.example.codechat.data.model.RegisterResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("verify-user")
    suspend fun login(@Body body: LoginRequest): LoginResponse

    @POST("register-user")
    suspend fun registerUser(@Body body: RegisterRequest): RegisterResponse

}