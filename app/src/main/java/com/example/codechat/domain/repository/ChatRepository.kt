package com.example.codechat.domain.repository

import com.example.codechat.data.model.VerifyChatRoomResponse
import com.example.codechat.domain.model.ChatRoom
import com.example.codechat.domain.model.Message

interface ChatRepository {
    suspend fun getUserChatRooms(): List<ChatRoom>
    suspend fun getRoomMessages(roomId: String): List<Message>
    suspend fun sendMessage(roomId: String, messageContent: String): Message
    suspend fun verifyChatRoom(userId: String): VerifyChatRoomResponse // Or return ChatRoom directly if preferred
}
