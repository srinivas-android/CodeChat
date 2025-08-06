package com.example.codechat.features.chatlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codechat.domain.usecase.GetUserChatRoomsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatListViewModel @Inject constructor(
    private val getUserChatRoomsUseCase: GetUserChatRoomsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatListUiState())
    val uiState = _uiState.asStateFlow()

    init {
        fetchChatRooms()
    }

    fun fetchChatRooms() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val rooms = getUserChatRoomsUseCase()
                _uiState.value = _uiState.value.copy(chatRooms = rooms, isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message ?: "Failed to load chat rooms",
                    isLoading = false
                )
            }
        }
    }
}
