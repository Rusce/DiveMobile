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
import androidx.navigation.NavController
import com.google.gson.Gson
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun MapScreen(navController: NavController) {
    val fakeDiveSites = listOf(
        DiveSite(
            id = "1",
            name = "Scogliera Romagnola",
            description = "Fondale roccioso.",
            latitude = 44.1500,
            longitude = 12.2500,
            minDepth = 12,
            maxDepth = 85,
            authorId = "idsub2",
            imageUrls = listOf("https://picsum.photos/200/100?random=1")
        ),
        DiveSite(
            id = "2",
            name = "Relitto Cesena",
            description = "Vecchio relitto sommerso, peschereccio affondato nel 1993 a seguito di una collisione con gli scogli.",
            latitude = 44.1480,
            longitude = 12.2355,
            minDepth = 15,
            maxDepth = 45,
            authorId = "idsub1",
            imageUrls = listOf(
                "https://picsum.photos/200/100?random=2",
                "https://picsum.photos/200/100?random=3",
                "https://picsum.photos/200/100?random=4"
            )
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
                        true
                    }
                )
            }
        }

        selectedSite?.let {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(start = 16.dp, end = 16.dp, bottom = 100.dp)
            ) {
                MapInfoOverlay(
                    site = it,
                    onDetailsClick = { site ->
                        val siteJson = URLEncoder.encode(
                            Gson().toJson(site),
                            StandardCharsets.UTF_8.toString()
                        )
                        navController.navigate("detail/$siteJson")
                    },
                    onClose = { selectedSite = null }
                )
            }
        }
    }
}
