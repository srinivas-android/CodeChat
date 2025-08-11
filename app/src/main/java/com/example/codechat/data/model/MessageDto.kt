package com.example.codechat.data.model

import com.google.gson.annotations.SerializedName

data class MessageDto(
    @SerializedName("id") val id: Int,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("room_id") val roomId: Int,
    @SerializedName("message") val message: String,
    @SerializedName("user") val user: UserDto
)
