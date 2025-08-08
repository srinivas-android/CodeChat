package com.example.codechat.data.model // Assuming data.model for request/response classes


import com.google.gson.annotations.SerializedName

// Assuming "user: {id:1}" means just sending the user ID as part of the request.
// If the full user object is expected, change `UserRef` to `UserDto`.
data class UserRef(
    @SerializedName("id") val id: String
)

data class SendMessageRequest(
    @SerializedName("message") val message: String,
    @SerializedName("roomId") val roomId: Int,
    @SerializedName("user") val user: UserRef // Or UserDto if full object needed
)