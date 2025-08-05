package com.example.codechat.features.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codechat.core.utils.TokenManager
import com.example.codechat.domain.model.User
import com.example.codechat.domain.usecase.GetUsersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
/*

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase,
    private val getUsersUseCase: GetUsersUseCase
): ViewModel() {
    var uiState by mutableStateOf(ProfileUiState())

    init {
        fetchProfile()
        fetchUsers()
    }

    fun fetchProfile() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            try {
                val profile = getProfileUseCase()
                uiState = uiState.copy(loggedInUser = profile, isLoading = false)
            }
            catch (e: Exception) {
                uiState = uiState.copy(errorMessage = e.localizedMessage, isLoading = false)
            }
        }
    }

    fun fetchUsers() {
        viewModelScope.launch {
            try {
                val users = getUsersUseCase()
                uiState = uiState.copy(userList = users)
            } catch (e: Exception) {
                uiState = uiState.copy(errorMessage = e.localizedMessage)
            }
        }
    }

    fun onRefreshProfileImage(url: String) {
        val updatedUser = uiState.loggedInUser?.copy(profileImage = url)
        uiState = uiState.copy(loggedInUser = updatedUser)
    }
}*/