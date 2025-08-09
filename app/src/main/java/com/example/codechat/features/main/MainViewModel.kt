package com.example.codechat.features.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codechat.core.utils.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _logoutSignal = MutableSharedFlow<Unit>()
    val logoutSignal = _logoutSignal.asSharedFlow()

    fun logout() {
        viewModelScope.launch {
            tokenManager.clearAuthData()
            _logoutSignal.emit(Unit)
        }
    }
}
