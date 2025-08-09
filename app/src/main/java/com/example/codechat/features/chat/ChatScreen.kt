package com.example.codechat.features.chat
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.codechat.domain.model.Message
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
    // partnerName: String?, // Or get from viewModel.uiState
    // partnerProfileImageUrl: String? // Or get from viewModel.uiState
) {
    val uiState by viewModel.uiState.collectAsState()
    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Scroll to bottom when new messages arrive or keyboard opens (simplified)
    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            coroutineScope.launch {
                lazyListState.animateScrollToItem(uiState.messages.size - 1)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.navigationTitle) }, // Use title from UiState
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
                // Optionally add partner's profile image here if needed
            )
        },
        bottomBar = {
            MessageInputBar(message = uiState.currentMessageInput,
                onMessageChange = { viewModel.onMessageInputChange(it) },
                onSendMessage = { viewModel.sendMessage() },
                isSending = uiState.isSendingMessage
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)) {
            when {
                uiState.isLoadingMessages && uiState.messages.isEmpty() -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                uiState.errorMessage != null -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("Error: ${uiState.errorMessage}", color = MaterialTheme.colorScheme.error)
                        // Retry logic if applicable
                        uiState.currentRoomId?.let { roomId ->
                            Button(onClick = { viewModel.loadMessages(roomId) }) {
                                Text("Retry")
                            }
                        }
                    }
                }
                // No specific "empty" state for messages, just shows an empty LazyColumn
                else -> {
                    LazyColumn(
                        state = lazyListState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.messages, key = { it.id }) { message ->
                            MessageBubble(message = message)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MessageBubble(message: Message) {
    val horizontalArrangement = if (message.isSentByCurrentUser) Arrangement.End else Arrangement.Start
    val bubbleColor = if (message.isSentByCurrentUser) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer
    val textColor = if (message.isSentByCurrentUser) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSecondaryContainer

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = horizontalArrangement
    ) {
        Box(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (message.isSentByCurrentUser) 16.dp else 0.dp,
                        bottomEnd = if (message.isSentByCurrentUser) 0.dp else 16.dp
                    )
                )
                .background(bubbleColor)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Column {
                // Optionally show sender name if it's a group chat or if needed
                // if (!message.isSentByCurrentUser && message.senderName != null) {
                //    Text(message.senderName, style = MaterialTheme.typography.labelSmall, color = textColor.copy(alpha = 0.7f))
                // }
                Text(
                    text = message.content,
                    color = textColor,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageInputBar(
    message: String,
    onMessageChange: (String) -> Unit,
    onSendMessage: () -> Unit,
    isSending: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = message,
            onValueChange = onMessageChange,
            modifier = Modifier.weight(1f),
            placeholder = { Text("Type a message...") },
            shape = RoundedCornerShape(24.dp),
            colors = TextFieldDefaults.colors( // Use TextFieldDefaults for OutlinedTextField
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            maxLines = 4
        )
        Spacer(modifier = Modifier.width(8.dp))
        if (isSending) {
            CircularProgressIndicator(modifier = Modifier.size(40.dp))
        } else {
            IconButton(
                onClick = onSendMessage,
                enabled = message.isNotBlank(),
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = if (message.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(
                            alpha = 0.12f
                        ),
                        shape = CircleShape
                    )
            ) {
                Icon(
                    Icons.Filled.Send,
                    contentDescription = "Send message",
                    tint = if (message.isNotBlank()) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface.copy(alpha = androidx.wear.compose.material.ContentAlpha.disabled)
                )
            }
        }
    }
}