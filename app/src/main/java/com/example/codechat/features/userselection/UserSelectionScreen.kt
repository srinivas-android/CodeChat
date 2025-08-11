package com.example.codechat.features.userselection

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.codechat.domain.model.User
// Optional: If you have a shared ProfileImage composable
// import com.example.codechat.core.presentation.components.ProfileImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserSelectionScreen(
    onUserSelected: (selectedUserId: Int) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: UserSelectionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Start New Chat With") },
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
            )
        }
    ) { paddingValues ->
        UserSelectionContent(
            modifier = Modifier.padding(paddingValues),
            uiState = uiState,
            onUserClick = { user ->
                onUserSelected(user.id) // Pass the selected user's ID
            }
        )
    }
}

@Composable
fun UserSelectionContent(
    modifier: Modifier = Modifier,
    uiState: UserSelectionUiState, // This UiState should be from UserSelectionViewModel
    onUserClick: (User) -> Unit
) {
    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        when {
            uiState.isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            uiState.errorMessage != null -> {
                Column(
                    modifier = Modifier.align(Alignment.Center).padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        "Error: ${uiState.errorMessage}",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    // Optional: Add a retry button
                    // Button(onClick = { /* Call viewModel.fetchUsers() */ }, Modifier.padding(top = 8.dp)) {
                    //    Text("Retry")
                    // }
                }
            }
            uiState.users.isEmpty() && !uiState.isLoading -> { // Check isLoading to avoid showing "No users" during initial load
                Text(
                    "No users found to start a new chat.",
                    modifier = Modifier.align(Alignment.Center).padding(16.dp)
                )
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(0.dp) // No extra padding for the LazyColumn itself
                ) {
                    items(uiState.users, key = { it.id }) { user ->
                        UserItem(
                            user = user,
                            onClick = { onUserClick(user) }
                        )
                        Divider() // Adds a line between items
                    }
                }
            }
        }
    }
}

@Composable
fun UserItem(
    user: User,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = { Text(user.name ?: "Unknown User", style = MaterialTheme.typography.titleMedium) },
        supportingContent = { user.email?.let { Text(it, style = MaterialTheme.typography.bodyMedium) } },
// leadingContent = { // Uncomment and use if you have a ProfileImage composable
//        //    ProfileImage(
//        //        imageUrl = user.profileImage, // Assuming user.profileImage is a String URL
//        //        contentDescription = "${user.name ?: "User"}'s profile picture",
//        //        size = 40.dp // Example size
//        //    )
//        // },
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp) // Adjusted padding for better spacing
    )
}