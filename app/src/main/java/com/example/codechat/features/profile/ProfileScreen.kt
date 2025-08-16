package com.example.codechat.features.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.codechat.R
import com.example.codechat.core.ui.components.UserList
import com.example.codechat.domain.model.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
//    onEditProfileClick: () -> Unit,
    onUserClick: (User) -> Unit
) {
    val uiState = viewModel.uiState

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri: Uri? ->
//            if (uri != null) {
//                viewModel.onRefreshProfileImage(uri.toString())
//            }
        viewModel.updateProfileImage(uri)
        }
    )

    // Optionally, if you want to refresh data when the screen is first composed or recomposed under certain conditions
    // LaunchedEffect(key1 = Unit) {
    //     viewModel.fetchMyProfile()
    // }

    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Profile") },
//                colors = TopAppBarDefaults.topAppBarColors(
//                    containerColor = MaterialTheme.colorScheme.primaryContainer,
//                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
//                )
//            )
//        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                photoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }) {
                Icon(
                    imageVector = Icons.Filled.AddAPhoto,
                    contentDescription = "Edit Profile"
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when {
                uiState.isLoading && uiState.loggedInUser == null -> {
                    CircularProgressIndicator()
                }
                uiState.errorMessage != null && uiState.loggedInUser == null -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Error: ${uiState.errorMessage}",
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.fetchMyProfile() }) {
                            Text("Retry")
                        }
                    }
                }
                uiState.loggedInUser != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp) //
                    ) {
                        // Profile Header Section
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Spacer(modifier = Modifier.height(16.dp)) // Adjusted spacing
                            Box(contentAlignment = Alignment.Center) {
                                val imageUrl = uiState.loggedInUser.profileImage
                                val loggedInUser = uiState.loggedInUser
                                if (imageUrl != null) {
                                    val imageRequest = ImageRequest.Builder(LocalContext.current)
                                        .data(imageUrl)
                                        .memoryCachePolicy(CachePolicy.DISABLED)
                                        .diskCachePolicy(CachePolicy.DISABLED)
                                        .crossfade(true)
                                        // Key change: Use a combination of the URL and the User object's hashcode.
                                        // This ensures that if the User object instance changes (due to .copy()),
                                        // Coil treats this as a new request, even if imageUrl string is the same.
                                        .build()
                                    AsyncImage(
                                        model = imageRequest, // Use the request builder
                                        contentDescription = null,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(
                                                start = 16.dp,
                                                top = 26.dp,
                                                end = 16.dp
                                            )
                                            .clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .size(120.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.surfaceVariant),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Person,
                                            contentDescription = "Profile image placeholder", // Add to strings.xml
                                            modifier = Modifier.size(72.dp),
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                if (uiState.isUploadingImage) {
                                    CircularProgressIndicator(modifier = Modifier.size(48.dp)) // Smaller indicator over image
                                }
                            }
                            uiState.imageUploadError?.let { error ->
                                Text(
                                    text = error,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = uiState.loggedInUser.name.toString(),
                                style = MaterialTheme.typography.headlineSmall,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = uiState.loggedInUser.email.toString(),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                        }

                        Divider(
                            modifier = Modifier.padding(vertical = 16.dp),
                            color = MaterialTheme.colorScheme.outlineVariant
                        )

                        // Other Users Section
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                "Other Users",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            when {
                                uiState.isLoadingUsers -> {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator()
                                    }
                                }
                                uiState.userList.isNotEmpty() -> {
                                    UserList(
                                        users = uiState.userList,
                                        onUserClick = onUserClick
                                    )
                                }
                                else -> {
                                        Text(
                                            "No other users to display.",
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 16.dp),
                                            textAlign = TextAlign.Center,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp)) // Bottom spacer for scroll padding
                    }
                }
                else -> {
                        LaunchedEffect(Unit) {
                            viewModel.fetchMyProfile()
                        }
                        Text("Loading profile...")
                }
            }
        }
    }
}

// @Preview
// @Composable
// fun ProfileScreenPreview() {
//    // ProfileScreen(...)
// }
