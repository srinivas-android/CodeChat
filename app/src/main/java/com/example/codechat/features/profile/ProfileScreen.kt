package com.example.codechat.features.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.codechat.core.ui.components.UserList
import com.example.codechat.domain.model.User

@Composable
fun ProfileScreen(
    user: User,
    onEditImageClick: () -> Unit,
    users: List<User>,
    onUserClick: (User) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text ("My Profile", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {/*onEditImageClick*/},
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ){
            Text("Edit Profile")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Divider()

        Text("Other Users", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))

        UserList(
            users = users,
            onUserClick = onUserClick
        )


    }
}

//@Preview
//@Composable
//fun ProfileScreenPreview() {
//    ProfileScreen()
//}