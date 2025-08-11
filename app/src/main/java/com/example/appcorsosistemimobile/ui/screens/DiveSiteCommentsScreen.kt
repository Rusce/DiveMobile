package com.example.appcorsosistemimobile.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.appcorsosistemimobile.data.model.DiveSiteComment
import com.example.appcorsosistemimobile.repository.DiveSiteRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "DiveSiteComments"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiveSiteCommentsScreen(
    diveSiteId: String,
    onBackClick: () -> Unit
) {
    var comments by remember { mutableStateOf<List<DiveSiteComment>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(diveSiteId) {
        scope.launch {
            isLoading = true
            val result = DiveSiteRepository.getCommentsForDiveSite(diveSiteId)
            Log.d(TAG, "Commenti ricevuti da Firestore: ${result.size}")
            result.forEach { Log.d(TAG, "Commento: ${it.title}, autore: ${it.authorName}") }
            comments = result
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Commenti Recenti") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Indietro")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }

                comments.isEmpty() -> {
                    Text(
                        text = "Nessun commento disponibile.",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val sortedComments = comments.sortedByDescending { comment -> comment.createdAt }
                        items(sortedComments) { comment ->
                            CommentItem(comment)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CommentItem(comment: DiveSiteComment) {
    val formatter = remember { SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()) }
    val formattedDate = formatter.format(Date(comment.createdAt))

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if(comment.title.isNotBlank()) {
                Text(comment.title, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
            }
            Text("⭐".repeat(comment.stars), style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            if(comment.description.isNotBlank()) {
                Text(comment.description, style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(8.dp))
            }
            Text("Autore: ${comment.authorName}", style = MaterialTheme.typography.bodySmall)
            Text("Data: $formattedDate", style = MaterialTheme.typography.bodySmall)
        }
    }
}
