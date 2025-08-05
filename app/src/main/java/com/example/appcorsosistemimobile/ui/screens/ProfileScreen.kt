package com.example.appcorsosistemimobile.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appcorsosistemimobile.viewmodel.AuthViewModel

@Composable
fun ProfileScreen(
    onNavigateToRegister: () -> Unit
) {
    val authViewModel: AuthViewModel = viewModel()

    if (authViewModel.isLoggedIn) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Loggato come: ${authViewModel.currentUserEmail}")
                Button(onClick = { authViewModel.logout() }) {
                    Text("Logout")
                }
            }
        }
    } else {
        LoginScreen(
            onLoginSuccess = { /* recomposition automatica */ },
            onNavigateToRegister = onNavigateToRegister
        )
    }
}

