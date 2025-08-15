package com.example.appcorsosistemimobile.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.navigation.NavController
import com.example.appcorsosistemimobile.data.model.DiveSite
import com.example.appcorsosistemimobile.repository.DiveSiteRepository
import com.example.appcorsosistemimobile.ui.components.InfoOverlay
import com.example.appcorsosistemimobile.viewmodel.AuthViewModel
import kotlin.math.absoluteValue

//TODO gamification (achievement per commenti, siti aggiunt ecc.)
//TODO visualizzazione preferiti (riciclando overlay mappa)
//TODO foto profilo (facoltativo mettere anche l'immagine nella navbar inferiore)

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ProfileScreen(
    navController: NavController,
    onNavigateToRegister: () -> Unit,
    authViewModel: AuthViewModel
) {
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()
    val currentUserEmail by authViewModel.currentUserEmail.collectAsState()
    val favourites = remember { mutableStateListOf<DiveSite>() }

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            currentUserEmail?.let { authViewModel.loadUserProfile(it) }
            val result = DiveSiteRepository.getAllDiveSites()
            favourites.clear()
            result.forEach {
                if(currentUser?.favouriteDiveSite?.contains(it.id) == true) {
                    favourites.add(it)
                }
            }
        }
    }

    if (isLoggedIn) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (currentUser != null) {
                    Text("Ciao, ${currentUser!!.name} ${currentUser!!.surname}!", style = MaterialTheme.typography.headlineSmall)
                    Text("Email: ${currentUser!!.email}", style = MaterialTheme.typography.bodyMedium)
                    Text("Immersioni preferite:", style = MaterialTheme.typography.titleMedium)

                    if (currentUser!!.favouriteDiveSite.isEmpty()) {
                        Text("Nessuna immersione preferita.")
                    } else {
                        val pagerState = rememberPagerState(pageCount = { favourites.size })

                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp)
                        ) { page ->
                            PagerCard(
                                pagerItem = favourites[page],
                                modifier = Modifier
                                    .fillMaxSize()
                                    .graphicsLayer {
                                        val pageOffset = (
                                                (pagerState.currentPage - page) + pagerState
                                                    .currentPageOffsetFraction
                                                ).absoluteValue

                                        lerp(
                                            start = 0.85f,
                                            stop = 1f,
                                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                                        ).also { scale ->
                                            scaleX = scale
                                            scaleY = scale
                                        }
                                    },
                                navController = navController
                            )
                        }

                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            favourites.forEachIndexed { index, _ ->
                                val isSelected = index == pagerState.currentPage
                                Box(
                                    modifier = Modifier
                                        .size(if (isSelected) 12.dp else 11.dp)
                                        .padding(horizontal = 4.dp)
                                        .background(
                                            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray,
                                            shape = CircleShape
                                        )
                                )
                            }
                        }
                    }
                } else {
                    CircularProgressIndicator()
                }

                Spacer(Modifier.height(24.dp))

                Button(onClick = { authViewModel.logout() }) {
                    Text("Logout")
                }
            }
        }
    } else {
        LoginScreen(
            onLoginSuccess = { /* recomposition automatica */ },
            onNavigateToRegister = onNavigateToRegister,
            authViewModel = authViewModel
        )
    }
}

@Composable
fun PagerCard(pagerItem: DiveSite, modifier: Modifier = Modifier, navController: NavController) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
    ) {
        InfoOverlay(
            site = pagerItem,
            onDetailsClick = { site ->
                navController.navigate("detail/${site.id}")
            }
        )
    }
}
