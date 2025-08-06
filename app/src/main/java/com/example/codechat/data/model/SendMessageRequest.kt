package com.example.codechat.data.model // Assuming data.model for request/response classes

data class SendMessageRequest(
    val roomId: String, // Or Int
    val message: String
    // Potentially senderId if not inferred from auth token by backend
)
