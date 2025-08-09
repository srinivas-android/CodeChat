package com.example.codechat.core.navigation

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val PROFILE = "profile"
    const val MAIN = "main_screen"
    const val CHAT_LIST = "chat_list"
    const val CHAT = "chat/{userId}"
    const val CHAT_SCREEN_BASE = "chat_screen" // Base for constructing the route with args

    const val SETTINGS = "settings"
    const val CONTACTS = "contacts"
    const val USER_SELECTION = "user_selection"
    const val ARG_ROOM_ID = "roomId"
    const val ARG_CHAT_USER_ID = "chatUserId"
    const val LOGOUT = "logout"
}