package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.myapplication.model.Place
import com.example.myapplication.viewmodel.PlacesViewModel
import com.example.myapplication.ui.components.Button as AppButton
import com.example.myapplication.ui.components.Card as AppCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickReviewScreen(
    vm: PlacesViewModel,
    onBack: () -> Unit,          // lo decides afuera
    onLogin: () -> Unit          // acción para abrir login
) {
    // --- Estado que provee tu ViewModel (sin crear modelos nuevos) ---
    val place     by vm.selectedPlace.collectAsState()
    val loggedIn  by vm.isLoggedIn.collectAsState()
    val rating    by vm.reviewRating.collectAsState()
    val comment   by vm.reviewComment.collectAsState()
    val sending   by vm.isSubmittingReview.collectAsState()

    val maxChars = 500
    val publishEnabled = loggedIn && rating in 1..5 && comment.isNotBlank()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Reseña rápida", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = 1.dp,
                shadowElevation = 6.dp,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AppButton(
                        onClick = { vm.cancelReview(); onBack() },
                        text = "Cancelar",
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                    AppButton(
                        onClick = { vm.publishReview() },
                        text = "Publicar",
                        color = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        isLoading = sending,
                        enabled = publishEnabled && !sending
                    )
                }
            }
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Banner de login
            if (!loggedIn) {
                Surface(color = MaterialTheme.colorScheme.surface, tonalElevation = 1.dp) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Lock, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.width(12.dp))
                        Text(
                            "Inicia sesión para publicar.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(onClick = onLogin) { Text("Iniciar sesión") }
                    }
                }
            }

            HorizontalDivider()

            // Cabecera del lugar
            place?.let { p ->
                PlaceHeaderCard(p)
                HorizontalDivider()
            }

            // Calificación
            SectionTitle("Calificación")
            StarRow(
                rating = rating,
                onRate = vm::setReviewRating
            )

            HorizontalDivider(thickness = 0.8.dp)

            // Comentario
            SectionTitle("Comentario")
            OutlinedTextField(
                value = comment,
                onValueChange = { vm.setReviewComment(it.take(maxChars)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 140.dp),
                placeholder = { Text("Comparte brevemente tu experiencia...") },
                shape = RoundedCornerShape(16.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Máx. $maxChars caracteres.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(16.dp))
        }
    }
}

/* ---------------- Subcomponentes (solo UI) ---------------- */

@Composable
private fun PlaceHeaderCard(place: Place) {
    AppCard(
        elevated = false,
        padding = PaddingValues(16.dp),
        content = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(place.images.firstOrNull())
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        place.title,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        // subtítulo simple usando el tipo/categoría
                        place.type.name.lowercase().replaceFirstChar { it.titlecase() },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    )
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
    )
}

@Composable
private fun StarRow(
    rating: Int,
    onRate: (Int) -> Unit
) {
    Row(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        (1..5).forEach { i ->
            IconButton(onClick = { onRate(i) }) {
                if (rating >= i) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "$i estrellas",
                        tint = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Icon(Icons.Outlined.Star, contentDescription = "$i estrellas")
                }
            }
        }
    }
}
