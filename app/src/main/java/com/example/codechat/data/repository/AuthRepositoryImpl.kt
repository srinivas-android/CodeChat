package com.example.codechat.data.repository

import com.example.codechat.core.network.AuthApiService
import com.example.codechat.data.model.LoginRequest
import com.example.codechat.domain.model.User
import com.example.codechat.domain.usecase.AuthRepository

class AuthRepositoryImpl(
    private val api: AuthApiService
): AuthRepository {
    override suspend fun login(email: String, password: String): User {
        val response = api.login(LoginRequest(email, password))
        return User(
            id = response.id,
            name = response.name,
            email = response.email,
            token = response.token
        )
    }
}