package com.example.myapplication.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.myapplication.model.Place
import com.example.myapplication.model.Schedule
import com.example.myapplication.viewmodel.PlacesViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import com.example.myapplication.ui.components.Card as AppCard
import com.example.myapplication.ui.components.Button as AppButton

/* ----------------------------------------------------------
   Pantalla principal
   ---------------------------------------------------------- */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlacesScreen(
    vm: PlacesViewModel,
    onOpenPlace: (String) -> Unit,
    onCall: (Place) -> Unit,
    onDirections: (Place) -> Unit,
    onOpenFilters: () -> Unit = {}
) {
    val places by vm.places.collectAsState()

    var query by remember { mutableStateOf("") }
    var sort by remember { mutableStateOf(SortOption.RELEVANCE) }

    val filtered = remember(places, query, sort) {
        places
            .filter {
                query.isBlank() ||
                        it.title.contains(query, ignoreCase = true) ||
                        it.type.name.contains(query, ignoreCase = true)
            }
            .let { list ->
                when (sort) {
                    SortOption.RELEVANCE -> list
                    SortOption.DISTANCE -> list // (aquí conectarías tu distancia real)
                    SortOption.RATING -> list // (usarías rating real cuando exista)
                }
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("uniLocal", style = MaterialTheme.typography.titleLarge) },
                actions = {
                    // Acciones de header (favoritos, perfil) – placeholders
                    FilledTonalIconButton(onClick = { /* favs */ }, shape = RoundedCornerShape(16.dp)) {
                        Icon(Icons.Outlined.Star, contentDescription = "Favoritos")
                    }
                    Spacer(Modifier.width(8.dp))
                    FilledTonalIconButton(onClick = { /* perfil */ }, shape = RoundedCornerShape(16.dp)) {
                        Icon(Icons.Outlined.Person, contentDescription = "Perfil")
                    }
                    Spacer(Modifier.width(8.dp))
                }
            )
        },
        bottomBar = {
            // Barra inferior (visual; conecta navegación si quieres)
            NavigationBar(tonalElevation = 2.dp) {
                NavigationBarItem(
                    selected = false, onClick = { /* mapa */ },
                    icon = { Icon(Icons.Outlined.LocationOn, null) }, label = { Text("Mapa") }
                )
                NavigationBarItem(
                    selected = true, onClick = { /* lista */ },
                    icon = {
                        Box(
                            Modifier.size(22.dp).clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = .12f))
                        )
                    }, label = { Text("Lista") }
                )
                NavigationBarItem(
                    selected = false, onClick = { /* favs */ },
                    icon = { Icon(Icons.Outlined.Star, null) }, label = { Text("Favoritos") }
                )
            }
        }
    ) { padding ->

        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
        ) {
            // Buscador + botón Filtros
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Buscar por nombre o tipo...") },
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Outlined.Search, null) },
                    shape = RoundedCornerShape(18.dp)
                )
                Spacer(Modifier.width(12.dp))
                FilledTonalButton(
                    onClick = onOpenFilters,
                    shape = RoundedCornerShape(18.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Icon(Icons.Outlined.FilterList, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Filtros")
                }
            }

            // Chips de orden
            SortRow(sort = sort, onSortChange = { sort = it })

            // Lista
            LazyColumn(
                contentPadding = PaddingValues(16.dp, 12.dp, 16.dp, 100.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(filtered, key = { it.id }) { place ->
                    PlaceListItem(
                        place = place,
                        onOpen = { onOpenPlace(place.id) },
                        onCall = { onCall(place) },
                        onDirections = { onDirections(place) }
                    )
                }
            }
        }
    }
}

/* ----------------------------------------------------------
   Item de la lista (usa tu Card y recibe parámetros)
   ---------------------------------------------------------- */
@Composable
private fun PlaceListItem(
    place: Place,
    onOpen: () -> Unit,
    onCall: () -> Unit,
    onDirections: () -> Unit
) {
    val cover = place.images.firstOrNull()
    val (isOpen, _) = remember(place.schedules) { computeOpenNow(place.schedules) }

    AppCard(
        elevated = true,
        padding = PaddingValues(12.dp),
        onClick = onOpen,
        content = {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {

                // Imagen
                Box(
                    Modifier
                        .size(92.dp)
                        .clip(RoundedCornerShape(16.dp))
                ) {
                    if (cover != null) {
                        AsyncImage(
                            model = cover,
                            contentDescription = place.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Box(
                            Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        )
                    }
                }

                Column(Modifier.weight(1f)) {
                    // Título + estado
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            place.title,
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(Modifier.weight(1f))
                        OpenChip(isOpen)
                    }

                    Spacer(Modifier.height(2.dp))

                    // Línea secundaria: categoría • rating • distancia (placeholder)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SecondaryDot(text = place.type.name.lowercase()
                            .replaceFirstChar { it.titlecase(Locale.getDefault()) })
                        SecondaryDot {
                            Icon(Icons.Outlined.Star, null, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("4.8 (1.2k)", style = MaterialTheme.typography.bodySmall)
                        }
                        SecondaryDot(text = "0.6 km")
                    }

                    Spacer(Modifier.height(6.dp))

                    Text(
                        place.description.ifBlank { "Sin descripción" },
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(Modifier.height(10.dp))

                    // Acciones de íconos – para no chocar con tu AppButton (que mide 200dp),
                    // usamos botones chicos Material.
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        FilledTonalIconButton(onClick = onCall, shape = RoundedCornerShape(12.dp)) {
                            Icon(Icons.Outlined.Call, contentDescription = "Llamar")
                        }
                        FilledTonalIconButton(onClick = onDirections, shape = RoundedCornerShape(12.dp)) {
                            Icon(Icons.Outlined.LocationOn, contentDescription = "Cómo llegar")
                        }
                    }
                }
            }
        }
    )
}

/* ----------------------------------------------------------
   Subcomponentes visuales
   ---------------------------------------------------------- */

@Composable
private fun SortRow(sort: SortOption, onSortChange: (SortOption) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SortChip("Relevancia", selected = sort == SortOption.RELEVANCE) {
            onSortChange(SortOption.RELEVANCE)
        }
        SortChip("Distancia", selected = sort == SortOption.DISTANCE) {
            onSortChange(SortOption.DISTANCE)
        }
        SortChip("Rating", selected = sort == SortOption.RATING) {
            onSortChange(SortOption.RATING)
        }
    }
    Spacer(Modifier.height(8.dp))
}

@Composable
private fun SortChip(text: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(text) },
        shape = RoundedCornerShape(20.dp)
    )
}

private enum class SortOption { RELEVANCE, DISTANCE, RATING }

@Composable
private fun OpenChip(isOpen: Boolean) {
    val (bg, fg, label) =
        if (isOpen)
            Triple(
                MaterialTheme.colorScheme.tertiaryContainer,
                MaterialTheme.colorScheme.onTertiaryContainer,
                "Abierto"
            )
        else
            Triple(
                MaterialTheme.colorScheme.errorContainer,
                MaterialTheme.colorScheme.onErrorContainer,
                "Cerrado"
            )

    Surface(shape = RoundedCornerShape(100), color = bg, contentColor = fg) {
        Text(
            label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun SecondaryDot(text: String) {
    SecondaryDot {
        Text(text, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun SecondaryDot(content: @Composable RowScope.() -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        content()
    }
}

/* ----------------------------------------------------------
   Helpers de horario (no dependen de nombres de campos)
   ---------------------------------------------------------- */

private fun computeOpenNow(schedules: List<com.example.myapplication.model.Schedule>): Pair<Boolean, String> {
    return runCatching {
        if (schedules.isEmpty()) return false to ""

        val now = java.time.LocalTime.now()
        val today = java.time.LocalDate.now().dayOfWeek

        val open = schedules.any { s ->
            // Ajusta a tus nombres reales (dayOfWeek, openTime, closeTime)
            val d = s.day
            val o = s.open
            val c = s.close
            d == today && now.isAfter(o) && now.isBefore(c)
        }
        open to ""    // etiqueta corta opcional si luego quieres mostrar “Hoy (Vie)”
    }.getOrDefault(false to "")
}

