package com.example.codechat.features.chat

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import javax.inject.Inject

class ChatViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
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