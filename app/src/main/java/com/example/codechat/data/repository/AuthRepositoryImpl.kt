package com.example.codechat.data.repository

import com.example.codechat.core.network.AuthApiService
import com.example.codechat.core.utils.TokenManager
import com.example.codechat.data.model.LoginRequest
import com.example.codechat.data.model.RegisterRequest
import com.example.codechat.domain.model.User
import com.example.codechat.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApiService,
    private val tokenManager: TokenManager
): AuthRepository {

    override suspend fun login(email: String, password: String): User {
        val response = api.login(LoginRequest(email, password))
        if (!response.exists || response.token.isNullOrBlank() || response.id.toString().isNullOrBlank()) {
            throw Exception("Invalid credentials or missing user data")
        }

        tokenManager.saveAuthData(token = response.token, userId = response.id)

        return User(
            id = response.id,
            name = response.name,
            email = response.email,
            token = response.token
        )
    }

    override suspend fun registerUser(name: String, email: String, password: String): User {
        val response = api.registerUser(RegisterRequest(name, email, password))

        if (response.token.isNullOrBlank() || response.id.toString().isNullOrBlank()) {
             throw Exception("Registration failed to return valid user data")
        }

//         tokenManager.saveAuthData(token = response.token, userId = response.id)


        return User(
            id = response.id,
            name = response.name,
            email = response.email,
            token = response.token
        )
    }
}
