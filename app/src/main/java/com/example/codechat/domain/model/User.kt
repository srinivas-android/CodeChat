package com.example.codechat.domain.model

import com.google.gson.annotations.SerializedName

data class User(
    val id: Int?,
    val name: String?,
    val email: String?,
    val token: String?,
    @SerializedName("profile_image")
    val profileImage: String? = null
)
