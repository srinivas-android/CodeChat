package com.example.codechat.data.repository

import com.example.codechat.core.network.AuthApiService
import com.example.codechat.data.model.LoginRequest
import com.example.codechat.data.model.RegisterRequest
import com.example.codechat.domain.model.User
import com.example.codechat.domain.usecase.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
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

    override suspend fun registerUser(name: String, email: String, password: String): User {
        val response = api.registerUser(RegisterRequest(name, email, password))
        return User(
            id = response.id,
            name = response.name,
            email = response.email,
            token = response.token
        )
    }
}