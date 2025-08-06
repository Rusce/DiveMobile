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
    onNavigateToRegister: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()
    val currentUserEmail by authViewModel.currentUserEmail.collectAsState()

    // Carica profilo se loggato e currentUser non ancora presente
    LaunchedEffect(isLoggedIn, currentUser) {
        if (isLoggedIn && currentUser == null) {
            currentUserEmail?.let { authViewModel.loadUserProfile(it) }
        }
    }

    if (isLoggedIn) {
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
                if (currentUser != null) {
                    Text("Ciao, ${currentUser!!.name} ${currentUser!!.surname}!", style = MaterialTheme.typography.headlineSmall)
                    Text("Email: ${currentUser!!.email}", style = MaterialTheme.typography.bodyMedium)
                    Text("Immersioni preferite:", style = MaterialTheme.typography.titleMedium)

                    if (currentUser!!.favouriteDiveSite.isEmpty()) {
                        Text("Nessuna immersione preferita.")
                    } else {
                        currentUser!!.favouriteDiveSite.forEach { diveId ->
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
