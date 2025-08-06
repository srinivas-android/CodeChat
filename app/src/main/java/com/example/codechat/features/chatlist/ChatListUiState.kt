package com.example.codechat.features.chatlist

import com.example.codechat.domain.model.ChatRoom

data class ChatListUiState(
    val chatRooms: List<ChatRoom> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
