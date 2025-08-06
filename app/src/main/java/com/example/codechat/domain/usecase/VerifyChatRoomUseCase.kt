package com.example.codechat.domain.usecase

import com.example.codechat.data.model.VerifyChatRoomResponse // Or a domain model representation
import com.example.codechat.domain.repository.ChatRepository
import javax.inject.Inject

class VerifyChatRoomUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(userId: String): VerifyChatRoomResponse { // Or return a domain model
        return chatRepository.verifyChatRoom(userId)
    }
}
