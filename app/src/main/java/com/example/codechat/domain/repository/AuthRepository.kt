package com.example.codechat.domain.repository // Changed package

import com.example.codechat.domain.model.User

interface AuthRepository {
    suspend fun login(email: String, password: String): User

    suspend fun registerUser(name: String, email: String, password: String): User

}