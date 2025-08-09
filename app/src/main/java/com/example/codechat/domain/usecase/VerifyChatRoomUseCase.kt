package com.example.codechat.domain.usecase

import com.example.codechat.data.model.VerifyChatRoomResponse
import com.example.codechat.domain.repository.ChatRepository
import javax.inject.Inject

class VerifyChatRoomUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    /**
     * Verifies a chat room with the given [chatUserId] and returns the roomId.
     * If the room doesn't exist, the repository is expected to create it.
     */
    suspend operator fun invoke(chatUserId: String): VerifyChatRoomResponse {
        // You might add validation for chatUserId here if needed
        return chatRepository.verifyChatRoom(chatUserId)
    }
}
