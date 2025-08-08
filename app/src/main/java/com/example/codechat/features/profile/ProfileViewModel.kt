package com.example.codechat.features.profile

import android.net.Uri // Added
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codechat.core.utils.TokenManager
import com.example.codechat.domain.usecase.GetMyProfileUseCase
import com.example.codechat.domain.usecase.GetUsersUseCase
import com.example.codechat.domain.usecase.UpdateProfileImageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getMyProfileUseCase: GetMyProfileUseCase,
    private val getUsersUseCase: GetUsersUseCase,
    private val tokenManager: TokenManager,
    private val updateProfileImageUseCase: UpdateProfileImageUseCase
): ViewModel() {
    var uiState by mutableStateOf(ProfileUiState())
        private set

    init {
        fetchMyProfile()
        fetchUsers()
    }

    fun fetchMyProfile() {
        viewModelScope.launch {
            val userId = tokenManager.getUserId()
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            try {
                val profile = getMyProfileUseCase(userId = userId.toString()) // Ensure this matches your use case signature
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
                uiState = uiState.copy(errorMessage = e.message ?: "An unexpected error occurred while fetching users", isLoadingUsers = false)
            }
        }
    }

    // This function was from the original file, it locally updates the state.
    // It might be redundant or serve a different purpose now.
    // Consider if this is still needed or if updateProfileImage handles all image updates.
    fun onRefreshProfileImage(url: String) {
        val updatedUser = uiState.loggedInUser?.copy(profileImage = url)
        uiState = uiState.copy(loggedInUser = updatedUser)
    }


    fun updateProfileImage(uri: Uri?) {
        if (uri == null) {
            uiState = uiState.copy(imageUploadError = "No image selected.")
            return
        }

        viewModelScope.launch {
            val userId = tokenManager.getUserId()
            uiState = uiState.copy(isUploadingImage = true, imageUploadError = null)
            updateProfileImageUseCase(userId.toString(), uri)
                .onSuccess { newImageUrl ->
                    val updatedUser = uiState.loggedInUser?.copy(profileImage = newImageUrl)
                    uiState = uiState.copy(
                        loggedInUser = updatedUser,
                        isUploadingImage = false
                    )
                    onRefreshProfileImage(uri.toString())
                }
                .onFailure { exception ->
                    uiState = uiState.copy(
                        imageUploadError = exception.message ?: "Failed to upload image.",
                        isUploadingImage = false
                    )
                }
        }
    }
}

