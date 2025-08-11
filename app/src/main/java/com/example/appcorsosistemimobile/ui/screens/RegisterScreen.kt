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

@Composable
fun RegisterScreen(
    authViewModel: AuthViewModel,
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var isEmailOk by remember { mutableStateOf(true) }
    var password by remember { mutableStateOf("") }
    var isPasswordOk by remember { mutableStateOf(true) }
    var passwordVisibility by remember { mutableStateOf(false) }
    var confirmPassword by remember { mutableStateOf("") }
    var isConfirmPasswordOk by remember { mutableStateOf(true) }
    var confirmPasswordVisibility by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var isNameOk by remember { mutableStateOf(true) }
    var surname by remember { mutableStateOf("") }
    var isSurnameOk by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val passwordIcon = if(passwordVisibility)
        Icons.Filled.Visibility
    else
        Icons.Filled.VisibilityOff

    val confirmPasswordIcon = if(confirmPasswordVisibility)
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
        Text("Registrazione", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nome") },
            modifier = Modifier.fillMaxWidth(),
            supportingText = {
                if (!isNameOk) {
                    Text(
                        text = "Questo campo è obbligatorio.",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        )

        OutlinedTextField(
            value = surname,
            onValueChange = { surname = it },
            label = { Text("Cognome") },
            modifier = Modifier.fillMaxWidth(),
            supportingText = {
                if (!isSurnameOk) {
                    Text(
                        text = "Questo campo è obbligatorio.",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        )

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

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Conferma Password") },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = { confirmPasswordVisibility = !confirmPasswordVisibility }) {
                    Icon(
                        imageVector = confirmPasswordIcon,
                        contentDescription = "Visibility Icon"
                    )
                }
            },
            supportingText = {
                if (!isConfirmPasswordOk) {
                    Text(
                        text = "Questo campo è obbligatorio.",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            visualTransformation = if(confirmPasswordVisibility)
                VisualTransformation.None
            else
                PasswordVisualTransformation()
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                isNameOk = name.isNotBlank()
                isSurnameOk = surname.isNotBlank()
                isEmailOk = email.isNotBlank()
                isPasswordOk = password.isNotBlank()
                isConfirmPasswordOk = confirmPassword.isNotBlank()

                if(isNameOk && isSurnameOk && isEmailOk && isPasswordOk && isConfirmPasswordOk) {
                    authViewModel.register(
                        name = name,
                        surname = surname,
                        email = email,
                        password = password,
                        confirmPassword = confirmPassword,
                        onSuccess = onRegisterSuccess,
                        onError = { errorMessage = it }
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Crea account")
        }

        TextButton(
            onClick = onNavigateToLogin,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Hai già un account? Accedi")
        }

        errorMessage?.let {
            Spacer(Modifier.height(12.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }
    }
}
