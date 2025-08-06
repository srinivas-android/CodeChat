package com.example.codechat.features.chatlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.codechat.domain.model.ChatRoom

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(
    viewModel: ChatListViewModel = hiltViewModel(),
    onChatRoomClick: (roomId: String) -> Unit,
    // onNewChatClick: () -> Unit // For starting a new chat, e.g., from a FAB
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Chats") })
        }
        // floatingActionButton = {
        //     FloatingActionButton(onClick = onNewChatClick) {
        //         Icon(Icons.Filled.Add, "Start new chat")
        //     }
        // }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator()
                }
                uiState.errorMessage != null -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Error: ${uiState.errorMessage}", color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.fetchChatRooms() }) {
                            Text("Retry")
                        }
                    }
                }
                uiState.chatRooms.isEmpty() -> {
                    Text("No chats yet. Start a new conversation!")
                }
                else -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(uiState.chatRooms) { room ->
                            ChatRoomItem(room = room, onClick = { onChatRoomClick(room.id) })
                            Divider()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatRoomItem(room: ChatRoom, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Placeholder for profile image
        // AsyncImage(model = room.roomImageUrl ?: defaultRoomImage, contentDescription = "Room image", modifier = Modifier.size(40.dp).clip(CircleShape))
        // Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(room.name ?: "Unnamed Chat", style = MaterialTheme.typography.titleMedium)
            Text(room.lastMessage ?: "No messages yet", style = MaterialTheme.typography.bodySmall, maxLines = 1)
        }
        // room.lastMessageTimestamp?.let {        
        //     Text(
        //         text = formatTimestamp(it), // You'll need a utility to format the timestamp
        //         style = MaterialTheme.typography.bodySmall
        //     )
        // }
    }
}

// Dummy formatTimestamp - replace with actual implementation
// fun formatTimestamp(timestamp: Long): String {
//     return java.text.SimpleDateFormat("hh:mm a", Locale.getDefault()).format(java.util.Date(timestamp))
// }
