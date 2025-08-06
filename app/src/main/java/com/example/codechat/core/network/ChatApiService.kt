package com.example.codechat.core.network

import com.example.codechat.data.model.SendMessageRequest
import com.example.codechat.data.model.VerifyChatRoomRequest
import com.example.codechat.data.model.VerifyChatRoomResponse
import com.example.codechat.domain.model.ChatRoom
import com.example.codechat.domain.model.Message
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ChatApiService {

    @GET("get-user-chat-rooms")
    suspend fun getUserChatRooms(): List<ChatRoom>

    // Assuming 'get-room-chats' takes roomId as a path parameter like /get-room-chats/{roomId}
    // If it's a query parameter, use @Query("roomId") roomId: String
    @GET("get-room-chats/{roomId}") 
    suspend fun getRoomMessages(@Path("roomId") roomId: String): List<Message>

    @POST("send-message")
    suspend fun sendMessage(@Body request: SendMessageRequest): Message // Assuming API returns the sent Message object

    @POST("verify-chat-room")
    suspend fun verifyChatRoom(@Body request: VerifyChatRoomRequest): VerifyChatRoomResponse

}
