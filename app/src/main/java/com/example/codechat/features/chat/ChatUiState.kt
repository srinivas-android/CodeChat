package com.example.codechat.features.chat

import androidx.datastore.preferences.protobuf.Timestamp

data class ChatUiState(
    val isLoading: Boolean = false,
    val messages: List<ChatMessage> = emptyList(),
    val errorMessage: String? = null,
    val newMessageText: String = ""
)

data class ChatMessage (
    val id: String,
    val sender: String,
    val content: String,
    val timestamp: Long
)