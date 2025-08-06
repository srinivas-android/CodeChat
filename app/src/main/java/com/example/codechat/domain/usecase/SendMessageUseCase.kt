package com.example.codechat.domain.usecase

import com.example.codechat.domain.model.Message
import com.example.codechat.domain.repository.ChatRepository
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(roomId: String, messageContent: String): Message {
        return chatRepository.sendMessage(roomId, messageContent)
    }
}
