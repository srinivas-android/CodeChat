package com.example.codechat.features.auth.login

data class LoginUiState(
    val email: String = "",
    val password: String= "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val isLoading: Boolean = false,
    val loginSuccess: Boolean = false,
    val loginErrorMessage: String? = null
)