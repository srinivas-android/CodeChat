package com.example.codechat.data.model

import com.google.gson.annotations.SerializedName

data class ChatRoomDto(
    @SerializedName("id") val id: Int,
    @SerializedName("user_id") val ownerUserId: Int, // The user who owns this "version" of the room link
    @SerializedName("chat_user_id") val partnerUserId: Int,
    @SerializedName("chat_user") val partnerUser: UserDto?,
    @SerializedName("chats") val chats: List<ChatMessageDto>?, // List of messages, might be partial or full
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String
)