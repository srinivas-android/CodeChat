package com.example.codechat.data.model

data class RegisterResponse(
    val id: Int,
    val name: String,
    val email: String,
    val token: String
)