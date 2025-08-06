package com.example.codechat.features.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codechat.core.utils.TokenManager
// TokenManager import might be needed later for things like logout, but not directly for getMyProfile
// import com.example.codechat.core.utils.TokenManager
import com.example.codechat.domain.model.User
import com.example.codechat.domain.usecase.GetMyProfileUseCase
import com.example.codechat.domain.usecase.GetUsersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject




@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getMyProfileUseCase: GetMyProfileUseCase,
    private val getUsersUseCase: GetUsersUseCase,
    private val tokenManager: TokenManager // <--
): ViewModel() {
    var uiState by mutableStateOf(ProfileUiState())
        private set

    init {
        fetchMyProfile()
        fetchUsers() // <--
    }

    fun fetchMyProfile() {
        viewModelScope.launch {
            val userId = tokenManager.getUserId()
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            try {
                val profile = getMyProfileUseCase(userId = userId.toString())
                uiState = uiState.copy(loggedInUser = profile, isLoading = false)
            } catch (e: Exception) {
                uiState = uiState.copy(errorMessage = e.message ?: "An unexpected error occurred", isLoading = false)
            }
        }
    }

    fun fetchUsers() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoadingUsers = true, errorMessage = null)
            try {
                val users = getUsersUseCase()
                uiState = uiState.copy(userList = users, isLoadingUsers = false)
            } catch (e: Exception) {
                uiState = uiState.copy(errorMessage = e.message ?: "An unexpected error occurred", isLoadingUsers = false)
            }
        }
    }

    fun onRefreshProfileImage(url: String) {
        val updatedUser = uiState.loggedInUser?.copy(profileImage = url)
        uiState = uiState.copy(loggedInUser = updatedUser)
    }
}
