package com.example.codechat.core.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.codechat.domain.model.User

@Composable
fun UserList(users: List<User>, onUserClick: (User) -> Unit) {
    Column{
        users.forEach { user ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onUserClick(user) }
                    .padding(8.dp)
            ) {
                Text (text = user.name.toString())
            }

        }
    }
}