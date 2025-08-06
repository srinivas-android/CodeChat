package com.example.codechat.data.repository

import com.example.codechat.core.network.ChatApiService
import com.example.codechat.data.model.SendMessageRequest
import com.example.codechat.data.model.VerifyChatRoomRequest
import com.example.codechat.data.model.VerifyChatRoomResponse
import com.example.codechat.domain.model.ChatRoom
import com.example.codechat.domain.model.Message
import com.example.codechat.domain.repository.ChatRepository
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val chatApiService: ChatApiService
) : ChatRepository {

    override suspend fun getUserChatRooms(): List<ChatRoom> {
        return chatApiService.getUserChatRooms()
    }

    override suspend fun getRoomMessages(roomId: String): List<Message> {
        // Here, you might want to map senderId to isSentByCurrentUser
        // For now, let's assume current user ID is available (e.g., from TokenManager or a user session)
        // This mapping logic ideally sits here or in a use case if more complex.
        // val currentUserId = "get_current_user_id_somehow" // Placeholder
        val messages = chatApiService.getRoomMessages(roomId)
        /*
        return messages.map { message ->
            message.copy(isSentByCurrentUser = message.senderId == currentUserId)
        }
        */
        // For now, returning directly. isSentByCurrentUser can be handled in ViewModel or UseCase
        // if currentUserId isn't easily accessible here.
        return messages
    }

    override suspend fun sendMessage(roomId: String, messageContent: String): Message {
        val request = SendMessageRequest(roomId = roomId, message = messageContent)
        return chatApiService.sendMessage(request)
    }

    override suspend fun verifyChatRoom(userId: String): VerifyChatRoomResponse {
        val request = VerifyChatRoomRequest(userId = userId)
        return chatApiService.verifyChatRoom(request)
    }
}
