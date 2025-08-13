package com.example.appcorsosistemimobile.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
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
import com.google.maps.android.SphericalUtil
import com.example.appcorsosistemimobile.data.model.DiveSite
import com.example.appcorsosistemimobile.ui.components.MapInfoOverlay
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.example.appcorsosistemimobile.R
import com.example.appcorsosistemimobile.utils.*
import androidx.navigation.NavController
import com.example.appcorsosistemimobile.repository.DiveSiteRepository
import com.google.maps.android.compose.MapProperties
import kotlinx.coroutines.launch


//TODO filtri (profondità)
//TODO visualizzazione lista (in base alla distanza e filtri extra)
//TODO quando premo mappa dalla schermata dei dettagli dovrebbe uscire dai dettagli

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
    var filterMenuExpanded by remember { mutableStateOf(false) }
    var sorting by remember { mutableStateOf("nothing") }

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
                    updateCameraPositionState(cameraPositionState, coordinates)
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(top = 56.dp, start = 16.dp, end = 16.dp, bottom = 100.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row (
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button (
                            onClick = { filterMenuExpanded = !filterMenuExpanded },
                            modifier = Modifier.clip(CircleShape),
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Sort,
                                contentDescription = "Ordina risultati",
                                modifier = Modifier.padding(end = 2.dp)
                            )
                            Text("Ordina per")
                        }

                        DropdownMenu(
                            expanded = filterMenuExpanded,
                            onDismissRequest = { filterMenuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Distanza") },
                                onClick = {
                                    sorting = "distance"
                                    filterMenuExpanded = false
                                }
                            )

                            DropdownMenuItem(
                                text = { Text("Profondità") },
                                onClick = {
                                    sorting = "depth"
                                    filterMenuExpanded = false
                                }
                            )

                            DropdownMenuItem(
                                text = { Text("Recensioni") },
                                onClick = {
                                    sorting = "reviews"
                                    filterMenuExpanded = false
                                }
                            )

                            DropdownMenuItem(
                                text = { Text("Nessuno") },
                                onClick = {
                                    sorting = "nothing"
                                    filterMenuExpanded = false
                                }
                            )
                        }

                        IconButton(
                            onClick = { showList = false },
                        ) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Chiudi")
                        }
                    }

                    Box {
                        LazyColumn(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            val sortedSites = when(sorting) {
                                "distance" -> {
                                    diveSites.forEach{ distances[it] = SphericalUtil.computeDistanceBetween(
                                        cameraPositionState.position.target,
                                        LatLng(it.latitude, it.longitude)) }
                                    distances.entries.sortedBy{ it.value }.map{ it.key }
                                }
                                "depth" -> { diveSites.sortedByDescending { it.maxDepth } }
                                "reviews" -> { votes.entries.sortedByDescending{ it.value }.map{ it.key } }
                                else -> { diveSites.toList() }
                            }
                            sortedSites.forEach { Log.d("SORTED SITES", it.toString()) }

                            items(sortedSites) { site ->
                                MapInfoOverlay(
                                    site = site,
                                    onDetailsClick = { clickedSite ->
                                        navController.navigate("detail/${clickedSite.id}")
                                    },
                                    onClose = { }
                                )
                            }
                        }
                    }
                }
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
