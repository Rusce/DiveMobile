package com.example.appcorsosistemimobile.ui.screens

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import com.example.appcorsosistemimobile.R
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.appcorsosistemimobile.data.model.DiveSite
import com.example.appcorsosistemimobile.repository.DiveSiteRepository
import com.example.appcorsosistemimobile.viewmodel.AuthViewModel
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.currentCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.descriptors.PrimitiveKind
import java.util.*

//TODO rotellina per caricamento nuovo divesite (pulsanti background disattivati e reset dei campi)

@Composable
fun AddDiveScreen(
    navController: NavController,
    authViewModel: AuthViewModel
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
        Toast.makeText(context, "Devi essere loggato per aggiungere un sito", Toast.LENGTH_SHORT).show()
        navController.popBackStack()
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

    val stateHandle = navController.currentBackStackEntry?.savedStateHandle

    var name by remember { mutableStateOf(stateHandle?.get("name") ?: "") }
    var isNameOk by remember { mutableStateOf(true) }
    var description by remember { mutableStateOf(stateHandle?.get("description") ?: "") }
    var isDescriptionOk by remember { mutableStateOf(true) }
    var latitude by remember { mutableStateOf(stateHandle?.get("latitude") ?: "") }
    var isLatitudeOk by remember { mutableStateOf(true) }
    var longitude by remember { mutableStateOf(stateHandle?.get("longitude") ?: "") }
    var isLongitudeOk by remember { mutableStateOf(true) }
    var minDepth by remember { mutableStateOf(stateHandle?.get("min_depth") ?: "") }
    var isMinDepthOk by remember { mutableStateOf(true) }
    var maxDepth by remember { mutableStateOf(stateHandle?.get("max_depth") ?: "") }
    var isMaxDepthOk by remember { mutableStateOf(true) }
    var imageUris by remember { mutableStateOf<List<Uri>>(stateHandle?.get("img_list") ?: emptyList()) }
    var isImageOk by remember { mutableStateOf(true) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    val pickImagesLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        imageUris = uris
        stateHandle?.set("img_list", imageUris)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Aggiungi Luogo di Immersione", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it
                stateHandle?.set("name", name)
            },
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
            value = description,
            onValueChange = {
                description = it
                stateHandle?.set("description", description)
            },
            label = { Text("Descrizione") },
            modifier = Modifier.fillMaxWidth(),
            supportingText = {
                if (!isDescriptionOk) {
                    Text(
                        text = "Questo campo è obbligatorio.",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        )

        OutlinedTextField(
            value = minDepth,
            onValueChange = {
                minDepth = it
                stateHandle?.set("min_depth", minDepth)
            },
            label = { Text("Profondità Min") },
            modifier = Modifier.fillMaxWidth(),
            supportingText = {
                if (!isMinDepthOk) {
                    Text(
                        text = "Questo campo è obbligatorio.",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        )

        OutlinedTextField(
            value = maxDepth,
            onValueChange = {
                maxDepth = it
                stateHandle?.set("min_depth", maxDepth)
            },
            label = { Text("Profondità Max") },
            modifier = Modifier.fillMaxWidth(),
            supportingText = {
                if (!isMaxDepthOk) {
                    Text(
                        text = "Questo campo è obbligatorio.",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        )

        OutlinedTextField(
            value = latitude.toString(),
            onValueChange = {
                latitude = it
                stateHandle?.set("latitude", latitude)
            },
            label = { Text("Latitudine") },
            modifier = Modifier.fillMaxWidth(),
            supportingText = {
                if (!isLatitudeOk) {
                    Text(
                        text = "Inserire una coordinata.",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        )

        OutlinedTextField(
            value = longitude.toString(),
            onValueChange = {
                longitude = it
                stateHandle?.set("longitude", longitude)
            },
            label = { Text("Longitudine") },
            modifier = Modifier.fillMaxWidth(),
            supportingText = {
                if (!isLongitudeOk) {
                    Text(
                        text = "Inserire una coordinata.",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        )

        Button(onClick = {
            navController.navigate("select_coordinates")
        }) {
            Text("Scegli le coordinate dalla Mappa")
        }

        Button(onClick = { pickImagesLauncher.launch("image/*") }) {
            Text("Seleziona immagini")
        }

        if(!isImageOk) {
            Text(
                text = "Inserire un'immagine.",
                color = MaterialTheme.colorScheme.error
            )
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
            isNameOk = name.isNotBlank()
            isDescriptionOk = description.isNotBlank()
            isLatitudeOk = latitude.toString().isNotBlank() && latitude.toString().toDoubleOrNull() != null
            isLongitudeOk = longitude.toString().isNotBlank() && longitude.toString().toDoubleOrNull() != null
            isMinDepthOk = minDepth.isNotBlank()
            isMaxDepthOk = maxDepth.isNotBlank()
            isImageOk = imageUris.isNotEmpty()

            if(isNameOk && isDescriptionOk && isLatitudeOk && isLongitudeOk && isMinDepthOk && isMaxDepthOk && isImageOk) {
                val diveSite = DiveSite(
                    id = UUID.randomUUID().toString(),
                    name = name,
                    description = description,
                    latitude = latitude.toString().toDouble(),
                    longitude = longitude.toString().toDouble(),
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
            }
        }) {
            Text("Salva")
        }

        successMessage?.let {
            Text(it, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectCoordinates(navController: NavController) {
    val stateHandle = navController.previousBackStackEntry?.savedStateHandle
    val latitude = stateHandle?.get<String>("latitude")?.toDoubleOrNull()
    val longitude = stateHandle?.get<String>("longitude")?.toDoubleOrNull()
    val initialPosition = if (latitude != null && longitude != null) {
        LatLng(latitude, longitude)
    } else {
        null
    }
    var markerPosition by remember { mutableStateOf<LatLng?>(initialPosition) }

    val diveSites = remember { mutableStateListOf<DiveSite>() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(key1 = navController.currentBackStackEntry) {
        val result = DiveSiteRepository.getAllDiveSites()
        diveSites.clear()
        diveSites.addAll(result)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Seleziona una posizione")
                    }
                }

            )
        }
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = currentCameraPositionState,
            onMapClick = { latLng ->
                coroutineScope.launch {
                    markerPosition = latLng
                    delay(500)

                    withContext(Dispatchers.Main) {
                        navController.previousBackStackEntry?.savedStateHandle?.set("latitude", latLng.latitude.toString())
                        navController.previousBackStackEntry?.savedStateHandle?.set("longitude", latLng.longitude.toString())
                        navController.navigateUp()
                    }
                }
            }
        ) {
            diveSites.forEach { site ->
                Marker(
                    state = rememberMarkerState(
                        position = LatLng(site.latitude, site.longitude)
                    ),
                    icon = BitmapDescriptorFactory.fromResource(R.drawable.divesite_icon),
                )
            }

            markerPosition?.let { position ->
                key(position) {
                    Marker(
                        state = rememberMarkerState(position = position),
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
                    )
                }
            }
        }
    }
}

private fun onCoordinatesSelected(latLng: LatLng) {
    Log.d(null, latLng.toString())
    try {
        Thread.sleep(500)
    } catch (e: InterruptedException) {
        Thread.currentThread().interrupt()
    }
}