package com.example.appcorsosistemimobile.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.appcorsosistemimobile.ui.screens.*

@Composable
fun BottomNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "map") {
        composable("map") { MapScreen() }
        composable("add") { AddDiveScreen() }
        composable("profile") { ProfileScreen() }
    }
}