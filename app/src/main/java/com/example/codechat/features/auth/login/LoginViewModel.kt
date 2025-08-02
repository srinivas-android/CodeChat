package com.example.codechat.features.auth.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    var uiState by mutableStateOf(LoginUiState())

    fun onEmailChange(email: String) {
        uiState = uiState.copy(email = email)
    }

    fun onPasswordChange(password: String) {
        uiState = uiState.copy(password = password)
    }

    fun login() {
        if (uiState.email.isBlank()) {
            uiState.copy(emailError = "Email is required")
            return
        }
        if (uiState.password.isBlank()) {
            uiState.copy(passwordError = "Password is required")
            return
        }
        uiState = uiState.copy(isLoading = true)

        viewModelScope.launch {
            delay(1000)

            if (uiState.email == "admin" && uiState.password == "password") {
                uiState = uiState.copy(isLoading = false, loginSuccess = true)
            } else {
                uiState = uiState.copy(
                    isLoading = false,
                    loginErrorMessage = "Invalid credentials"
                )
            }
        }
    }

    fun clearLoginError() {
        uiState = uiState.copy(loginErrorMessage = null)
    }
}