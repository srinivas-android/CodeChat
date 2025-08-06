package com.example.codechat.domain.usecase

import com.example.codechat.domain.model.ChatRoom
import com.example.codechat.domain.repository.ChatRepository
import javax.inject.Inject

class GetUserChatRoomsUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(): List<ChatRoom> {
        return chatRepository.getUserChatRooms()
    }
}
