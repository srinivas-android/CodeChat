package com.example.codechat.features.chatlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddComment
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.codechat.core.ui.components.ProfileImage
import com.example.codechat.domain.model.ChatRoom


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(
    viewModel: ChatListViewModel = hiltViewModel(),
    onNavigateToChat: (roomId: String, partnerUserId: String?, partnerName: String?) -> Unit,
    onNavigateToNewChatSelection: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Chats") },
//                colors = TopAppBarDefaults.topAppBarColors(
//                    containerColor = MaterialTheme.colorScheme.primaryContainer,
//                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
//                )
//            )
//        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToNewChatSelection) {
                Icon(Icons.Filled.AddComment, contentDescription = "Start new chat")
            }
        }
    ) { paddingValues ->
        ChatListContent(
            modifier = Modifier.padding(paddingValues),
            uiState = uiState,
            onChatRoomClick = { chatRoom ->
                onNavigateToChat(
                    chatRoom.id,
                    chatRoom.partnerUser?.id.toString(), // Pass partnerUserId for context
                    chatRoom.partnerUser?.name // Pass partnerName for context
                )
            },
            onRefresh = { viewModel.onRefresh() }
        )
    }
}

@Composable
fun ChatListContent(
    modifier: Modifier = Modifier,
    uiState: ChatListUiState,
    onChatRoomClick: (ChatRoom) -> Unit,
    onRefresh: () -> Unit // For swipe-to-refresh or a button
) {
    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        when {
            uiState.isLoading && uiState.chatRooms.isEmpty() -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            uiState.errorMessage != null -> {
                Column(
                    modifier = Modifier.align(Alignment.Center).padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Error: ${uiState.errorMessage}", color = MaterialTheme.colorScheme.error)
                    // You might add a retry button here that calls onRefresh
                }
            }
            uiState.chatRooms.isEmpty() -> {
                Text("No chats available. Start a new conversation!", modifier = Modifier.align(Alignment.Center))
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 0.dp) // No padding if items have their own
                ) {
                    items(uiState.chatRooms, key = { it.id }) { chatRoom ->
                        ChatRoomItem(
                            chatRoom = chatRoom,
                            onClick = { onChatRoomClick(chatRoom) }
                        )
                        Divider(thickness = 0.5.dp)
                    }
                }
            }
        }
        // Add SwipeRefresh if desired
    }
}

@Composable
fun ChatRoomItem(
    chatRoom: ChatRoom,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = { Text(chatRoom.name.toString(), style = MaterialTheme.typography.titleMedium) },
        supportingContent = {
            chatRoom.lastMessage?.let {
                Text(
                    it,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1
                )
            }
        },
        leadingContent = {
            ProfileImage(
                imageUrl = chatRoom.profileImageUrl,
                contentDescription = "${chatRoom.name} profile picture",
                size = 40.dp
            )
        },
        // Optionally, show unread count or last message timestamp as trailing content
        // trailingContent = { Text(chatRoom.lastMessageTimestamp.toString()) },
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}