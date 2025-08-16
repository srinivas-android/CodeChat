package com.example.codechat.domain.model

data class Message(
    val id: String,
    val roomId: String,
    val senderId: String,
    val senderName: String?,
    val content: String,
    val timestamp: Long,
    val isSentByCurrentUser: Boolean = false,
    val senderProfileImage: String?
)
