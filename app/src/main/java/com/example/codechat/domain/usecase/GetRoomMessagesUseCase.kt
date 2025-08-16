package com.example.codechat.domain.usecase

import com.example.codechat.domain.model.Message
import com.example.codechat.domain.repository.ChatRepository
import javax.inject.Inject

class GetRoomMessagesUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(roomId: String): List<Message> {
        return chatRepository.getRoomMessages(roomId)
    }
}
