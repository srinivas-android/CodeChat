package com.example.codechat.data.model

import com.google.gson.annotations.SerializedName

data class GetUserChatRoomsResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("rooms") val rooms: List<ChatRoomDto>
)