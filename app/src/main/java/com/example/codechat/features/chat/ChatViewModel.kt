package com.example.codechat.features.chat

import android.util.Log
import androidx.activity.result.launch
import androidx.compose.animation.core.copy
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codechat.core.utils.TokenManager
import com.example.codechat.domain.model.Message
import com.example.codechat.domain.repository.ChatRepository
import com.example.codechat.domain.usecase.GetRoomMessagesUseCase
import com.example.codechat.domain.usecase.SendMessageUseCase
import com.example.codechat.domain.usecase.VerifyChatRoomUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getRoomMessagesUseCase: GetRoomMessagesUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val verifyChatRoomUseCase: VerifyChatRoomUseCase,
    private val tokenManager: TokenManager,
    private val chatRepository: ChatRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()


    private var currentRoomIdInternal: String? = null
    private var realtimeMessagesJob: Job? = null

    // Consider if you need a mutable list for adding messages optimistically
    // val messagesList = mutableStateListOf<Message>() // Alternative for optimistic updates

    init {
        val roomIdArg: String? = savedStateHandle.get<String>("roomId")
        val chatPartnerUserIdArg: String? =
            savedStateHandle.get<String>("chatUserId") // For new chats

        if (roomIdArg != null) {
//            _uiState.update { it.copy(currentRoomId = roomIdArg) }
//            loadMessages(roomIdArg)
//            loadInitialMessages(roomIdArg)
//            subscribeToRealtimeUpdates(roomIdArg)
            setupChatInteractions(roomIdArg)
            // You might want to fetch partner user details here too if not passed via nav
        } else if (chatPartnerUserIdArg != null) {
            // This is a new chat, verify/create the room first
            initializeNewChat(chatPartnerUserIdArg)
        } else {
            _uiState.update { it.copy(errorMessage = "Room ID or Chat User ID not provided.") }
        }
    }

    private fun setupChatInteractions(roomId: String) {
        currentRoomIdInternal = roomId
        _uiState.update { it.copy(currentRoomId = roomId) }
        loadInitialMessages(roomId)
        subscribeToRealtimeUpdates(roomId)
    }


    private fun loadInitialMessages(roomId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMessages = true) }
            try {
                val messages = getRoomMessagesUseCase(roomId) // Fetches via HTTP
                _uiState.update { currentState ->
                    currentState.copy(
                        messages = messages.sortedBy { it.timestamp },
                        isLoadingMessages = false,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Error loading initial messages", e)
                _uiState.update {
                    it.copy(
                        errorMessage = "Failed to load messages: ${e.message}",
                        isLoadingMessages = false
                    )
                }
            }
        }
    }

        private fun subscribeToRealtimeUpdates(roomId: String) {
            realtimeMessagesJob?.cancel() // Cancel any previous subscription job
            chatRepository.subscribeToRoom(roomId) // Tell repo to subscribe Pusher channel

            realtimeMessagesJob = viewModelScope.launch {
                val currentUserIdString = tokenManager.getUserId()?.toString() ?: ""
                chatRepository.getRealtimeMessages()
                    .filter { it.roomId.toString() == roomId } // Ensure message is for the current room
                    .map { messageDto ->
                        // Map DTO to Domain Message object
                        // This reuses the toDomain extension from ChatRepositoryImpl
                        // Make sure that toDomain exists and is accessible, or replicate mapping here.
                        // For simplicity, assuming a mapper or direct DTO to Domain conversion.
                        Message( // Example mapping, ensure it matches your MessageDto.toDomain
                            id = messageDto.id.toString(),
                            roomId = messageDto.roomId.toString(),
                            senderId = messageDto.userId.toString(),
                            content = messageDto.message,
                            timestamp = parseTimestamp(messageDto.createdAt), // Ensure parseTimestamp is accessible
                            isSentByCurrentUser = messageDto.userId.toString() == currentUserIdString,
                            senderName = messageDto.user.name,
                            senderProfileImage = messageDto.user.profileImage
                        )
                    }
                    .collect { newMessage ->
                        _uiState.update { currentState ->
                            val existingMessages = currentState.messages
                            if (existingMessages.any { it.id == newMessage.id }) {
                                // Message already exists (e.g., if sender also gets their own message via WS)
                                // Optionally update it if needed, or just keep current state
                                currentState
                            } else {
                                val updatedMessages = (existingMessages + newMessage).sortedBy { it.timestamp }
                                currentState.copy(messages = updatedMessages)
                            }
                        }
                    }
            }
        }
        // Helper needed in ViewModel if parseTimestamp is in Repository and not globally accessible
        private fun parseTimestamp(timestamp: String?): Long { /* ... your parsing logic ... */ return 0L}



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
                    Log.e("Chat Messages",fetchedMessages.toString())
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

     override fun onCleared() {
        super.onCleared()
        realtimeMessagesJob?.cancel()
        currentRoomIdInternal?.let {
            chatRepository.unsubscribeFromRoom(it)
        }
        // Consider if PusherService.disconnect() should be called,
        // e.g., if app is closing or no chat features are active.
        // If PusherService is a @Singleton, it might manage its own lifecycle.
    }



//    fun onMessageTextChanged(text: String) {
//        uiState = uiState.copy(newMessageText = text)
//    }
}