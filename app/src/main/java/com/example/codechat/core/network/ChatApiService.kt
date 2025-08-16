package com.example.codechat.core.network

import com.example.codechat.data.model.GetRoomChatsResponse
import com.example.codechat.data.model.GetUserChatRoomsResponse
import com.example.codechat.data.model.SendMessageRequest
import com.example.codechat.data.model.SendMessageResponse
import com.example.codechat.data.model.VerifyChatRoomRequest
import com.example.codechat.data.model.VerifyChatRoomResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ChatApiService {

    @GET("get-user-chat-rooms")
    suspend fun getUserChatRooms(@Query("id") userId: String): Response<GetUserChatRoomsResponse>

    @GET("get-room-chats")
    suspend fun getRoomMessages(@Query("id") roomId: Int): Response<GetRoomChatsResponse>

    @POST("send-message")
    suspend fun sendMessage(@Body request: SendMessageRequest): Response<SendMessageResponse>

    @POST("verify-chat-room")
    suspend fun verifyChatRoom(@Body request: VerifyChatRoomRequest): Response<VerifyChatRoomResponse>

}
