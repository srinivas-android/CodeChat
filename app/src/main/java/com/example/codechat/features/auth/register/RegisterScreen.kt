package com.example.codechat.features.auth.register

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.codechat.core.ui.components.AuthTextField

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel = viewModel(),
    onRegisterSuccess: () -> Unit,
    onLoginClick: () -> Unit
) {
    val state = viewModel.uiState

    if(state.registerSuccess) {
        onRegisterSuccess()
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.
                    padding(24.dp).fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Create an account", style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(24.dp))

            AuthTextField(
                value = state.name,
                onValueChange = viewModel :: onNameChange,
                label = "Name",
                leadingIcon = Icons.Default.Person,
                errorMessage = state.nameError
            )

            Spacer(modifier = Modifier.height(16.dp))

            AuthTextField(
                value = state.email,
                onValueChange = viewModel :: onEmailChange,
                label = "Email",
                leadingIcon = Icons.Default.Email,
                errorMessage = state.emailError
            )

            Spacer(modifier = Modifier.height(16.dp))

            AuthTextField(
                value = state.password,
                onValueChange = viewModel :: onPasswordChange,
                label = "Password",
                leadingIcon = Icons.Default.Lock,
                errorMessage = state.passwordError,
                isPassword = true,
                keyBoardType = KeyboardType.Password
            )

            Spacer(modifier = Modifier.height(16.dp))

            AuthTextField(
                value = state.confirmPassword,
                onValueChange = viewModel :: onConfirmPasswordChange,
                label = "Confirm Password",
                leadingIcon = Icons.Default.Lock,
                errorMessage = state.confirmPasswordError,
                isPassword = true,
                keyBoardType = KeyboardType.Password
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    viewModel.registerUser()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading
            ) {
                Text(if(state.isLoading) "Loading..." else "Register")
            }

            TextButton(onClick = onLoginClick) {
                Text("Already have an account? Sign in")
            }

            if(state.registerErrorMessage != null) {

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = state.registerErrorMessage,
                    color = MaterialTheme.colorScheme.error
                )
        }

        }
    }
}
