package com.example.appcorsosistemimobile.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.appcorsosistemimobile.data.model.DiveSiteComment
import com.example.appcorsosistemimobile.repository.DiveSiteRepository
import com.example.appcorsosistemimobile.ui.components.InfoOverlay
import com.example.appcorsosistemimobile.viewmodel.AuthViewModel
import kotlin.math.absoluteValue

//TODO foto profilo (facoltativo mettere anche l'immagine nella navbar inferiore)

@SuppressLint("CoroutineCreationDuringComposition", "MutableCollectionMutableState")
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
    var diveSitesNumber by remember { mutableIntStateOf(0) }
    var commentsNumber by remember { mutableIntStateOf(0) }

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            currentUserEmail?.let { authViewModel.loadUserProfile(it) }
            favourites.clear()
            diveSitesNumber = 0
            commentsNumber = 0
            val diveSites = DiveSiteRepository.getAllDiveSites()
            val comments = mutableMapOf<String, List<DiveSiteComment>>()

            diveSites.forEach {
                if(currentUser?.favouriteDiveSite?.contains(it.id) == true) {
                    favourites.add(it)
                }
                comments[it.id] = DiveSiteRepository.getCommentsForDiveSite(it.id)
                if (it.authorName == "${currentUser?.name} ${currentUser?.surname}") {
                    diveSitesNumber++
                }
            }

            comments.forEach { (_, list) ->
                list.forEach{ comment ->
                    if (comment.authorName == "${currentUser?.name} ${currentUser?.surname}")
                        commentsNumber++
                }
            }
        }
    }

    if (isLoggedIn) {
        val scrollState = rememberScrollState()

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (currentUser != null) {
                    Text("Ciao, ${currentUser!!.name} ${currentUser!!.surname}!", style = MaterialTheme.typography.headlineSmall)
                    Text("Email: ${currentUser!!.email}", style = MaterialTheme.typography.bodyMedium)

                    Text("Immersioni preferite:", style = MaterialTheme.typography.titleMedium)
                    if (currentUser!!.favouriteDiveSite.isNotEmpty()) {
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
                    } else {
                        Text("Nessuna immersione preferita.")
                    }

                    // gamification: num siti inseriti, num commenti
                    Text("Il tuo contributo:", style = MaterialTheme.typography.titleMedium)
                    Row {
                        CircularIndicator("Siti:", diveSitesNumber)
                        Spacer(Modifier.width(32.dp))
                        CircularIndicator("Commenti:", commentsNumber)
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

@Composable
fun CircularIndicator(text: String, progress: Int) {
    val divider = when {
        progress < 5 -> 5
        progress < 20 -> 20
        else -> 50
    }
    val level = when {
        progress >= 50 -> "oro🥇"
        progress >= 20 -> "argento🥈"
        progress >= 5 -> "bronzo🥉"
        else -> ""
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = text, style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(8.dp))
        Box(
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                progress = {
                    if(progress < 50) (progress).toFloat() / ((progress / divider + 1) * divider)
                    else 1f
                },
                modifier = Modifier.size(100.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = Color.Gray,
                strokeWidth = 8.dp,
                strokeCap = ProgressIndicatorDefaults.CircularDeterminateStrokeCap,
            )
            Text(
                text = when {
                    progress < 50 -> "$progress / ${(progress / divider + 1) * divider}"
                    progress == 50 -> "$progress / $divider"
                    else -> "$progress"
                },
                style = MaterialTheme.typography.headlineSmall,
            )
        }
        Spacer(Modifier.height(8.dp))
        if(progress >= 5) Text(text = "Livello $level", style = MaterialTheme.typography.bodyMedium)
    }
}
