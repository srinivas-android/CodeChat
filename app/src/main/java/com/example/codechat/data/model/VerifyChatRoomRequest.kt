package com.example.codechat.data.model

data class VerifyChatRoomRequest(
    val userId: String // Or Int, ID of the other user to verify/create room with
    // Add other relevant fields if needed by your API
)
