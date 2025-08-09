package com.example.codechat.features.chat

import androidx.datastore.preferences.protobuf.Timestamp
import com.example.codechat.domain.model.Message
import com.example.codechat.domain.model.User

data class ChatUiState(
    val isLoadingMessages: Boolean = false,
    val messages: List<Message> = emptyList(),
    val currentMessageInput: String = "",
    val isSendingMessage: Boolean = false,
    val errorMessage: String? = null,
    val partnerUser: User? = null,
    val currentRoomId: String? = null,
    val navigationTitle: String = "Chat",
    val newMessageText: String = ""
)

data class ChatMessage (
    val id: String,
    val sender: String,
    val content: String,
    val timestamp: Long
)