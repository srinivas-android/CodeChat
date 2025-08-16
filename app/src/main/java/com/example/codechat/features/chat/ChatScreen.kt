package com.example.codechat.features.chat

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Send
// import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.material.ContentAlpha
import coil.compose.AsyncImage
import com.example.codechat.R // Assuming you have an R file
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Data class to represent a message in the UI, adapted from Codechat
data class DisplayMessage(
    val id: String,
    val author: String,
    val content: String,
    val timestamp: String,
    val authorImageUrl: String?,
    val isUserMe: Boolean,
    val authorId: String? = null
    // val image: Int? = null, (Codechat original)
    // val imageData: Any? = null (Future)
)

// Simplified UiState for ConversationContent
data class CodechatConversationUiState(
    val messages: List<DisplayMessage>,
    val channelName: String,
    val channelMembers: Int,
    val currentMessageInput: String,
    val isSendingMessage: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    navigateToProfile: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

   Log.d("ChatScreen", "UI_STATE_COLLECTED: $uiState")

        val codechatUiState = remember(uiState) {
        CodechatConversationUiState(
            messages = uiState.messages.map { domainMessage ->
                val authorMe = try {
                    uiState.partnerUser?.name
                } catch (e: Exception) {
                    "Me"
                }
                DisplayMessage(
                    id = domainMessage.id,
                    author = if (domainMessage.isSentByCurrentUser) authorMe.toString() else domainMessage.senderName
                        ?: "Unknown User",
                    content = domainMessage.content,
                    timestamp = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(
                        Date(
                            domainMessage.timestamp
                        )
                    ),
                    authorImageUrl = domainMessage.senderProfileImage,
                    isUserMe = domainMessage.isSentByCurrentUser,
                    authorId = domainMessage.senderId
                )
            }.reversed(),
            channelName = uiState.navigationTitle,
            channelMembers = 0, // Placeholder - Codechat UI shows this.
            currentMessageInput = uiState.currentMessageInput,
            isSendingMessage = uiState.isSendingMessage
        )
    }

    val displayMessagesForLazyColumn = remember(uiState.messages) {
        uiState.messages.map { domainMessage ->
            val authorMe = uiState.partnerUser?.name ?: "Me"
            DisplayMessage(
                id = domainMessage.id,
                author = if (domainMessage.isSentByCurrentUser) authorMe else domainMessage.senderName ?: "Unknown User",
                content = domainMessage.content,
                timestamp = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(domainMessage.timestamp)),
                authorImageUrl = domainMessage.senderProfileImage,
                isUserMe = domainMessage.isSentByCurrentUser,
                authorId = domainMessage.senderId
            )
        }.reversed()
    }


    val scrollState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(displayMessagesForLazyColumn.size) {
        if (codechatUiState.messages.isNotEmpty()) {
            scope.launch {
                scrollState.animateScrollToItem(0)
            }
        }
    }

    Scaffold(
        topBar = {
            CodechatChannelNameBar(
                channelName = codechatUiState.channelName,
                channelMembers = codechatUiState.channelMembers,
                onNavIconPressed = onNavigateBack,
                onSearchClicked = { /* TODO: Implement search or show not available */ },
                onInfoClicked = { /* TODO: Implement info or show not available */ },
            )
        },
        contentWindowInsets = ScaffoldDefaults
            .contentWindowInsets
            .exclude(WindowInsets.navigationBars)
            .exclude(WindowInsets.ime),
    ) { paddingValues ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                 uiState.isLoadingMessages && codechatUiState.messages.isEmpty() -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally).padding(16.dp))
                }
                uiState.errorMessage != null -> {
                     Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .align(Alignment.CenterHorizontally),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("Error: ${uiState.errorMessage}", color = MaterialTheme.colorScheme.error)
                        uiState.currentRoomId?.let { roomId ->
                            Button(onClick = { viewModel.loadMessages(roomId) }, modifier = Modifier.padding(top = 8.dp)) {
                                Text("Retry")
                            }
                        }
                    }
                }

                else -> {
                    CodechatMessages(
                        messages = codechatUiState.messages,
                        navigateToProfile = navigateToProfile,
                        scrollState = scrollState,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            CodechatUserInput(
                modifier = Modifier
                    .navigationBarsPadding()
                    .imePadding(),
                message = codechatUiState.currentMessageInput,
                onMessageChange = { viewModel.onMessageInputChange(it) },
                onSendMessage = { viewModel.sendMessage() },
                isSending = codechatUiState.isSendingMessage,
                resetScroll = {
                    scope.launch {
                        scrollState.scrollToItem(0)
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CodechatChannelNameBar(
    channelName: String,
    channelMembers: Int, // Can be used or ignored
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    onNavIconPressed: () -> Unit = { },
    onSearchClicked: () -> Unit,
    onInfoClicked: () -> Unit
) {
    TopAppBar(
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            IconButton(onClick = onNavIconPressed) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "stringResource(R.string.abc_action_bar_up_description)" // Example string
                )
            }
        },
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = channelName,
                    style = MaterialTheme.typography.titleMedium
                )
                if (channelMembers > 0) {
                    Text(
                        text = "2",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        actions = {
            // IconButton(onClick = onSearchClicked) {
            //     Icon(
            //         imageVector = Icons.Outlined.Search,
            //         contentDescription = stringResource(R.string.search_description) // Example string
            //     )
            // }
            // IconButton(onClick = onInfoClicked) {
            //     Icon(
            //         imageVector = Icons.Outlined.Info,
            //         contentDescription = stringResource(R.string.info_description) // Example string
            //     )
            // }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}

@Composable
fun CodechatMessages(
    messages: List<DisplayMessage>,
    navigateToProfile: (String) -> Unit,
    scrollState: LazyListState,
    modifier: Modifier = Modifier
) {

    if (messages.isNotEmpty()) {
        Log.d("CodechatMessages", "First message in list passed to LazyColumn: ID=${messages[0].id}, Content='${messages[0].content}'")
    } else {
        Log.d("CodechatMessages", "Messages list passed to LazyColumn is empty.")
    }
    Box(modifier = modifier) {
        LazyColumn(
            reverseLayout = true, // Newest messages at the bottom, and scroll from bottom
            state = scrollState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp) // Spacing between messages
        ) {
            items(messages, key = { it.id }) { msg ->
                CodechatMessageItem(
                    onAuthorClick = { authorId -> if (authorId != null) navigateToProfile(authorId) },
                    msg = msg,
                    isUserMe = msg.isUserMe,

                    isFirstMessageByAuthor = true,
                    isLastMessageByAuthor = true
                )
            }
        }
        // JumpToBottom button can be added here later
    }
}

@Composable
fun CodechatMessageItem(
    onAuthorClick: (String?) -> Unit,
    msg: DisplayMessage,
    isUserMe: Boolean,
    isFirstMessageByAuthor: Boolean,
    isLastMessageByAuthor: Boolean
) {
    val borderColor = if (isUserMe) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.tertiary
    }

    val rowModifier = if (isFirstMessageByAuthor) Modifier.padding(top = 8.dp) else Modifier

    Row(modifier = rowModifier.fillMaxWidth()) {
        if (!isUserMe && isLastMessageByAuthor) {
            AsyncImage(
                model = msg.authorImageUrl,
                contentDescription = "${msg.author} profile picture",
                modifier = Modifier
                    .clickable(onClick = { onAuthorClick(msg.authorId) })
                    .padding(start = 4.dp, end = 8.dp)
                    .size(42.dp)
                    .border(1.5.dp, borderColor, CircleShape)
                    .border(3.dp, MaterialTheme.colorScheme.surface, CircleShape)
                    .clip(CircleShape)
                    .align(Alignment.Top)
            )
        } else if (isUserMe) {
             Spacer(Modifier.weight(0.15f))
        } else {
            Spacer(modifier = Modifier.width(42.dp + 12.dp))
        }

        CodechatAuthorAndTextMessage(
            msg = msg,
            isUserMe = isUserMe,
            isFirstMessageByAuthor = isFirstMessageByAuthor,
            isLastMessageByAuthor = isLastMessageByAuthor,
            authorClicked = { onAuthorClick(msg.authorId) },
            modifier = Modifier
                .padding(end = if (isUserMe) 4.dp else 16.dp)
                .weight(if (isUserMe) 0.85f else 1f)
        )
         if (!isUserMe) {
             Spacer(Modifier.weight(0.15f))
        }
    }
}

@Composable
fun CodechatAuthorAndTextMessage(
    msg: DisplayMessage,
    isUserMe: Boolean,
    isFirstMessageByAuthor: Boolean,
    isLastMessageByAuthor: Boolean,
    authorClicked: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val messageAlign = if (isUserMe) Alignment.End else Alignment.Start
    Column(modifier = modifier, horizontalAlignment = messageAlign) {
        if (!isUserMe && isLastMessageByAuthor) {
            CodechatAuthorNameTimestamp(msg)
        }
        CodechatChatItemBubble(msg, isUserMe, authorClicked = authorClicked)
        Spacer(modifier = Modifier.height(if (isFirstMessageByAuthor) 8.dp else 4.dp))
    }
}

@Composable
private fun CodechatAuthorNameTimestamp(msg: DisplayMessage) {
    Row() {
        Text(
            text = msg.author,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .alignBy(LastBaseline)
                .paddingFrom(LastBaseline, after = 8.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = msg.timestamp,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.alignBy(LastBaseline),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private val ChatBubbleShapeMe = RoundedCornerShape(
    topStart = 16.dp,
    topEnd = 16.dp,
    bottomStart = 16.dp,
    bottomEnd = 4.dp
)
private val ChatBubbleShapeOther = RoundedCornerShape(
    topStart = 16.dp,
    topEnd = 16.dp,
    bottomStart = 4.dp,
    bottomEnd = 16.dp
)

@Composable
fun CodechatChatItemBubble(
    message: DisplayMessage,
    isUserMe: Boolean,
    authorClicked: (String?) -> Unit
) {
    val backgroundBubbleColor = if (isUserMe) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.secondaryContainer
    }
    val textColor = if (isUserMe) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSecondaryContainer
    }
    val bubbleShape = if (isUserMe) ChatBubbleShapeMe else ChatBubbleShapeOther

    Surface(
        color = backgroundBubbleColor,
        shape = bubbleShape,
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Text(
            text = message.content,
            style = MaterialTheme.typography.bodyLarge.copy(color = textColor),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
        )

    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CodechatUserInput(
    modifier: Modifier = Modifier,
    message: String,
    onMessageChange: (String) -> Unit,
    onSendMessage: () -> Unit,
    isSending: Boolean,
    resetScroll: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = message,
            onValueChange = onMessageChange,
            modifier = Modifier.weight(1f),
            placeholder = { Text("Type a message...") }, // TODO: R.string.textfield_hint
            shape = RoundedCornerShape(24.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                // TODO: Set background color based on theme, e.g., MaterialTheme.colorScheme.surface
            ),
            maxLines = 4,
            trailingIcon = {
                 if (isSending) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                }
            }
        )
        Spacer(modifier = Modifier.width(8.dp))

        IconButton(
            onClick = {
                if (message.isNotBlank()) {
                    onSendMessage()
                    resetScroll()
                }
            },
            enabled = message.isNotBlank() && !isSending,
            modifier = Modifier
                .size(48.dp)
                .background(
                    color = if (message.isNotBlank() && !isSending) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                    shape = CircleShape
                )
        ) {
            Icon(
                Icons.Filled.Send,
                contentDescription = "Send message", // TODO: R.string.send_message_description
                tint = if (message.isNotBlank() && !isSending) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface.copy(alpha = ContentAlpha.disabled)
            )
        }
    }
}
