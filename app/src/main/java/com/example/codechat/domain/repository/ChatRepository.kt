package com.example.codechat.domain.repository

import com.example.codechat.data.model.MessageDto
import com.example.codechat.data.model.VerifyChatRoomResponse
import com.example.codechat.domain.model.ChatRoom
import com.example.codechat.domain.model.Message
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface ChatRepository {
    suspend fun getUserChatRooms(): List<ChatRoom>
    suspend fun getRoomMessages(roomId: String): List<Message>
    suspend fun sendMessage(roomId: String, messageContent: String): Message
    suspend fun verifyChatRoom(userId: String): VerifyChatRoomResponse

    suspend fun getRealtimeMessages(): SharedFlow<MessageDto>

    fun subscribeToRoom(roomId: String)

    fun unsubscribeFromRoom(roomId: String)
}
