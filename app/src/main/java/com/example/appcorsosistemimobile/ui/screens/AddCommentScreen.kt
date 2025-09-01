package com.example.appcorsosistemimobile.ui.screens

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.appcorsosistemimobile.data.model.DiveSiteComment
import com.example.appcorsosistemimobile.repository.DiveSiteRepository
import com.example.appcorsosistemimobile.viewmodel.AuthViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun AddCommentScreen(
    navController: NavController,
    diveSiteId: String,
    onBackClick: () -> Unit,
    authViewModel: AuthViewModel
) {
    CommentScreen(navController,  diveSiteId, onBackClick, authViewModel)
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun EditCommentScreen(
    navController: NavController,
    diveSiteId: String,
    onBackClick: () -> Unit,
    authViewModel: AuthViewModel
) {
    val scope = rememberCoroutineScope()
    val currentUser by authViewModel.currentUser.collectAsState()

    var comment by remember { mutableStateOf<DiveSiteComment?>(null) }

    scope.launch {
        comment = DiveSiteRepository.getUserReviewForDiveSite(
            diveSiteId,
            currentUser?.name + " " + currentUser?.surname
        )
    }
    CommentScreen(navController, diveSiteId, onBackClick, authViewModel, comment)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentScreen(
    navController: NavController,
    diveSiteId: String,
    onBackClick: () -> Unit,
    authViewModel: AuthViewModel,
    diveSiteComment: DiveSiteComment? = null
) {
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()
    val currentUserEmail by authViewModel.currentUserEmail.collectAsState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(isLoggedIn, currentUser, currentUserEmail, diveSiteComment) {
        if (isLoggedIn && currentUser == null && currentUserEmail != null) {
            authViewModel.loadUserProfile(currentUserEmail!!)
        }
    }

    if (!isLoggedIn) {
        Toast.makeText(LocalContext.current, "Devi essere loggato per aggiungere un commento", Toast.LENGTH_SHORT).show()
        onBackClick()
        return
    }

    if (currentUser == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val authorName = "${currentUser!!.name} ${currentUser!!.surname}"

    LaunchedEffect(currentUser) {
        Log.d("USER_DEBUG", "Current user: $authorName")
    }

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var stars by remember { mutableIntStateOf(3) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    val scrollState = rememberScrollState()

    LaunchedEffect(diveSiteComment) {
        diveSiteComment?.let { comment ->
            title = comment.title
            description = comment.description
            stars = comment.stars
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("${if(diveSiteComment != null) "Modifica" else "Aggiungi"} commento") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Indietro")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(scrollState)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Titolo") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descrizione") },
                modifier = Modifier.fillMaxWidth()
            )

            Text("Valutazione: $stars stelle")
            Slider(
                value = stars.toFloat(),
                onValueChange = { stars = it.toInt() },
                valueRange = 1f..5f,
                steps = 3
            )

            Button(onClick = {
                val comment = DiveSiteComment(
                    id = diveSiteComment?.id ?: UUID.randomUUID().toString(),
                    diveId = diveSiteId,
                    authorName = authorName,
                    title = title,
                    description = description,
                    stars = stars,
                    createdAt = System.currentTimeMillis()
                )

                scope.launch {
                    val result = DiveSiteRepository.addCommentToDiveSite(diveSiteId, comment)
                    successMessage = if (result.isSuccess) {
                        "Commento ${if(diveSiteComment != null) "modificato" else "aggiunto"} con successo!"
                    } else {
                        "Errore: ${result.exceptionOrNull()?.message}"
                    }
                    if(result.isSuccess) {
                        delay(500)
                        navController.navigateUp()
                    }
                }
            }) {
                Text("Salva commento")
            }

            successMessage?.let {
                Text(it, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
