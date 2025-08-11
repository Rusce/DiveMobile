package com.example.appcorsosistemimobile.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appcorsosistemimobile.viewmodel.AuthViewModel

//TODO validazione input

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var isEmailOk by remember { mutableStateOf(true) }
    var password by remember { mutableStateOf("") }
    var isPasswordOk by remember { mutableStateOf(true) }
    var passwordVisibility by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val passwordIcon = if(passwordVisibility)
            Icons.Filled.Visibility
        else
            Icons.Filled.VisibilityOff

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Login", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            supportingText = {
                if (!isEmailOk) {
                    Text(
                        text = "Questo campo è obbligatorio.",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                    Icon(
                        imageVector = passwordIcon,
                        contentDescription = "Visibility Icon"
                    )
                }
            },
            supportingText = {
                if (!isPasswordOk) {
                    Text(
                        text = "Questo campo è obbligatorio.",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            visualTransformation = if(passwordVisibility)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation()
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                isEmailOk = email.isNotBlank()
                isPasswordOk = password.isNotBlank()
                if(isEmailOk && isPasswordOk) {
                    authViewModel.login(email, password, onSuccess = onLoginSuccess) {
                        errorMessage = it
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Accedi")
        }

        TextButton(
            onClick = onNavigateToRegister,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Non hai un account? Registrati")
        }

        errorMessage?.let {
            Spacer(Modifier.height(12.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }
    }
}

