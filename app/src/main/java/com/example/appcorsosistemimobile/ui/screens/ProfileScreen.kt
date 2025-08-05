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
    val user = authViewModel.currentUser

    // Quando l’utente si logga, carica il profilo se non già caricato
    LaunchedEffect(authViewModel.isLoggedIn) {
        if (authViewModel.isLoggedIn && user == null) {
            authViewModel.currentUserEmail?.let {
                authViewModel.loadUserProfile(it)
            }
        }
    }

    if (authViewModel.isLoggedIn) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (user != null) {
                    Text("Ciao, ${user.name} ${user.surname}!", style = MaterialTheme.typography.headlineSmall)
                    Text("Email: ${user.email}", style = MaterialTheme.typography.bodyMedium)
                    Text("Immersioni preferite:", style = MaterialTheme.typography.titleMedium)

                    if (user.favouriteDiveSite.isEmpty()) {
                        Text("Nessuna immersione preferita.")
                    } else {
                        user.favouriteDiveSite.forEach { diveId ->
                            Text("- $diveId")
                        }
                    }
                } else {
                    CircularProgressIndicator()
                }

                Spacer(Modifier.height(24.dp))

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


