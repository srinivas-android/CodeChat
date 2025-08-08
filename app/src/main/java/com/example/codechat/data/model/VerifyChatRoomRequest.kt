package com.example.codechat.data.model


import com.google.gson.annotations.SerializedName

data class VerifyChatRoomRequest(
    @SerializedName("userId") val userId: String,
    @SerializedName("chatUserId") val chatUserId: Int
)
