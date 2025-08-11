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
import com.example.appcorsosistemimobile.repository.DiveSiteRepository

//TODO barra ricerca luoghi (api google maps)
//TODO filtri (profondità)
//TODO posizione utente
//TODO visualizzazione lista (in base alla distanza e filtri extra)

@Composable
fun MapScreen(navController: NavController) {
    val diveSites = remember { mutableStateListOf<DiveSite>() }

    LaunchedEffect(key1 = navController.currentBackStackEntry) {
        val result = DiveSiteRepository.getAllDiveSites()
        diveSites.clear()
        diveSites.addAll(result)
    }



    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(44.1480, 12.2355), 14f)
    }

    var selectedSite by remember { mutableStateOf<DiveSite?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.matchParentSize(),
            cameraPositionState = cameraPositionState
        ) {
            diveSites.forEach { site ->
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
                        navController.navigate("detail/${site.id}")
                    },
                    onClose = { selectedSite = null }
                )
            }
        }
    }
}
