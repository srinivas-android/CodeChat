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
import java.text.SimpleDateFormat
import javax.inject.Inject
import java.util.Locale
import java.util.TimeZone

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
        } else if (chatPartnerUserIdArg != null) {
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
                val messages = getRoomMessagesUseCase(roomId)
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
        realtimeMessagesJob?.cancel()
        Log.d("ChatViewModel", "SUBSCRIBING_TO_ROOM_UPDATES: For RoomId=$roomId. Cancelling previous job if any.")
        chatRepository.subscribeToRoom(roomId)

        realtimeMessagesJob = viewModelScope.launch {
            Log.d("ChatViewModel", "LAUNCHED_REALTIME_JOB: For RoomId=$roomId")
            val currentUserIdString = tokenManager.getUserId()?.toString() ?: ""
            Log.d("ChatViewModel", "USER_ID_FOR_WS_CHECK: CurrentUserIdString='$currentUserIdString' for RoomId=$roomId")

            chatRepository.getRealtimeMessages()
                .collect { messageDto -> // This is the MessageDto from PusherService
                    Log.d("ChatViewModel", "COLLECTED_DTO_FROM_REPO: DTO=$messageDto, CurrentScreenRoomId=$roomId")

                    // FILTERING LOGIC
                    if (messageDto.roomId.toString() != roomId) {
                        Log.w("ChatViewModel", "FILTERED_OUT_DTO: DTO_RoomId=${messageDto.roomId}, Expected_Screen_RoomId=$roomId. Message: ${messageDto.message}")
                        return@collect // Skip this message
                    }
                    Log.d("ChatViewModel", "PASSED_FILTER_DTO: $messageDto for RoomId=$roomId")


                    val domainMessage: Message? = try {
                        Message(
                            id = messageDto.id.toString(),
                            roomId = messageDto.roomId.toString(),
                            senderId = messageDto.userId.toString(),
                            content = messageDto.message,
                            timestamp = parseTimestamp(messageDto.createdAt),
                            isSentByCurrentUser = messageDto.userId.toString() == currentUserIdString,
                            senderName = messageDto.user.name,
                            senderProfileImage = messageDto.user.profileImage
                        )
                    } catch (e: Exception) {
                        Log.e("ChatViewModel", "MAPPING_ERROR_DTO_TO_DOMAIN: DTO=$messageDto", e)
                        null
                    }

                    if (domainMessage == null) {
                        Log.e("ChatViewModel", "DOMAIN_MESSAGE_IS_NULL_AFTER_MAPPING: Skipping UI update for DTO=$messageDto")
                        return@collect
                    }
                    Log.d("ChatViewModel", "MAPPED_TO_DOMAIN_MESSAGE: $domainMessage for RoomId=$roomId")


                    _uiState.update { currentState ->
                        if (currentState.messages.any { it.id == domainMessage.id }) {
                            Log.d("ChatViewModel", "DUPLICATE_MESSAGE_SKIPPED: ID=${domainMessage.id}, Content='${domainMessage.content}' in RoomId=$roomId")
                            currentState
                        } else {
                            val updatedMessages = (currentState.messages + domainMessage).sortedBy { it.timestamp }
                            Log.d("ChatViewModel", "UPDATING_UI_STATE_WITH_NEW_MESSAGE: NewMsgID=${domainMessage.id}, Content='${domainMessage.content}'. Total messages now: ${updatedMessages.size} for RoomId=$roomId")
                            currentState.copy(messages = updatedMessages)
                        }
                    }
                }
            Log.d("ChatViewModel", "REALTIME_JOB_COLLECTION_ENDED_OR_CANCELLED: For RoomId=$roomId") // Should not see this unless viewmodel is cleared or job cancelled
        }
    }


        private fun parseTimestamp(timestamp: String?): Long {
            val format = SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss",
                Locale.getDefault()
            )

            format.timeZone = TimeZone.getTimeZone("UTC")
            return try {
                val date = format.parse(timestamp)
                date?.time ?: 0L
            } catch (e: Exception) {
                Log.e("TimestampParser", "Failed to parse timestamp: $timestamp", e)
                0L
            }
        }



        private fun initializeNewChat(chatPartnerUserId: String) {
            viewModelScope.launch {
                _uiState.update { it.copy(isLoadingMessages = true, errorMessage = null) }
                try {
                    val roomId = verifyChatRoomUseCase(chatPartnerUserId)
                    _uiState.update { it.copy(currentRoomId = roomId.roomId.toString()) }
                    loadMessages(roomId.roomId.toString())
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
                            messages = it.messages + sentMessage,
                            currentMessageInput = ""
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
    }



//    fun onMessageTextChanged(text: String) {
//        uiState = uiState.copy(newMessageText = text)
//    }
}