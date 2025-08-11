package com.example.appcorsosistemimobile.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.appcorsosistemimobile.R
import com.example.appcorsosistemimobile.data.model.DiveSite

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
                    Text(site.description, style = MaterialTheme.typography.bodySmall, maxLines = 2)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { onDetailsClick(site) }) {
                        Text("Ulteriori dettagli")
                    }
                }
            }

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
