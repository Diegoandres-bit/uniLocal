package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.myapplication.viewmodel.PlaceStatus
import com.example.myapplication.viewmodel.PlacesViewModel
import com.example.myapplication.viewmodel.UiPlace
import com.example.myapplication.ui.components.Button as AppButton
import com.example.myapplication.ui.components.Card as AppCard
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.compose.foundation.lazy.items   // 👈 importante


// ---------- ENTRADA PRINCIPAL (usa tu ViewModel) ----------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPlacesScreen(
    vm: PlacesViewModel,
    onBack: (() -> Unit)? = null,
    onCreate: (() -> Unit)? = null,
    onComments: (UiPlace) -> Unit
) {
    val ui by vm.ui.collectAsState()

    // diálogo de confirmación
    if (ui.pendingDeleteId != null) {
        AlertDialog(
            onDismissRequest = vm::dismissDelete,
            confirmButton = {
                AppButton(
                    onClick = vm::confirmDelete,
                    text = "Eliminar",
                    color = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            },
            dismissButton = {
                AppButton(
                    onClick = vm::dismissDelete,
                    text = "Cancelar",
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            },
            title = { Text("Eliminar lugar") },
            text = { Text("Esta acción no se puede deshacer. ¿Deseas continuar?") }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis lugares") },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                        }
                    }
                },
                actions = {
                    AppButton(
                        onClick = { onCreate?.invoke() },
                        text = "Crear",
                        color = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            SearchAndFilterRow(
                query = ui.query,
                onQueryChange = vm::onSearch,
                selected = ui.filter,
                onFilterChange = vm::onFilter
            )

            if (ui.isLoading) {
                LinearProgressIndicator(Modifier.fillMaxWidth())
            }

            if (ui.error != null) {
                AssistChip(onClick = vm::refresh, label = { Text("Reintentar: ${ui.error}") })
            }

            if (ui.filtered.isEmpty() && !ui.isLoading) {
                EmptyState()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(ui.filtered, key = { it.id }) { place ->
                        PlaceItemCard(
                            place = place,
                            onView = { /* hook externo si quieres */ },
                            onComments = { onComments(place) },
                            onEdit = { /* hook externo si quieres */ },
                            onDelete = { vm.askDelete(place.id) }
                        )
                    }
                }
            }
        }
    }
}

// ---------- Subcomponentes visuales ----------
@Composable
private fun SearchAndFilterRow(
    query: String,
    onQueryChange: (String) -> Unit,
    selected: PlaceStatus?,
    onFilterChange: (PlaceStatus?) -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.weight(1f),
            placeholder = { Text("Buscar por nombre") },
            singleLine = true,
            shape = RoundedCornerShape(14.dp)
        )
        Spacer(Modifier.width(12.dp))
        var expanded by remember { mutableStateOf(false) }
        FilterChip(selected = selected != null, onClick = { expanded = true }, label = { Text("Estado") })
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(text = { Text("Todos") }, onClick = { onFilterChange(null); expanded = false })
            DropdownMenuItem(text = { Text("Publicado") }, onClick = { onFilterChange(PlaceStatus.PUBLISHED); expanded = false })
            DropdownMenuItem(text = { Text("Pendiente") }, onClick = { onFilterChange(PlaceStatus.PENDING); expanded = false })
            DropdownMenuItem(text = { Text("Rechazado") }, onClick = { onFilterChange(PlaceStatus.REJECTED); expanded = false })
        }
    }
}

@Composable
private fun PlaceItemCard(
    place: UiPlace,
    onView: () -> Unit,
    onComments: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    AppCard(
        elevated = true,
        padding = PaddingValues(14.dp),
        content = {
            Row(verticalAlignment = Alignment.Top) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(place.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            place.name,
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(Modifier.weight(1f))
                        StatusBadge(place.status)
                    }
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(formatDate(place.createdAt), style = MaterialTheme.typography.bodySmall)
                        Spacer(Modifier.width(12.dp))
                        Text("${place.comments}", style = MaterialTheme.typography.bodySmall)
                    }
                    Spacer(Modifier.height(2.dp))
                    Text(
                        "@${place.author.username} • ${place.city}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        footer = {
            // Tu Button fija width(200.dp); para evitar desbordes, los pongo en dos filas.
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AppButton(onClick = onView,     text = "Ver",
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurface)
                AppButton(onClick = onComments, text = "Comentarios",
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurface)
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AppButton(onClick = onEdit,     text = "Editar",
                    color = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary)
                AppButton(onClick = onDelete,   text = "Eliminar",
                    color = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer)
            }
        }
    )
}

@Composable
private fun StatusBadge(status: PlaceStatus) {
    val (bg, fg) = when (status) {
        PlaceStatus.PUBLISHED -> MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
        PlaceStatus.PENDING   -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
        PlaceStatus.REJECTED  -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
    }
    Surface(color = bg, contentColor = fg, shape = RoundedCornerShape(20.dp)) {
        Text(
            when (status) {
                PlaceStatus.PUBLISHED -> "Publicado"
                PlaceStatus.PENDING   -> "Pendiente"
                PlaceStatus.REJECTED  -> "Rechazado"
            },
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            Modifier.size(64.dp),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {}
        Spacer(Modifier.height(12.dp))
        Text("No hay resultados", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(4.dp))
        Text(
            "Intenta cambiar el término de búsqueda o el estado.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// Utilidad fecha
private fun formatDate(date: LocalDate): String =
    date.format(DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.forLanguageTag("es-CO")))
