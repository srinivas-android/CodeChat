package com.example.codechat.features.chat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ChatScreen(viewModel :ChatViewModel = viewModel()) {
    val state = viewModel.uiState

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        LazyColumn(modifier = Modifier.weight(1f), reverseLayout = true) {
            items(state.messages.reversed()) { message ->
                Text("${message.sender}: ${message.content}")
            }
        }

        OutlinedTextField(
            value = state.newMessageText,
            onValueChange = { viewModel.onMessageTextChanged(it) },
            label = { Text("Type a message") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = { viewModel.sendMessage() },
            modifier = Modifier.align(Alignment.End).padding(top = 8.dp)
        ){
            Text("Send")
        }
    }
}