package com.example.appcorsosistemimobile.ui.components

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.appcorsosistemimobile.R
import com.example.appcorsosistemimobile.data.model.DiveSite
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import com.google.maps.android.compose.CameraPositionState

@Composable
fun GetStars(vote: Double, showNumber: Boolean = false, textStyle: TextStyle = MaterialTheme.typography.headlineSmall) {
    val gold = Color(0xFFFFD700)
    val iconSize = with(LocalDensity.current) {
        textStyle.fontSize.toDp()
    }

    Row {
        if(showNumber) { Text("$vote/5", style = MaterialTheme.typography.bodySmall) }
        Spacer(Modifier.width(4.dp))
        for(i in 1..vote.toInt()) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Stella piena",
                tint = gold,
                modifier = Modifier.size(iconSize)
            )
        }
        when{
            vote - vote.toInt() >= 0.75 -> {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Stella piena",
                    tint = gold,
                    modifier = Modifier.size(iconSize)
                )
            }
            vote - vote.toInt() >= 0.25 -> {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.StarHalf,
                    contentDescription = "Mezza stella",
                    tint = gold,
                    modifier = Modifier.size(iconSize)
                )
            }
            vote < 5.0 -> {
                Icon(
                    imageVector = Icons.Default.StarOutline,
                    contentDescription = "Stella vuota",
                    tint = gold,
                    modifier = Modifier.size(iconSize)
                )
            }
            else -> { }
        }
        for(i in vote.toInt() + 1..4) {
            Icon(
                imageVector = Icons.Default.StarOutline,
                contentDescription = "Stella vuota",
                tint = gold,
                modifier = Modifier.size(iconSize)
            )
        }
    }
}

@Composable
fun InfoOverlay(
    site: DiveSite,
    onDetailsClick: (DiveSite) -> Unit,
    showVotes: Double? = null
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        AsyncImage(
            model = site.imageUrls.firstOrNull(),
            contentDescription = site.name,
            modifier = Modifier.size(100.dp),
            placeholder = painterResource(id = R.drawable.placeholder),
            error = painterResource(id = R.drawable.image_error)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(site.name, style = MaterialTheme.typography.titleMedium)
            if(showVotes != null) {
                Text(
                    text = "Profondità: ${site.minDepth ?: "?"}m - ${site.maxDepth ?: "?"}m",
                    style = MaterialTheme.typography.bodySmall
                )
                GetStars(showVotes, true, MaterialTheme.typography.bodyLarge)
            } else {
                Text(site.description, style = MaterialTheme.typography.bodySmall, maxLines = 2)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { onDetailsClick(site) }) {
                Text("Mostra")
            }
        }
    }
}

@Composable
fun MapInfoOverlay(
    site: DiveSite,
    onDetailsClick: (DiveSite) -> Unit,
    onClose: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box {
            InfoOverlay(
                site = site,
                onDetailsClick = onDetailsClick,
            )

            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
            ) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Chiudi")
            }
        }
    }
}

@Composable
fun SitesListOverlay(
    diveSites: List<DiveSite>,
    distances: MutableMap<DiveSite, Double>,
    votes: MutableMap<DiveSite, Double>,
    cameraPositionState: CameraPositionState,
    onDetailsClick: (DiveSite) -> Unit,
    onClose: () -> Unit
) {
    var filterMenuExpanded by remember { mutableStateOf(false) }
    var sorting by remember { mutableStateOf("nothing") }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 56.dp, start = 16.dp, end = 16.dp, bottom = 100.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button (
                    onClick = { filterMenuExpanded = !filterMenuExpanded },
                    modifier = Modifier.clip(CircleShape),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Sort,
                        contentDescription = "Ordina risultati",
                        modifier = Modifier.padding(end = 2.dp)
                    )
                    Text("Ordina per")
                }

                DropdownMenu(
                    expanded = filterMenuExpanded,
                    onDismissRequest = { filterMenuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Distanza") },
                        onClick = {
                            sorting = "distance"
                            filterMenuExpanded = false
                        }
                    )

                    DropdownMenuItem(
                        text = { Text("Profondità") },
                        onClick = {
                            sorting = "depth"
                            filterMenuExpanded = false
                        }
                    )

                    DropdownMenuItem(
                        text = { Text("Recensioni") },
                        onClick = {
                            sorting = "reviews"
                            filterMenuExpanded = false
                        }
                    )

                    DropdownMenuItem(
                        text = { Text("Nessuno") },
                        onClick = {
                            sorting = "nothing"
                            filterMenuExpanded = false
                        }
                    )
                }

                IconButton(
                    onClick = onClose,
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Chiudi")
                }
            }

            Box {
                LazyColumn(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val sortedSites = when(sorting) {
                        "distance" -> {
                            diveSites.forEach{ distances[it] = SphericalUtil.computeDistanceBetween(
                                cameraPositionState.position.target,
                                LatLng(it.latitude, it.longitude)
                            ) }
                            distances.entries.sortedBy{ it.value }.map{ it.key }
                        }
                        "depth" -> { diveSites.sortedByDescending { it.maxDepth } }
                        "reviews" -> { votes.entries.sortedByDescending{ it.value }.map{ it.key } }
                        else -> { diveSites.toList() }
                    }
                    sortedSites.forEach { Log.d("SORTED SITES", it.toString()) }

                    items(sortedSites) { site ->
                        InfoOverlay(
                            site = site,
                            onDetailsClick = onDetailsClick,
                            showVotes = votes[site]
                        )
                    }
                }
            }
        }
    }
}
