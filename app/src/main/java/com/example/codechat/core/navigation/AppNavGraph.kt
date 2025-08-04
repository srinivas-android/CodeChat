package com.example.codechat.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun AppNavGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(navController, startDestination = "chats", modifier = Modifier) {
        composable("chats") {}
        composable("contacts") {}
        composable("settings") {}
        composable("profile") {}
        composable("about") {}
        composable("logout") {}
    }
}