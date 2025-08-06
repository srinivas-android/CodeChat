package com.example.codechat.domain.model

data class Message(
    val id: String, // Or Int
    val roomId: String, // Or Int
    val senderId: String, // Or Int
    val senderName: String?, // Denormalized for convenience
    val content: String,
    val timestamp: Long, // Or String
    val isSentByCurrentUser: Boolean = false // UI helper
)
