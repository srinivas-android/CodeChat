package com.example.codechat.features.profile

import com.example.codechat.domain.model.User

data class ProfileUiState (
    val isLoading: Boolean = false,
    val loggedInUser: User? = null,
    val userList: List<User> = emptyList(),
    val errorMessage: String? = null
)