package com.example.codechat.features.auth.login

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codechat.core.utils.TokenManager
import com.example.codechat.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val tokenManager: TokenManager
) : ViewModel() {
    var uiState by mutableStateOf(LoginUiState())

    fun onEmailChange(email: String) {
        uiState = uiState.copy(email = email)
    }

    fun onPasswordChange(password: String) {
        uiState = uiState.copy(password = password)
    }

    fun login() {
        if (uiState.email.isBlank()) {
            uiState = uiState.copy(emailError = "Email is required")
            return
        }
        if (uiState.password.isBlank()) {
            uiState = uiState.copy(passwordError = "Password is required")
            return
        }
        uiState = uiState.copy(isLoading = true)

        viewModelScope.launch {
            delay(1000)

            try {
                val result = loginUseCase(uiState.email, uiState.password)
                tokenManager.saveToken(result.token.toString())
                uiState = uiState.copy(isLoading = false, loginSuccess = true)
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    loginErrorMessage = e.localizedMessage ?: "Invalid credentials"
                )
            }

//            if (uiState.email == "admin" && uiState.password == "password") {
//                uiState = uiState.copy(isLoading = false, loginSuccess = true)
//            } else {
//                uiState = uiState.copy(
//                    isLoading = false,
//                    loginErrorMessage = "Invalid credentials"
//                )
//            }
        }
    }

    fun clearLoginError() {
        uiState = uiState.copy(loginErrorMessage = null)
    }
}