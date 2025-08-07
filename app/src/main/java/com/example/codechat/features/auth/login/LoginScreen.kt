package com.example.codechat.features.auth.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.codechat.R
import com.example.codechat.core.ui.components.AuthTextField

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(),
    onLoginSuccess: () -> Unit,
    onRegisterClick: () -> Unit
){
    val state = viewModel.uiState


    LaunchedEffect(state.loginSuccess) {
        if(state.loginSuccess) {
                onLoginSuccess()
        }
    }


    Surface(modifier = Modifier.fillMaxSize()){
        Column(
            modifier = Modifier.padding(24.dp).fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            Image(painter = painterResource(id = R.drawable.code_chat),
                contentDescription = "CodeChat Logo",
                Modifier.size(120.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Sign in to CodeChat", style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(32.dp))

            AuthTextField(
                value = state.email,
                onValueChange = { viewModel.onEmailChange(it)},
                label = "Email",
                leadingIcon = Icons.Default.Email,
                keyBoardType = KeyboardType.Email,
                errorMessage =state.emailError
            )

            Spacer(modifier = Modifier.height(16.dp))

            AuthTextField(
                value = state.password,
                onValueChange = { viewModel.onPasswordChange(it) },
                label = "Password",
                leadingIcon = Icons.Default.Lock,
                keyBoardType = KeyboardType.Password,
                isPassword = true,
                errorMessage = state.passwordError
            )

            Box(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                TextButton(onClick = { /*TODO*/ },
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Text(
                        text = "Forgot password?",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }


            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewModel.login() },
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (state.isLoading) "Logging in..." else "Sign in")
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(onClick = { onRegisterClick() }){
                Text(text = "Don't have an account? Sign up")
            }

            if(state.loginErrorMessage != null) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = state.loginErrorMessage,
                    color = MaterialTheme.colorScheme.error
                )
            }

        }
    }
}

@Preview
@Composable
fun LoginScreenPreview(){
    LoginScreen(
        onLoginSuccess = {},
        onRegisterClick = {}
    )
}
