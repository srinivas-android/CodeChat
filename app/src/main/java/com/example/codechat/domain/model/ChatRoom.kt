package com.example.codechat.domain.model

data class ChatRoom(
    val id: String, // Or Int, depending on your API
    val name: String?, // Could be the other user's name or a group name
    val lastMessage: String?,
    val lastMessageTimestamp: Long?, // Or String, depending on API
    val participants: List<User>? = null, // Optional, could be useful
    val partnerUser: User,
    val unreadCount: Int,
    val profileImageUrl: String?
    // Add other relevant fields like unreadCount, roomImageUrl, etc.
)
