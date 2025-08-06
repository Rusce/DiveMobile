package com.example.appcorsosistemimobile.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.appcorsosistemimobile.data.model.DiveSiteComment
import com.example.appcorsosistemimobile.repository.DiveSiteRepository
import com.example.appcorsosistemimobile.viewmodel.AuthViewModel
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCommentScreen(
    diveSiteId: String,
    onBackClick: () -> Unit,
    navController: NavController,
    authViewModel: AuthViewModel = viewModel()
) {
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()
    val authorName = currentUser?.name ?: "Sconosciuto"
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    if (!isLoggedIn) {
        LaunchedEffect(Unit) {
            navController.navigate("profile")
        }
        return
    }

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var stars by remember { mutableIntStateOf(3) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Aggiungi commento") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Indietro")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Titolo") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descrizione") },
                modifier = Modifier.fillMaxWidth()
            )

            Text("Valutazione: $stars stelle")
            Slider(
                value = stars.toFloat(),
                onValueChange = { stars = it.toInt() },
                valueRange = 1f..5f,
                steps = 3
            )

            Button(onClick = {
                val comment = DiveSiteComment(
                    id = UUID.randomUUID().toString(),
                    diveId = diveSiteId,
                    authorName = authorName,
                    title = title,
                    description = description,
                    stars = stars,
                    createdAt = System.currentTimeMillis()
                )

                scope.launch {
                    val result = DiveSiteRepository.addCommentToDiveSite(diveSiteId, comment)
                    successMessage = if (result.isSuccess) {
                        "Commento aggiunto con successo!"
                    } else {
                        "Errore: ${result.exceptionOrNull()?.message}"
                    }
                }
            }) {
                Text("Salva commento")
            }

            successMessage?.let {
                Text(it, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
