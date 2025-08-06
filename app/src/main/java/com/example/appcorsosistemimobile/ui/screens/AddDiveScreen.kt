package com.example.appcorsosistemimobile.ui.screens

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.appcorsosistemimobile.data.model.DiveSite
import com.example.appcorsosistemimobile.repository.DiveSiteRepository
import com.example.appcorsosistemimobile.viewmodel.AuthViewModel
import kotlinx.coroutines.launch
import java.util.*

//TODO validazione input
//TODO rotellina per caricamento nuovo divesite (pulsanti background disattivati e reset dei campi)

@Composable
fun AddDiveScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel()
) {
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()
    val currentUserEmail by authViewModel.currentUserEmail.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(isLoggedIn, currentUser, currentUserEmail) {
        if (isLoggedIn && currentUser == null && currentUserEmail != null) {
            authViewModel.loadUserProfile(currentUserEmail!!)
        }
    }

    if (!isLoggedIn) {
        LaunchedEffect(Unit) {
            navController.navigate("profile")
        }
        return
    }

    if (currentUser == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val authorName = "${currentUser!!.name} ${currentUser!!.surname}"

    LaunchedEffect(currentUser) {
        Log.d("USER_DEBUG", "Current user: $authorName")
    }

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var latitude by remember { mutableStateOf("") }
    var longitude by remember { mutableStateOf("") }
    var minDepth by remember { mutableStateOf("") }
    var maxDepth by remember { mutableStateOf("") }
    var imageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    val pickImagesLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris -> imageUris = uris }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Aggiungi Luogo di Immersione", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nome") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Descrizione") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = latitude, onValueChange = { latitude = it }, label = { Text("Latitudine") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = longitude, onValueChange = { longitude = it }, label = { Text("Longitudine") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = minDepth, onValueChange = { minDepth = it }, label = { Text("Profondità Min") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = maxDepth, onValueChange = { maxDepth = it }, label = { Text("Profondità Max") }, modifier = Modifier.fillMaxWidth())

        Button(onClick = { pickImagesLauncher.launch("image/*") }) {
            Text("Seleziona immagini")
        }

        imageUris.forEach { uri ->
            Image(
                painter = rememberAsyncImagePainter(uri),
                contentDescription = null,
                modifier = Modifier
                    .height(100.dp)
                    .fillMaxWidth()
            )
        }

        Button(onClick = {
            val diveSite = DiveSite(
                id = UUID.randomUUID().toString(),
                name = name,
                description = description,
                latitude = latitude.toDoubleOrNull() ?: 0.0,
                longitude = longitude.toDoubleOrNull() ?: 0.0,
                minDepth = minDepth.toIntOrNull(),
                maxDepth = maxDepth.toIntOrNull(),
                authorName = authorName,
                imageUrls = imageUris.map { it.toString() }
            )

            scope.launch {
                val result = DiveSiteRepository.addDiveSiteWithImages(
                    context = context,
                    diveSite = diveSite,
                    imageUris = imageUris
                )

                successMessage = if (result.isSuccess) {
                    "Dive site aggiunto con successo!"
                } else {
                    "Errore: ${result.exceptionOrNull()?.message}"
                }
            }
        }) {
            Text("Salva")
        }

        successMessage?.let {
            Text(it, color = MaterialTheme.colorScheme.primary)
        }
    }
}