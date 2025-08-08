package com.example.codechat.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.codechat.features.chatlist.ChatListScreen // Ensure this import is correct
import com.example.codechat.features.profile.ProfileScreen

@Composable
fun AppNavGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(navController, startDestination = Routes.PROFILE, modifier = modifier) { // Also applied the modifier parameter
        composable(Routes.CHAT_LIST) {
            ChatListScreen( onChatRoomClick = { roomId ->
                navController.navigate("chat_room/$roomId")
            })
        }
        composable(Routes.CHAT){

        }
        composable("contacts") {
            // TODO: Add your Contacts screen composable here
        }
        composable("settings") {
            // TODO: Add your Settings screen composable here
        }
        composable("profile") {
            ProfileScreen(
//                onEditProfileClick = {
//                navController.navigate("edit_profile")
//                },
                onUserClick = { user ->
                navController.navigate("user_detail/${user.id}")
                }
            )
        }
        composable("about") {
            // TODO: Add your About screen composable here
        }
        composable("logout") {
            // TODO: Handle logout logic and navigation
        }
    }
}