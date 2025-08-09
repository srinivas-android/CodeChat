package com.example.codechat.domain.usecase

import com.example.codechat.domain.model.Message
import com.example.codechat.domain.repository.ChatRepository
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(roomId: String, messageText: String): Message {
        // You might add validation for roomId and messageText here if needed
        if (messageText.isBlank()) {
            throw IllegalArgumentException("Message text cannot be blank.")
        }
        return chatRepository.sendMessage(roomId, messageText)
    }
}
