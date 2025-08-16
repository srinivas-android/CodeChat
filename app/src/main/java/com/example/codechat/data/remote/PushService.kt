// In a new file, e.g., data/remote/PusherService.kt
package com.example.codechat.data.remote

import android.util.Log
import com.google.gson.Gson
import com.pusher.client.Pusher
import com.pusher.client.PusherOptions
import com.pusher.client.channel.Channel
import com.pusher.client.channel.PusherEvent
import com.pusher.client.channel.SubscriptionEventListener
import com.pusher.client.connection.ConnectionEventListener
import com.pusher.client.connection.ConnectionState
import com.pusher.client.connection.ConnectionStateChange
import com.pusher.client.util.HttpAuthorizer // For private channels
import com.example.codechat.core.utils.TokenManager // Assuming you have this
import com.example.codechat.data.model.MessageDto // Your DTO for messages from WebSocket
import com.example.codechat.data.model.WebSocketMessageWrapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PusherService @Inject constructor(
    private val tokenManager: TokenManager,
    private val gson: Gson
) {
    private var pusher: Pusher? = null
    private val activeChannels = mutableMapOf<String, Channel>()

    private val _incomingMessageEvents = MutableSharedFlow<MessageDto>()
    val incomingMessageEvents: SharedFlow<MessageDto> = _incomingMessageEvents.asSharedFlow()

    private val serviceScope = CoroutineScope(Dispatchers.IO)

    companion object {
        private const val PUSHER_APP_KEY = "chatkey"
        private const val PUSHER_APP_CLUSTER = "mt1"
        private const val WS_HOST = "192.168.1.5" // For Android emulator to connect to localhost of host machine
        // private const val WS_HOST = "your_actual_domain.com" // For production
        private const val WS_PORT = 6001       // Default Laravel WebSockets port (NOT your HTTP port 8000)
        private const val USE_TLS = false      // For local development with ws://
        // private const val USE_TLS = true    // For production with wss://

        private const val AUTH_ENDPOINT_URL = "http://$WS_HOST:8000/broadcasting/auth"
    }

    fun connect() {
        if (pusher?.connection?.state == ConnectionState.CONNECTED) {
            Log.d("PusherService", "Already connected.")
            return
        }

        val options = PusherOptions().apply {
            setCluster(PUSHER_APP_CLUSTER)
            setHost(WS_HOST)
            setWsPort(WS_PORT)
            setWssPort(WS_PORT)
            setUseTLS(USE_TLS)
            setEncrypted(false)


            val authorizer = HttpAuthorizer(AUTH_ENDPOINT_URL)
            val headers = HashMap<String, String>()

            headers["Accept"] = "application/json"
            authorizer.setHeaders(headers)
            setAuthorizer(authorizer)
        }

        try {
            pusher = Pusher(PUSHER_APP_KEY, options)
            pusher?.connect(object : ConnectionEventListener {
                override fun onConnectionStateChange(change: ConnectionStateChange) {
                    Log.i("PusherService", "State changed from ${change.previousState} to ${change.currentState}")
                }

                override fun onError(message: String, code: String?, e: Exception?) {
                    Log.e("PusherService", "Connection error: $message Code: $code", e)
                }
            }, ConnectionState.ALL)
        } catch (e: Exception) {
            Log.e("PusherService", "Pusher initialization error", e)
        }
    }

    fun subscribeToRoomChannel(roomId: String, eventName: String) {
        val channelName = "chat_$roomId"
        if (pusher?.connection?.state != ConnectionState.CONNECTED) {
            Log.w("PusherService", "Pusher not connected. Cannot subscribe to $channelName.")
            connect()
            return
        }

        if (activeChannels.containsKey(channelName)) {
            Log.d("PusherService", "Already subscribed to $channelName.")
            return
        }

        try {
            val channel = pusher?.subscribe(channelName)
            channel?.bind(eventName, SubscriptionEventListener { event: PusherEvent ->
                Log.i("PusherService", "Received event: ${event.eventName} on channel ${event.channelName} with data: ${event.data}")
                try {

//                    val messageDto = gson.fromJson(event.data, MessageDto::class.java)
                    val wrapper = gson.fromJson(event.data, WebSocketMessageWrapper::class.java)
                    val messageDto = wrapper.chat

                    serviceScope.launch {
                        _incomingMessageEvents.emit(messageDto)
                    }
                } catch (e: Exception) {
                    Log.e("PusherService", "Error parsing message from event data: ${event.data}", e)
                }
            })
            activeChannels[channelName] = channel!!
            Log.i("PusherService", "Subscribed to $channelName and bound to $eventName")
        } catch (e: Exception) {
            Log.e("PusherService", "Error subscribing to $channelName or binding event $eventName", e)
        }
    }

    fun unsubscribeFromRoomChannel(roomId: String) {
        val channelName = "chat_$roomId"
        if (activeChannels.containsKey(channelName)) {
            pusher?.unsubscribe(channelName)
            activeChannels.remove(channelName)
            Log.i("PusherService", "Unsubscribed from $channelName")
        }
    }

    fun disconnect() {
        activeChannels.keys.forEach { pusher?.unsubscribe(it) }
        activeChannels.clear()
        pusher?.disconnect()
        Log.i("PusherService", "Pusher client disconnected.")
    }
}
