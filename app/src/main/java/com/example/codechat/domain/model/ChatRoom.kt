package com.example.codechat.domain.model

data class ChatRoom(
    val id: String,
    val name: String?,
    val lastMessage: String?,
    val lastMessageTimestamp: Long?,
    val participants: List<User>? = null,
    val partnerUser: User,
    val unreadCount: Int,
    val profileImageUrl: String?
    // Add other relevant fields like unreadCount, roomImageUrl, etc.
)
