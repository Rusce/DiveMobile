package com.example.appcorsosistemimobile.utils

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.provider.Settings
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices.getFusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.maps.android.compose.CameraPositionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.math.abs

data class Coordinates(val latitude: Double, val longitude: Double)

class LocationService(private val ctx: Context) {
    private val fusedLocationClient = getFusedLocationProviderClient(ctx)
    private val locationManager = ctx.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    private val _coordinates = MutableStateFlow<Coordinates?>(null)
    val coordinates = _coordinates.asStateFlow()
    private val _isLoadingLocation = MutableStateFlow(false)
    val isLoadingLocation = _isLoadingLocation.asStateFlow()

    suspend fun getCurrentLocation(): Coordinates? {
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            throw IllegalStateException("Location is disabled")
        }

        if ( ContextCompat.checkSelfPermission(
                ctx,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED ) {
            throw SecurityException("Location permission not granted")
        }

        _isLoadingLocation.value = true
        val location = withContext(Dispatchers.IO) {
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                CancellationTokenSource().token
            ).await()
        }
        _isLoadingLocation.value = false

        _coordinates.value =
            if (location != null) Coordinates(location.latitude, location.longitude)
            else null

        return coordinates.value
    }

    fun openLocationSettings() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        if (intent.resolveActivity(ctx.packageManager) != null) {
            ctx.startActivity(intent)
        }
    }
}

val defaultInitialLocation = LatLng(44.1480, 12.2355)

suspend fun getLocationOrRequestPermission(locationPermissions: MultiplePermissionHandler, locationService: LocationService) {
    if (!locationPermissions.statuses.any { it.value.isGranted }) {
        locationPermissions.launchPermissionRequest()
    }
    if (locationPermissions.statuses.any { it.value.isGranted }) {
        try {
            locationService.getCurrentLocation()
        } catch (_: IllegalStateException) { }
    }
}

fun isApproximatelyEqual(latLng1: LatLng, latLng2: LatLng, tolerance: Double = 0.000001 ): Boolean {
    return abs(latLng1.latitude - latLng2.latitude) < tolerance && abs(latLng1.longitude - latLng2.longitude) < tolerance
}

fun updateCameraPositionState(cameraPositionState: CameraPositionState, coordinates: Coordinates?) {
    if(coordinates != null && isApproximatelyEqual(cameraPositionState.position.target, defaultInitialLocation)) {
        cameraPositionState.position = CameraPosition.fromLatLngZoom(LatLng(coordinates.latitude, coordinates.longitude), cameraPositionState.position.zoom)
    }
}