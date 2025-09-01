package com.example.appcorsosistemimobile.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import com.example.appcorsosistemimobile.utils.*
import androidx.navigation.NavController
import com.example.appcorsosistemimobile.repository.DiveSiteRepository
import com.example.appcorsosistemimobile.ui.components.SitesListOverlay
import com.google.maps.android.compose.MapProperties
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition", "MutableCollectionMutableState")
@Composable
fun MapScreen(navController: NavController) {
    val diveSites = remember { mutableStateListOf<DiveSite>() }
    val ctx = LocalContext.current
    val locationService = remember { LocationService(ctx) }
    val coordinates by locationService.coordinates.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val locationPermissions = rememberMultiplePermissions(
        listOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
    ) { }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultInitialLocation, 10f)
    }
    val votes by remember { mutableStateOf<MutableMap<DiveSite, Double>>(mutableMapOf()) }
    val distances by remember { mutableStateOf<MutableMap<DiveSite, Double>>(mutableMapOf()) }

    var selectedSite by remember { mutableStateOf<DiveSite?>(null) }
    var isInitialLocationSet by remember { mutableStateOf(false) }
    var showList by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = navController.currentBackStackEntry) {
        val result = DiveSiteRepository.getAllDiveSites()
        diveSites.clear()
        diveSites.addAll(result)
    }

    scope.launch {
        getLocationOrRequestPermission(locationPermissions, locationService)
        diveSites.forEach { votes[it] = DiveSiteRepository.getReveiwsAverageForDiveSite(it.id) }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.matchParentSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties( isMyLocationEnabled = locationPermissions.statuses.any { it.value.isGranted } )
        ) {
            diveSites.forEach { site ->
                Marker(
                    state = rememberMarkerState(
                        position = LatLng(site.latitude, site.longitude)
                    ),
                    icon = BitmapDescriptorFactory.fromResource(R.drawable.divesite_icon),
                    onClick = {
                        selectedSite = site
                        showList = false
                        true
                    }
                )
            }

            LaunchedEffect(coordinates) {
                if (coordinates != null && !isInitialLocationSet) {
                    centerUser(cameraPositionState, coordinates)
                    isInitialLocationSet = true
                }
            }
        }

        FloatingActionButton (
            onClick = {
                showList = !showList
                selectedSite = null
            },
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(horizontal = 16.dp, vertical = 32.dp)
                .clip(CircleShape)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.List,
                contentDescription = "Lista luoghi"
            )
        }

        if (showList) {
            SitesListOverlay(
                diveSites = diveSites,
                distances = distances,
                votes = votes,
                cameraPositionState = cameraPositionState,
                onDetailsClick = {
//                    site -> navController.navigate("detail/${site.id}")
                    showList = false
                    updateCameraPositionState(cameraPositionState, LatLng(it.latitude, it.longitude))
                    selectedSite = it
                },
                onClose = { showList = false }
            )
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
