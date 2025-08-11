package com.example.codechat.data.model


import com.google.gson.annotations.SerializedName

data class GetRoomChatsResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("chats") val chats: List<MessageDto>
)
