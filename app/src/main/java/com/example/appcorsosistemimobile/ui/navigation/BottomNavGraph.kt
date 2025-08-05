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
            AddDiveScreen()
        }

        composable("profile") {
            ProfileScreen()
        }

        composable(
            route = "detail/{siteJson}",
            arguments = listOf(navArgument("siteJson") { type = NavType.StringType })
        ) { backStackEntry ->
            val json = backStackEntry.arguments?.getString("siteJson")
            val decodedJson = URLDecoder.decode(json, StandardCharsets.UTF_8.toString())
            val site = Gson().fromJson(decodedJson, DiveSite::class.java)

            DiveSiteDetailScreen(
                site = site,
                navController = navController,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = "comments/{diveSiteId}",
            arguments = listOf(navArgument("diveSiteId") { type = NavType.StringType })
        ) { backStackEntry ->
            val diveSiteId = backStackEntry.arguments?.getString("diveSiteId") ?: ""
            DiveSiteCommentsScreen(
                diveSiteId = diveSiteId,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = "add_comment/{diveSiteId}",
            arguments = listOf(navArgument("diveSiteId") { type = NavType.StringType })
        ) { backStackEntry ->
            val diveSiteId = backStackEntry.arguments?.getString("diveSiteId") ?: ""
            AddCommentScreen(
                diveSiteId = diveSiteId,
                onBackClick = { navController.popBackStack() }
            )
        }

    }
}
