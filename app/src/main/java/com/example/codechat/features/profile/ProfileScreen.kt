package com.example.codechat.features.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.codechat.core.ui.components.UserList // Assuming this component is ready
import com.example.codechat.domain.model.User

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onEditProfileClick: () -> Unit,
    onUserClick: (User) -> Unit
) {
    val uiState = viewModel.uiState

    // Optionally, if you want to refresh data when the screen is first composed or recomposed under certain conditions
    // LaunchedEffect(key1 = Unit) {
    //     viewModel.fetchMyProfile()
    // }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
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
                    Button(onClick = { viewModel.fetchMyProfile() }) { // Retry button
                        Text("Retry")
                    }
                }
            }
            uiState.loggedInUser != null -> {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text("My Profile", style = MaterialTheme.typography.headlineMedium)
                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Name: ${uiState.loggedInUser.name}", style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Email: ${uiState.loggedInUser.email}", style = MaterialTheme.typography.bodyLarge)

                     uiState.loggedInUser.profileImage?.let { imageUrl ->
                         AsyncImage(model = imageUrl, contentDescription = "Profile Image")
                     }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onEditProfileClick,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text("Edit Profile")
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Other Users", style = MaterialTheme.typography.headlineSmall)
                    Spacer(modifier = Modifier.height(8.dp))

                    if (uiState.isLoadingUsers) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                    } else if (uiState.userList.isNotEmpty()) {
                        UserList(
                            users = uiState.userList,
                            onUserClick = onUserClick
                        )
                    } else {
                        Text(
                            "No other users to display.",
                             modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                }
            }
            else -> {

                Text("Loading profile...")
                LaunchedEffect(Unit) {
                    viewModel.fetchMyProfile()
                }
            }
        }
    }
}

// Preview would need to be updated to mock ProfileViewModel or provide dummy state
// @Preview
// @Composable
// fun ProfileScreenPreview() {
//    // ProfileScreen(...)
// }
