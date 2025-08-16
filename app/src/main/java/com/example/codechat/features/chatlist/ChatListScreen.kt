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
        topBar = {
            TopAppBar(
                title = { Text("Chats") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
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
                    chatRoom.partnerUser?.id.toString(),
                    chatRoom.partnerUser?.name
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
    onRefresh: () -> Unit
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

                }
            }
            uiState.chatRooms.isEmpty() -> {
                Text("No chats available. Start a new conversation!", modifier = Modifier.align(Alignment.Center))
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 0.dp)
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
            val imageUrl = chatRoom.profileImageUrl
            ProfileImage(
                imageUrl = if(imageUrl.isNullOrBlank()) null else imageUrl,
                contentDescription = "${chatRoom.name} profile picture",
                size = 40.dp
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}