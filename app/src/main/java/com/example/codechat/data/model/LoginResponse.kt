package com.example.codechat.data.model

data class LoginResponse(
    val id: Int,
    val name: String,
    val email: String,
    val token: String,
    val exists: Boolean
)