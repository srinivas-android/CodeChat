package com.example.codechat.domain.usecase

import com.example.codechat.domain.model.Message
import com.example.codechat.domain.repository.ChatRepository
import javax.inject.Inject

class GetRoomMessagesUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(roomId: String): List<Message> {
        // You might add validation for roomId here if needed
        return chatRepository.getRoomMessages(roomId)
    }
}
