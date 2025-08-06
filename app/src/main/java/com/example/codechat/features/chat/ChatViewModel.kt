package com.example.codechat.features.chat

import androidx.lifecycle.ViewModel

class ChatViewModel : ViewModel() {
    var uiState = ChatUiState()

    fun sendMessage() {
        if (uiState.newMessageText.isBlank()) return

        val newMessage = ChatMessage(
            id = System.currentTimeMillis().toString(),
            sender = "Me",
            content = uiState.newMessageText,
            timestamp = System.currentTimeMillis()
        )

        uiState = uiState.copy(
            messages = uiState.messages + newMessage,
            newMessageText = ""
        )

    }

    fun onMessageTextChanged(text: String) {
        uiState = uiState.copy(newMessageText = text)
    }
}