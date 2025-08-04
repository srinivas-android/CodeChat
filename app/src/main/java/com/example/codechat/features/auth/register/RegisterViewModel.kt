package com.example.codechat.features.auth.register

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codechat.core.utils.TokenManager
import com.example.codechat.domain.usecase.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase,
    private val tokenManager: TokenManager
) : ViewModel() {
    var uiState by mutableStateOf(RegisterUiState())

    fun onNameChange(value: String) {
        uiState = uiState.copy(name = value)
    }
    fun onEmailChange(value: String) {
        uiState = uiState.copy(email = value)
    }
    fun onPasswordChange(value: String) {
        uiState = uiState.copy(password = value)
    }
    fun onConfirmPasswordChange(value: String){
        uiState = uiState.copy(confirmPassword = value)
    }

    fun registerUser() {
        val name = uiState.name
        val email = uiState.email
        val password = uiState.password
        val confirmPassword = uiState.confirmPassword

        var hasError = false

        if(name.isBlank()) {
            uiState = uiState.copy(nameError = "Name is required")
            hasError = true
        }

        if(email.isBlank()) {
            uiState = uiState.copy(emailError = "Email is required")
            hasError = true
        }

        if(password.isBlank()) {
            uiState = uiState.copy(passwordError = "Password is required")
            hasError = true
        }

        if(confirmPassword != password) {
            uiState = uiState.copy(confirmPasswordError = "Passwords do not match")
            hasError = true
        }

        if(hasError) return

        uiState = uiState.copy(isLoading = true)

        viewModelScope.launch {
            try {
                val result = registerUseCase(name, email, password)
                tokenManager.saveToken(result.token.toString())
                uiState = uiState.copy(isLoading = false, registerSuccess = true)
            }
            catch (e: Exception) {
                uiState = uiState.copy(registerErrorMessage = e.message, isLoading = false)
            }

        }
    }

    fun clearError() {
        uiState = uiState.copy(registerErrorMessage = null)
    }

}