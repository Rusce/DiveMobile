package com.example.appcorsosistemimobile.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.appcorsosistemimobile.R
import com.example.appcorsosistemimobile.data.model.DiveSite
import com.example.appcorsosistemimobile.data.model.DiveSiteComment
import com.example.appcorsosistemimobile.repository.DiveSiteRepository
import com.example.appcorsosistemimobile.viewmodel.AuthViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiveSiteDetailScreen(
    diveSiteId: String,
    onBackClick: () -> Unit,
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()
    val currentUserEmail by authViewModel.currentUserEmail.collectAsState()

    var site by remember { mutableStateOf<DiveSite?>(null) }
    var loading by remember { mutableStateOf(true) }
    var toggleLoading by remember { mutableStateOf(false) }
    var userReview by remember { mutableStateOf<DiveSiteComment?>(null) }

    val isFavorite = currentUser?.favouriteDiveSite?.contains(diveSiteId) == true

    LaunchedEffect(diveSiteId) {
        site = DiveSiteRepository.getDiveSiteById(diveSiteId)
        loading = false
        if(site != null) {
            userReview = DiveSiteRepository.getUserReviewForDiveSite(
                site!!.id,
                currentUser?.name + " " + currentUser?.surname
            )
        }
    }

    LaunchedEffect(key1 = currentUserEmail) {
        if (currentUserEmail != null && currentUser == null) {
            authViewModel.loadUserProfile(currentUserEmail!!)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(site?.name ?: "Dettagli") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Indietro")
                    }
                },
                actions = {
                    if(site != null && site!!.authorName == currentUser?.name + " " + currentUser?.surname) {
                        Button(
                            onClick = { navController.navigate("edit/${Uri.encode(site!!.id)}") }
                        ) {
                            Text("Modifica sito")
                        }
                    }

                    IconButton(
                        onClick = {
                            if (!isLoggedIn) {
                                Toast.makeText(context, "Devi essere loggato per aggiungere ai preferiti", Toast.LENGTH_SHORT).show()
                            } else if (!toggleLoading) {
                                toggleLoading = true
                                coroutineScope.launch {
                                    val result = DiveSiteRepository.toggleFavoriteDiveSite(
                                        userEmail = currentUserEmail ?: return@launch,
                                        diveSiteId = diveSiteId,
                                        isFavorite = isFavorite
                                    )
                                    if (result.isSuccess) {
                                        authViewModel.loadUserProfile(currentUserEmail!!)
                                    }
                                    toggleLoading = false
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = if (isFavorite) "Rimuovi dai preferiti" else "Aggiungi ai preferiti",
                            tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->

        if (loading || site == null) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        val formattedDate = remember(site!!.createdAt) {
            SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault()).format(Date(site!!.createdAt))
        }

        val coordinates = "${site!!.latitude} ${site!!.longitude}"

        Column(
            modifier = Modifier
                .padding(top = paddingValues.calculateTopPadding())
                .padding(16.dp)
                .verticalScroll(scrollState)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = site!!.description, style = MaterialTheme.typography.bodyLarge)

            if (site!!.minDepth != null || site!!.maxDepth != null) {
                Text(
                    text = "Profondità: ${site!!.minDepth ?: "?"}m - ${site!!.maxDepth ?: "?"}m",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (site!!.authorName.isNotBlank()) {
                Text(text = "Autore: ${site!!.authorName}", style = MaterialTheme.typography.bodyMedium)
            }

            Text(text = "Creato il: $formattedDate", style = MaterialTheme.typography.bodyMedium)

            Text(
                text = "Coordinate: $coordinates (clicca per copiare)",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.clickable {
                    clipboardManager.setText(AnnotatedString(coordinates))
                    Toast.makeText(context, "Coordinate copiate negli appunti", Toast.LENGTH_SHORT).show()
                }
            )

            if (site!!.imageUrls.isNotEmpty()) {
                Text(text = "Immagini", style = MaterialTheme.typography.titleMedium)

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(site!!.imageUrls) { url ->
                        AsyncImage(
                            model = url,
                            contentDescription = "Immagine del sito",
                            modifier = Modifier
                                .height(120.dp)
                                .width(200.dp),
                            contentScale = ContentScale.Crop,
                            placeholder = painterResource(id = R.drawable.placeholder),
                            error = painterResource(id = R.drawable.image_error)
                        )
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val encodedId = Uri.encode(site!!.id)

                Button(
                    onClick = { navController.navigate("comments/$encodedId") },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Visualizza commenti")
                }

                Button(
                    onClick = {
                        if (!isLoggedIn) {
                            Toast.makeText(context, "Devi essere loggato per aggiungere un commento", Toast.LENGTH_SHORT).show()
                        } else {
                            navController.navigate("${if(userReview != null) "edit" else "add"}_comment/$encodedId")
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("${if(userReview != null) "Modifica" else "Aggiungi"} commento")
                }
            }

            Text(text = "Posizione", style = MaterialTheme.typography.titleMedium)

            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(
                    LatLng(site!!.latitude, site!!.longitude), 15f
                )
            }

            GoogleMap(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                cameraPositionState = cameraPositionState
            ) {
                Marker(
                    state = rememberMarkerState(position = LatLng(site!!.latitude, site!!.longitude)),
                    title = site!!.name
                )
            }
        }
    }
}
