package com.example.appcorsosistemimobile.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.example.appcorsosistemimobile.data.model.DiveSite
import com.example.appcorsosistemimobile.ui.components.MapInfoOverlay
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.example.appcorsosistemimobile.R

@Composable
fun MapScreen(
    onDetailsClick: (DiveSite) -> Unit // callback per navigare al dettaglio
) {
    val fakeDiveSites = listOf(
        DiveSite(
            id = "1",
            name = "Scogliera Romagnola",
            description = "Fondale roccioso.",
            latitude = 44.1500,
            longitude = 12.2500,
            imageUrls = listOf("https://picsum.photos/200/100?random=1")
        ),
        DiveSite(
            id = "2",
            name = "Relitto Cesena",
            description = "Vecchio relitto sommerso.",
            latitude = 44.1480,
            longitude = 12.2355,
            imageUrls = listOf("https://picsum.photos/200/100?random=2")
        )
    )

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(44.1480, 12.2355), 14f)
    }

    var selectedSite by remember { mutableStateOf<DiveSite?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.matchParentSize(),
            cameraPositionState = cameraPositionState
        ) {
            fakeDiveSites.forEach { site ->
                Marker(
                    state = rememberMarkerState(
                        position = LatLng(site.latitude, site.longitude)
                    ),
                    icon = BitmapDescriptorFactory.fromResource(R.drawable.divesite_icon),
                    onClick = {
                        selectedSite = site
                        true // blocca infoWindow predefinita
                    }
                )
            }
        }

        // Scheda mostrata in basso sopra la navbar
        selectedSite?.let {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(start = 16.dp, end = 16.dp, bottom = 100.dp) // evita la navbar
            ) {
                MapInfoOverlay(
                    site = it,
                    onDetailsClick = onDetailsClick,
                    onClose = { selectedSite = null }
                )
            }
        }
    }
}
