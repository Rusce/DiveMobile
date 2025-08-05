package com.example.appcorsosistemimobile.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.appcorsosistemimobile.data.model.DiveSiteComment
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiveSiteCommentsScreen(
    diveSiteId: String,
    onBackClick: () -> Unit
) {
    // Lista fittizia di commenti
    val comments = remember {
        listOf(
            DiveSiteComment(
                id = "1", diveId = diveSiteId, authorId = "utente1",
                title = "Bellissima immersione!",
                description = "Relitto spettacolare, ottima visibilità.",
                stars = 5
            ),
            DiveSiteComment(
                id = "2", diveId = diveSiteId, authorId = "utente2",
                title = "Poca visibilità",
                description = "Fondale interessante ma l'acqua era torbida.",
                stars = 3
            ),
            DiveSiteComment(
                id = "3", diveId = diveSiteId, authorId = "utente3",
                title = "Esperienza fantastica",
                description = "Tanti pesci, posto tranquillo e ben segnalato.",
                stars = 4
            ),
            DiveSiteComment(
                id = "1", diveId = diveSiteId, authorId = "utente1",
                title = "Bellissima immersione!",
                description = "Relitto spettacolare, ottima visibilità.",
                stars = 5
            ),
            DiveSiteComment(
                id = "2", diveId = diveSiteId, authorId = "utente2",
                title = "Poca visibilità",
                description = "Fondale interessante ma l'acqua era torbida.",
                stars = 3
            ),
            DiveSiteComment(
                id = "3", diveId = diveSiteId, authorId = "utente3",
                title = "Esperienza fantastica",
                description = "Tanti pesci, posto tranquillo e ben segnalato.",
                stars = 4
            ),
            DiveSiteComment(
                id = "1", diveId = diveSiteId, authorId = "utente1",
                title = "Bellissima immersione!",
                description = "Relitto spettacolare, ottima visibilità.",
                stars = 5
            ),
            DiveSiteComment(
                id = "2", diveId = diveSiteId, authorId = "utente2",
                title = "Poca visibilità",
                description = "Fondale interessante ma l'acqua era torbida.",
                stars = 3
            ),
            DiveSiteComment(
                id = "3", diveId = diveSiteId, authorId = "utente3",
                title = "Esperienza fantastica",
                description = "Tanti pesci, posto tranquillo e ben segnalato.",
                stars = 4
            ),
            DiveSiteComment(
                id = "1", diveId = diveSiteId, authorId = "utente1",
                title = "Bellissima immersione!",
                description = "Relitto spettacolare, ottima visibilità.",
                stars = 5
            ),
            DiveSiteComment(
                id = "2", diveId = diveSiteId, authorId = "utente2",
                title = "Poca visibilità",
                description = "Fondale interessante ma l'acqua era torbida.",
                stars = 3
            ),
            DiveSiteComment(
                id = "3", diveId = diveSiteId, authorId = "utente3",
                title = "Esperienza fantastica",
                description = "Tanti pesci, posto tranquillo e ben segnalato.",
                stars = 4
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Commenti Recenti") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Indietro")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(comments) { comment ->
                CommentItem(comment)
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
            Text(comment.title, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text("⭐".repeat(comment.stars), style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(comment.description, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Autore: ${comment.authorId}", style = MaterialTheme.typography.bodySmall)
            Text("Data: $formattedDate", style = MaterialTheme.typography.bodySmall)
        }
    }
}
