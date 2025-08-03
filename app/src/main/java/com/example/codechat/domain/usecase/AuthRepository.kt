package com.example.codechat.domain.usecase

import com.example.codechat.domain.model.User

interface AuthRepository {
    suspend fun login(email: String, password: String): User
}