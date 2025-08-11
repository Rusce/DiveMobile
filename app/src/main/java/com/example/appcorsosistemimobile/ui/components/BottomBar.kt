package com.example.appcorsosistemimobile.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.appcorsosistemimobile.viewmodel.AuthViewModel
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.collectAsState

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    data object Map : BottomNavItem("map", Icons.Default.Place, "Mappa")
    data object Add : BottomNavItem("add", Icons.Default.AddCircle, "")
    data object Profile : BottomNavItem("profile", Icons.Default.Person, "Profilo")
}

@Composable
fun BottomBar(navController: NavController, authViewModel: AuthViewModel) {
    val items = listOf(
        BottomNavItem.Map,
        BottomNavItem.Add,
        BottomNavItem.Profile
    )

    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
    val context = LocalContext.current
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    if (item is BottomNavItem.Add && !isLoggedIn) {
                        Toast.makeText(context, "Devi essere loggato per aggiungere un sito di immersione", Toast.LENGTH_SHORT).show()
                        return@NavigationBarItem
                    }

                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = { Icon(imageVector = item.icon, contentDescription = item.label) },
                label = { if (item.label.isNotBlank()) Text(item.label) }
            )
        }
    }
}
