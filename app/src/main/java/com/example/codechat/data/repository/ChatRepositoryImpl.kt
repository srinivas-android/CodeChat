package com.example.codechat.data.repository


import com.example.codechat.core.network.ChatApiService
import com.example.codechat.core.utils.TokenManager
import com.example.codechat.data.model.ChatMessageDto
import com.example.codechat.data.model.ChatRoomDto
import com.example.codechat.data.model.MessageDto
import com.example.codechat.data.model.SendMessageRequest
import com.example.codechat.data.model.UserDto
import com.example.codechat.data.model.UserRef
import com.example.codechat.data.model.VerifyChatRoomRequest
import com.example.codechat.data.model.VerifyChatRoomResponse
import com.example.codechat.data.remote.PusherService
import com.example.codechat.domain.model.ChatRoom
import com.example.codechat.domain.model.Message
import com.example.codechat.domain.model.User
import com.example.codechat.domain.repository.ChatRepository
import kotlinx.coroutines.flow.SharedFlow
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val chatApiService: ChatApiService,
    private val tokenManager: TokenManager,
    private val pusherService: PusherService
) : ChatRepository {

    init {
        pusherService.connect()
    }

    private val timestampParser = SimpleDateFormat(
        "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'",
        Locale.getDefault()
    ).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
    private val altTimestampParser = SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
        Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    private fun parseTimestamp(timestamp: String?): Long {
        if (timestamp == null) return 0L
        return try {
            timestampParser.parse(timestamp)?.time ?: 0L
        } catch (e: Exception) {
            try {
                altTimestampParser.parse(timestamp)?.time ?: 0L
            } catch (e2: Exception) {
                0L
            }
        }
    }

    private fun UserDto.toDomain(currentUserId: Int? = null): User {
        return User(
            id = this.id,
            name = this.name,
            email = this.email,
            profileImage = this.profileImage,
            token = null
            // 'isCurrentUser' can be determined in UseCase/ViewModel if needed,
            // or if currentUserId is passed here.
        )
    }

    private fun ChatMessageDto.toDomain(currentUserId: String): Message {
        return Message(
            id = this.id.toString(),
            roomId = this.roomId.toString(),
            senderId = this.userId.toString(),
            content = this.message,
            timestamp = parseTimestamp(this.createdAt),
            isSentByCurrentUser = this.userId.toString() == currentUserId,
            senderName = this.user?.name,
            senderProfileImage = this.user?.profileImage // Assuming you add senderProfileImage
        )
    }

    private fun ChatRoomDto.toDomain(currentUserId: String): ChatRoom {
        val partner = this.partnerUser?.toDomain()
            ?: User(
                id = this.partnerUserId,
                name = "Unknown User",
                email = null,
                profileImage = null,
                token = null
            )

        val lastMessageFromDto = this.chats?.maxByOrNull { parseTimestamp(it.createdAt) }

        return ChatRoom(
            id = this.id.toString(),
            name = partner.name ?: "Chat with ${partner.id}",
            lastMessage = lastMessageFromDto?.message,
            lastMessageTimestamp = lastMessageFromDto?.let { parseTimestamp(it.createdAt) },
            partnerUser = partner,
            unreadCount = 0,
            participants =  listOfNotNull(partner),
            profileImageUrl = partner.profileImage, // Placeholder, as not directly available in this DTO
        )
    }

    private fun MessageDto.toDomain(currentLoggedInUserId: String): Message {
        return Message(
            id = this.id.toString(),
            roomId = this.roomId.toString(),
            senderId = this.userId.toString(),
            content = this.message,
            timestamp = parseTimestamp(this.createdAt), // Assuming MessageDto.createdAt is the correct field for timestamp
            isSentByCurrentUser = this.userId.toString() == currentLoggedInUserId,
            senderName = this.user.name, // Assuming MessageDto.user (UserDto) is non-nullable and has name
            senderProfileImage = this.user.profileImage // Assuming MessageDto.user (UserDto) is non-nullable and has profileImage
        )
    }


    override suspend fun getUserChatRooms(): List<ChatRoom> {
        val currentUserId = tokenManager.getUserId() ?: throw IllegalStateException("User not logged in")
        val response = chatApiService.getUserChatRooms(userId = currentUserId)

        if (response.isSuccessful) {
            val chatRoomsResponse = response.body()
            return chatRoomsResponse?.rooms?.map { it.toDomain(currentUserId) } ?: emptyList()
        } else {
            // Handle error (e.g., throw an exception, return an error state)
            throw Exception("Failed to get user chat rooms: ${response.code()} ${response.message()}")
        }
    }

    override suspend fun getRoomMessages(roomId: String): List<Message> {
        val currentUserId = tokenManager.getUserId() ?: throw IllegalStateException("User ID not found in TokenManager")
        val roomIdInt = roomId.toIntOrNull() ?: throw IllegalArgumentException("Invalid room ID format: $roomId")
        val response = chatApiService.getRoomMessages(roomId = roomIdInt)

        if (response.isSuccessful) {
            val roomChatsResponse = response.body()
            // The API response "get-room chats" seems to return a list of rooms,
            // we are interested in the chats of the *first* room in that list for the given roomId.
            val messagesDtoList = roomChatsResponse?.chats
            return messagesDtoList
                ?.map { it.toDomain(currentUserId) }
                ?.sortedBy { it.timestamp } // Sort by timestamp
                ?: emptyList()
        } else {
            throw Exception("Failed to get room messages for room $roomId: ${response.code()} ${response.message()}")
        }
    }

    override suspend fun verifyChatRoom(chatUserId: String): VerifyChatRoomResponse {
        val currentUserId = tokenManager.getUserId() ?: throw IllegalStateException("User ID not found in TokenManager")
        val partnerUserIdInt = chatUserId.toIntOrNull() ?: throw IllegalArgumentException("Invalid partner user ID format: $chatUserId")

        val request = VerifyChatRoomRequest(
            userId = currentUserId,
            chatUserId = partnerUserIdInt
        )
        val response = chatApiService.verifyChatRoom(request)

        if (response.isSuccessful) {
            val verifyResponse = response.body()
            return verifyResponse ?: throw Exception("Room ID not found in verifyChatRoom response")
        } else {
            throw Exception("Failed to verify/create chat room: ${response.code()} ${response.message()}")
        }
    }

    override suspend fun sendMessage(roomId: String, messageText: String): Message {
        val currentUserId = tokenManager.getUserId() ?: throw IllegalStateException("User ID not found in TokenManager")
        val roomIdInt = roomId.toIntOrNull() ?: throw IllegalArgumentException("Invalid room ID format: $roomId")

        val request = SendMessageRequest(
            message = messageText,
            roomId = roomIdInt,
            user = UserRef(id = currentUserId)
        )
        val response = chatApiService.sendMessage(request)

        if (response.isSuccessful) {
            val sendMessageResponse = response.body()
            val messageDto = sendMessageResponse?.message ?: throw Exception("Sent message data not found in response")
            return messageDto.toDomain(currentUserId)
        } else {
            throw Exception("Failed to send message: ${response.code()} ${response.message()}")
        }
    }

    override suspend fun getRealtimeMessages(): SharedFlow<MessageDto> {
        return pusherService.incomingMessageEvents
    }

     override fun subscribeToRoom(roomId: String) {
        // The event name must match what your Laravel backend broadcasts for new messages
        // e.g., if you have `broadcast(new ChatMessageSent($message))->toOthers();`
        // and ChatMessageSent event is not explicitly named, Laravel uses the class name.
        // If App\Events\ChatMessageSent, then it might be ".App.Events.ChatMessageSent" or just "ChatMessageSent"
        // Check your Echo configuration or broadcasting setup.
        val messageEventName = "ChatMessageSent" // TODO: VERIFY THIS EVENT NAME
        pusherService.subscribeToRoomChannel(roomId, messageEventName)
    }

    override fun unsubscribeFromRoom(roomId: String) {
        pusherService.unsubscribeFromRoomChannel(roomId)
    }

//    override suspend fun verifyChatRoom(userId: String): VerifyChatRoomResponse {
//        throw NotImplementedError("This version of verifyChatRoom is outdated. Use verifyChatRoom(chatUserId: String): String instead.")
//    }
}

