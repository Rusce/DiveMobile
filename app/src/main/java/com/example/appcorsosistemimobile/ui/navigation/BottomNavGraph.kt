package com.example.appcorsosistemimobile.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.appcorsosistemimobile.ui.screens.*
import com.example.appcorsosistemimobile.data.model.DiveSite
import com.google.gson.Gson
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import androidx.navigation.NavType
import androidx.navigation.navArgument

@Composable
fun BottomNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "map") {

        composable("map") {
            MapScreen(navController = navController)
        }

        composable("add") {
            AddDiveScreen(navController = navController)
        }

        composable("profile") {
            ProfileScreen(onNavigateToRegister = { navController.navigate("register") })
        }

        composable(
            route = "detail/{diveSiteId}",
            arguments = listOf(navArgument("diveSiteId") { type = NavType.StringType })
        ) { backStackEntry ->
            val diveSiteId = backStackEntry.arguments?.getString("diveSiteId") ?: ""
            DiveSiteDetailScreen(
                diveSiteId = diveSiteId,
                onBackClick = { navController.popBackStack() },
                navController = navController
            )
        }

        composable(
            route = "comments/{diveSiteId}",
            arguments = listOf(navArgument("diveSiteId") { type = NavType.StringType })
        ) { backStackEntry ->
            val encoded = backStackEntry.arguments?.getString("diveSiteId") ?: ""
            val diveSiteId = URLDecoder.decode(encoded, StandardCharsets.UTF_8.name())
            DiveSiteCommentsScreen(
                diveSiteId = diveSiteId,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = "add_comment/{diveSiteId}",
            arguments = listOf(navArgument("diveSiteId") { type = NavType.StringType })
        ) { backStackEntry ->
            val encoded = backStackEntry.arguments?.getString("diveSiteId") ?: ""
            val diveSiteId = URLDecoder.decode(encoded, StandardCharsets.UTF_8.name())
            AddCommentScreen(
                diveSiteId = diveSiteId,
                onBackClick = { navController.popBackStack() },
                navController = navController
            )
        }


        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("profile") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate("register") }
            )
        }

        composable("register") {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    }
                },
                onNavigateToLogin = { navController.navigate("login") }
            )
        }



    }
}
