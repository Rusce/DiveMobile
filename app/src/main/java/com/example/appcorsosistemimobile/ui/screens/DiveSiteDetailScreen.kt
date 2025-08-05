package com.example.appcorsosistemimobile.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.appcorsosistemimobile.data.model.DiveSite
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.foundation.clickable
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import android.net.Uri
import androidx.navigation.NavController


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiveSiteDetailScreen(
    site: DiveSite,
    onBackClick: () -> Unit,
    navController: NavController
) {
    val dateFormat = remember {
        SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault())
    }
    val formattedDate = remember(site.createdAt) {
        dateFormat.format(Date(site.createdAt))
    }

    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    val coordinates = "${site.latitude} ${site.longitude}"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(site.name) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Indietro")
                    }
                }
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .padding(top = paddingValues.calculateTopPadding())
                .padding(16.dp)
                .verticalScroll(scrollState)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = site.description, style = MaterialTheme.typography.bodyLarge)

            if (site.minDepth != null || site.maxDepth != null) {
                Text(
                    text = "Profondità: " +
                            (site.minDepth?.toString() ?: "?") + "m - " +
                            (site.maxDepth?.toString() ?: "?") + "m",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (site.authorId.isNotBlank()) {
                Text(text = "Autore: ${site.authorId}", style = MaterialTheme.typography.bodyMedium)
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

            if (site.imageUrls.isNotEmpty()) {
                Text(text = "Immagini", style = MaterialTheme.typography.titleMedium)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(site.imageUrls) { url ->
                        Image(
                            painter = rememberAsyncImagePainter(
                                ImageRequest.Builder(LocalContext.current)
                                    .data(url)
                                    .crossfade(true)
                                    .build()
                            ),
                            contentDescription = "Immagine del sito",
                            modifier = Modifier
                                .height(120.dp)
                                .width(200.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val encodedId = Uri.encode(site.id) // se contiene caratteri speciali

                Button(
                    onClick = {
                        navController.navigate("comments/$encodedId")
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Visualizza commenti")
                }

                Button(
                    onClick = {
                        navController.navigate("add_comment/$encodedId")
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Aggiungi commento")
                }
            }

            Text(text = "Posizione", style = MaterialTheme.typography.titleMedium)

            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(
                    LatLng(site.latitude, site.longitude), 15f
                )
            }

            GoogleMap(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                cameraPositionState = cameraPositionState
            ) {
                Marker(
                    state = rememberMarkerState(position = LatLng(site.latitude, site.longitude)),
                    title = site.name
                )
            }
        }
    }
}
