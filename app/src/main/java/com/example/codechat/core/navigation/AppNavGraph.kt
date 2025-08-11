package com.example.codechat.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.codechat.features.chat.ChatScreen
import com.example.codechat.features.chatlist.ChatListScreen // Ensure this import is correct
import com.example.codechat.features.main.MainViewModel
import com.example.codechat.features.profile.ProfileScreen
import com.example.codechat.features.userselection.UserSelectionScreen

@Composable
fun AppNavGraph(navController: NavHostController,
                modifier: Modifier = Modifier
) {
    NavHost(navController,
        startDestination = Routes.PROFILE,
        modifier = modifier
    ) { // Also applied the modifier parameter
        composable(Routes.CHAT_LIST) {
            ChatListScreen(
                onNavigateToChat = { roomId, _, _ ->
                    navController.navigate("${Routes.CHAT_SCREEN_BASE}?${Routes.ARG_ROOM_ID}=${roomId}")
                },
                onNavigateToNewChatSelection = {
                    navController.navigate(Routes.USER_SELECTION)
                }
            )
        }

        composable(
            route = "${Routes.CHAT_SCREEN_BASE}?${Routes.ARG_ROOM_ID}={${Routes.ARG_ROOM_ID}}&${Routes.ARG_CHAT_USER_ID}={${Routes.ARG_CHAT_USER_ID}}",
            arguments = listOf(
                navArgument(Routes.ARG_ROOM_ID) {
                    type = NavType.StringType
                    nullable = true
                },
                navArgument(Routes.ARG_CHAT_USER_ID) {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) {
            ChatScreen(
                onNavigateBack = { navController.popBackStack() }
                // ChatViewModel will pick up roomId or chatUserId from SavedStateHandle
            )
        }

        composable(Routes.USER_SELECTION)
        {
            UserSelectionScreen(
                onUserSelected = { selectedUserId ->
                    navController.navigate("${Routes.CHAT_SCREEN_BASE}?${Routes.ARG_CHAT_USER_ID}=$selectedUserId") {
                        popUpTo(Routes.USER_SELECTION) { inclusive = true }
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("contacts") {
            // TODO: Add your Contacts screen composable here
        }
        composable("settings") {
            // TODO: Add your Settings screen composable here
        }
        composable(Routes.PROFILE) {
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

        composable(Routes.LOGOUT) {
            val mainViewModel: MainViewModel = hiltViewModel()
            LaunchedEffect(Unit) {
                mainViewModel.logout()
            }
        }

    }
}