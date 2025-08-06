package com.example.codechat.data.model

import com.example.codechat.domain.model.ChatRoom

data class VerifyChatRoomResponse(
    val room: ChatRoom?, // If the API returns the created/verified room details
    val exists: Boolean,
    val message: String?
)
