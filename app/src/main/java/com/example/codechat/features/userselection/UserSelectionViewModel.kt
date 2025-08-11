package com.example.codechat.features.userselection

import androidx.activity.result.launch
import androidx.compose.animation.core.copy
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codechat.core.utils.TokenManager
import com.example.codechat.domain.model.User
import com.example.codechat.domain.usecase.GetUsersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


data class UserSelectionUiState(
    val isLoading: Boolean = true,
    val users: List<User> = emptyList(),
    val errorMessage: String? = null
)

@HiltViewModel
class UserSelectionViewModel @Inject constructor(
    private val getUsersUseCase: GetUsersUseCase,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserSelectionUiState())
    val uiState: StateFlow<UserSelectionUiState> = _uiState.asStateFlow()

    init {
        fetchUsers()
    }

    fun fetchUsers() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val currentUserId = tokenManager.getUserId()?.toInt()
                val usersList = getUsersUseCase()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        users = usersList.filter { user -> user.id != currentUserId }
                    )
                }
            } catch (e: Exception) {
                // Log the exception e
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = e.message ?: "Failed to load users")
                }
            }
        }
    }
}
