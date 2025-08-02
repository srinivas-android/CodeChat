package com.example.codechat.core.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AuthTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: ImageVector? = null,
    keyBoardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    errorMessage: String? = null
){
    var isPasswordVisible by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(text = label) },
            leadingIcon = {
                if (leadingIcon != null) {
                    Icon(imageVector = leadingIcon, contentDescription = null)
                }
            },
            trailingIcon = if(isPassword) {
                {
                    val visibilityIcon = if (isPasswordVisible) {
                        Icons.Filled.Visibility
                    } else {
                        Icons.Filled.VisibilityOff
                    }
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(imageVector = visibilityIcon, contentDescription = null)
                    }
                }
            }
            else null,
            visualTransformation = if (isPassword && !isPasswordVisible) {
                PasswordVisualTransformation()
            }
            else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = keyBoardType),
            isError = errorMessage != null,
            singleLine = true,
            textStyle = TextStyle(fontSize = 16.sp),
            modifier = Modifier.fillMaxWidth()
        )
        if(errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp, start = 16.dp)
                )

        }
    }
}

@Preview(showBackground = true)
@Composable
fun AuthTextFieldPreview() {
    var text by remember { mutableStateOf("") }

    MaterialTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            AuthTextField(
                value = text,
                onValueChange = { text = it },
                label = "Email",
                leadingIcon = Icons.Default.Email,
                keyBoardType = KeyboardType.Email
            )

            Spacer(modifier = Modifier.height(16.dp))

            AuthTextField(
                value = text,
                onValueChange = { text = it },
                label = "Password",
                leadingIcon = Icons.Default.Lock,
                isPassword = true,
                errorMessage = "Password too short"
            )
        }
    }
}