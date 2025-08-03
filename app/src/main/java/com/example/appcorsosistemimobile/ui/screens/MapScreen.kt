package com.example.appcorsosistemimobile.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng

@Composable
fun MapScreen() {
    val startPosition = LatLng(44.1480616125233, 12.23551531160112) // Campus cesena

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(startPosition, 5f)
    }

    GoogleMap(
        modifier = Modifier,
        cameraPositionState = cameraPositionState
    ) {
        Marker(
            state = rememberMarkerState(position = startPosition),
            title = "Campus Cesena",
            snippet = "Punto di immersione fittizio"
        )
    }
}
