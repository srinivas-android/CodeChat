package com.example.codechat.domain.usecase

import com.example.codechat.domain.model.Message
import com.example.codechat.domain.repository.ChatRepository
import com.example.codechat.core.utils.TokenManager
import javax.inject.Inject

class GetRoomMessagesUseCase @Inject constructor(
    private val chatRepository: ChatRepository,
    private val tokenManager: TokenManager // To get current user ID for isSentByCurrentUser
) {
    suspend operator fun invoke(roomId: String): List<Message> {
        val messages = chatRepository.getRoomMessages(roomId)
        val currentUserId = tokenManager.getUserId() // Assuming TokenManager has getUserId()

        // Gracefully handle if currentUserId is null (e.g., user logged out, data inconsistent)
        return if (currentUserId != null) {
            messages.map {
                // Ensure ID comparison is type-safe if one is Int and other is String from API
                // For now, assuming User.id and Message.senderId are comparable as Strings.
                // If User.id is Int, ensure Message.senderId is also treated as Int or cast appropriately.
                it.copy(isSentByCurrentUser = it.senderId == currentUserId)
            }
        } else {
            // If currentUserId is null, cannot determine sentByCurrentUser accurately
            // Optionally, log a warning or return messages without this flag set
            messages
        }
    }
}
