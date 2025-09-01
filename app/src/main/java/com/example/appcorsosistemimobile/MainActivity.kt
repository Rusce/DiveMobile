package com.example.appcorsosistemimobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.appcorsosistemimobile.ui.theme.AppCorsoSistemiMobileTheme
import androidx.navigation.compose.rememberNavController
import com.example.appcorsosistemimobile.ui.components.BottomBar
import com.example.appcorsosistemimobile.ui.navigation.BottomNavGraph
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appcorsosistemimobile.viewmodel.AuthViewModel
import androidx.compose.runtime.LaunchedEffect



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppCorsoSistemiMobileTheme {
                val authViewModel: AuthViewModel = viewModel()

                LaunchedEffect(Unit) {
                    authViewModel.initSession()
                }

                val navController = rememberNavController()
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = { BottomBar(navController, authViewModel) }
                ) { innerPadding ->
                    androidx.compose.foundation.layout.Box(modifier = Modifier.padding(innerPadding)) {
                        BottomNavGraph(navController = navController, authViewModel = authViewModel)
                    }
                }
            }
        }
    }
}