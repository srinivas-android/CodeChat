package com.example.codechat.data.model


import com.google.gson.annotations.SerializedName

data class SendMessageResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: ChatMessageDto // The sent message details
)