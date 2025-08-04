package com.example.codechat.features.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun NavigationDrawerContent(onItemClick: (String) -> Unit) {
    ModalDrawerSheet {
        Text("CodeChat", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(16.dp))

        NavigationDrawerItem(
            label = { Text("About") },
            selected = false,
            onClick = { onItemClick("about") },
            icon = { Icon(Icons.Default.Info, contentDescription = null)}
        )

        NavigationDrawerItem(
            label = { Text("Logout")},
            selected = false,
            onClick = { onItemClick("logout") },
            icon = { Icon(Icons.Default.ExitToApp, contentDescription = null)}
        )
    }
}