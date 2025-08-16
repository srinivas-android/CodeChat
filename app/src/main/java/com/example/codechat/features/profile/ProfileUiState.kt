package com.example.codechat.features.profile

import com.example.codechat.domain.model.User

data class ProfileUiState(
    val loggedInUser: User? = null,
    val userList: List<User> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingUsers: Boolean = false,
    val errorMessage: String? = null,
    val isUploadingImage: Boolean = false,
    val imageUploadError: String? = null
)