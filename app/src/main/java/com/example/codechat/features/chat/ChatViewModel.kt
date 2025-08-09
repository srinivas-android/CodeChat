package com.example.codechat.features.chat

import androidx.activity.result.launch
import androidx.compose.animation.core.copy
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codechat.domain.usecase.GetRoomMessagesUseCase
import com.example.codechat.domain.usecase.SendMessageUseCase
import com.example.codechat.domain.usecase.VerifyChatRoomUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChatViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getRoomMessagesUseCase: GetRoomMessagesUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val verifyChatRoomUseCase: VerifyChatRoomUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    // Consider if you need a mutable list for adding messages optimistically
    // val messagesList = mutableStateListOf<Message>() // Alternative for optimistic updates

    init {
        val roomIdArg: String? = savedStateHandle.get<String>("roomId")
        val chatPartnerUserIdArg: String? =
            savedStateHandle.get<String>("chatUserId") // For new chats

        if (roomIdArg != null) {
            _uiState.update { it.copy(currentRoomId = roomIdArg) }
            loadMessages(roomIdArg)
            // You might want to fetch partner user details here too if not passed via nav
        } else if (chatPartnerUserIdArg != null) {
            // This is a new chat, verify/create the room first
            initializeNewChat(chatPartnerUserIdArg)
        } else {
            _uiState.update { it.copy(errorMessage = "Room ID or Chat User ID not provided.") }
        }
    }

        private fun initializeNewChat(chatPartnerUserId: String) {
            viewModelScope.launch {
                _uiState.update { it.copy(isLoadingMessages = true, errorMessage = null) }
                try {
                    val roomId = verifyChatRoomUseCase(chatPartnerUserId) // Assumes invoke operator
                    _uiState.update { it.copy(currentRoomId = roomId.roomId.toString()) }
                    loadMessages(roomId.roomId.toString())
                    // Fetch partner user details here to set name, profile image in UI
                } catch (e: Exception) {
                    _uiState.update {
                        it.copy(
                            isLoadingMessages = false,
                            errorMessage = e.message ?: "Could not initialize chat."
                        )
                    }
                }
            }
        }

        fun loadMessages(roomId: String) {
            viewModelScope.launch {
                _uiState.update { it.copy(isLoadingMessages = true, errorMessage = null) }
                try {
                    val fetchedMessages = getRoomMessagesUseCase(roomId)
                    _uiState.update {
                        it.copy(
                            isLoadingMessages = false,
                            messages = fetchedMessages
                        )
                    }
                    // messagesList.clear()
                    // messagesList.addAll(fetchedMessages)
                } catch (e: Exception) {
                    _uiState.update {
                        it.copy(
                            isLoadingMessages = false,
                            errorMessage = e.message ?: "Failed to load messages."
                        )
                    }
                }
            }
        }

        fun onMessageInputChange(newInput: String) {
            _uiState.update { it.copy(currentMessageInput = newInput) }
        }

        fun sendMessage() {
            val currentRoom = _uiState.value.currentRoomId
            val messageText = _uiState.value.currentMessageInput.trim()

            if (currentRoom == null || messageText.isEmpty()) {
                // Optionally set an error message or just ignore
                return
            }

            viewModelScope.launch {
                _uiState.update { it.copy(isSendingMessage = true, errorMessage = null) }
                try {
                    val sentMessage = sendMessageUseCase(
                        roomId = currentRoom,
                        messageText = messageText
                    ) // Assumes invoke operator
                    _uiState.update {
                        it.copy(
                            isSendingMessage = false,
                            messages = it.messages + sentMessage, // Append new message
                            currentMessageInput = "" // Clear input field
                        )
                    }
                    // If using mutableStateListOf:
                    // messagesList.add(sentMessage)
                    // _uiState.update { it.copy(currentMessageInput = "") }


                } catch (e: Exception) {
                    _uiState.update {
                        it.copy(
                            isSendingMessage = false,
                            errorMessage = e.message ?: "Failed to send message."
                            // Consider how to handle the message that failed to send (e.g., keep in input field)
                        )
                    }
                }
            }
        }


//    fun onMessageTextChanged(text: String) {
//        uiState = uiState.copy(newMessageText = text)
//    }
}