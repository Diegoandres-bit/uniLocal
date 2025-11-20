package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.TurnSlightRight
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
import com.example.myapplication.model.Schedule
import com.example.myapplication.viewmodel.PlacesViewModel
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotationGroup
import com.mapbox.maps.plugin.annotation.generated.pointAnnotationOptions
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import com.example.myapplication.ui.components.Card as AppCard
import com.example.myapplication.ui.components.Button as AppButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceDetailsScreen(
    vm: PlacesViewModel,
    placeId: String,
    onBack: () -> Unit = {},
    onCall: () -> Unit = {},
    onDirections: () -> Unit = {},
    onCommentRate: () -> Unit = {},
    onToggleFavorite: () -> Unit = {},
    onShare: () -> Unit = {},
) {
    val places by vm.places.collectAsState()
    val place = remember(places, placeId) { places.find { it.id == placeId } ?: vm.findById(placeId) }
    val viewportState = rememberMapViewportState()

    LaunchedEffect(place?.id) {
        place?.let {
            viewportState.setCameraOptions {
                center(Point.fromLngLat(it.location.longitude, it.location.latitude))
                zoom(15.0)
                pitch(0.0)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de lugar") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                actions = {
                    FilledIconButton(onClick = onToggleFavorite, shape = RoundedCornerShape(16.dp)) {
                        Icon(Icons.Outlined.Star, contentDescription = "Favorito")
                    }
                    Spacer(Modifier.width(8.dp))
                    FilledIconButton(onClick = onShare, shape = RoundedCornerShape(16.dp)) {
                        Icon(Icons.Outlined.Share, contentDescription = "Compartir")
                    }
                    Spacer(Modifier.width(8.dp))
                }
            )
        }
    ) { padding ->
        if (place == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Lugar no encontrado")
            }
            return@Scaffold
        }

        val image = place.images.firstOrNull()
        val (isOpen, todayLabel) = remember(place.schedules) { computeOpenNow(place.schedules) }
        val groups = remember(place.schedules) { compressSchedules(place.schedules) }

        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(padding)
        ) {

            // Header imagen
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(20.dp))
            ) {
                if (image != null) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current).data(image).crossfade(true).build(),
                        contentDescription = place.title,
                        modifier = Modifier.fillMaxWidth().height(180.dp),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        Modifier.fillMaxWidth().height(180.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    )
                }
            }

            // Título + estado
            Column(Modifier.padding(horizontal = 16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        place.title,
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.weight(1f))
                    StatusChip(isOpen)
                }

                Spacer(Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // categoría
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        shape = RoundedCornerShape(100)
                    ) {
                        Text(
                            text = niceType(place),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                    // rating ficticio
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        shape = RoundedCornerShape(100)
                    ) {
                        Row(Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Star, null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("4.8 • 120", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }

            Spacer(Modifier.height(18.dp))
            HorizontalDivider()

            // Horario
            Column(Modifier.padding(16.dp)) {
                Text("Horario", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(10.dp))

                AppCard(
                    elevated = true,
                    padding = PaddingValues(14.dp),
                    content = {   // 👈 content nombrado (evita el error)
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            groups.forEach { (label, hours, isTodayRow) ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(label, style = MaterialTheme.typography.bodyLarge)
                                    if (isTodayRow) {
                                        Surface(
                                            color = MaterialTheme.colorScheme.secondaryContainer,
                                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                            shape = RoundedCornerShape(100)
                                        ) {
                                            Text(
                                                text = "Hoy (${todayLabel.dayOfWeekShort}) $hours",
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                                style = MaterialTheme.typography.labelLarge
                                            )
                                        }
                                    } else {
                                        Text(hours, style = MaterialTheme.typography.bodyLarge)
                                    }
                                }
                            }
                        }
                    }
                )
            }

            // Acciones
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AppButton(
                    onClick = onCall,
                    text = "Llamar",
                    color = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    icon = Icons.Outlined.Phone
                )
                AppButton(
                    onClick = onDirections,
                    text = "Cómo llegar",
                    color = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    icon = Icons.Outlined.TurnSlightRight
                )
            }

            Spacer(Modifier.height(16.dp))
            HorizontalDivider()

            // Ubicación en Mapbox
            Column(Modifier.padding(16.dp)) {
                Text("Ubicación", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(18.dp))
                ) {
                    MapboxMap(
                        modifier = Modifier.fillMaxSize(),
                        mapViewportState = viewportState
                    ) {
                        PointAnnotationGroup(
                            annotations = listOf(
                                pointAnnotationOptions {
                                    geometry(Point.fromLngLat(place.location.longitude, place.location.latitude))
                                    iconImage("marker-15")
                                    textField(place.id)
                                }
                            )
                        )
                    }
                }
            }

            HorizontalDivider()

            // Descripción
            Column(Modifier.padding(16.dp)) {
                Text(
                    place.description.ifBlank { "Sin descripción" },
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    "Ambiente cálido y música suave.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            HorizontalDivider()

            // Comentarios (CTA)
            Row(
                Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Comentarios", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.weight(1f))
                AppButton(
                    onClick = onCommentRate,
                    text = "Comentar/Calificar",
                    color = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            }

            // vacío elegante
            Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Surface(
                    tonalElevation = 1.dp,
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Row(
                        Modifier.fillMaxWidth().padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            Modifier.size(36.dp).clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surface)
                        )
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("Sé el primero en opinar",
                                style = MaterialTheme.typography.bodyMedium)
                            Text(
                                "Toca “Comentar/Calificar” para dejar tu reseña.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(22.dp))
        }
    }
}

/* ---------- helpers ---------- */

@Composable
private fun StatusChip(isOpen: Boolean) {
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
            text = label,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

private data class TodayLabel(val dayOfWeekShort: String)

/** Determina si está abierto ahora; sin depender de nombres de campos del modelo. */
private fun computeOpenNow(schedules: List<Schedule>): Pair<Boolean, TodayLabel> {
    if (schedules.isEmpty()) return false to TodayLabel(dayShort(LocalDate.now()))
    val now = LocalTime.now()
    val today = LocalDate.now().dayOfWeek
    val todayShort = dayShort(LocalDate.now())

    val isOpen = schedules.any { s ->
        val (d, open, close) = s  // desestructura el data class
        d == today && now.isAfter(open) && now.isBefore(close)
    }
    return isOpen to TodayLabel(todayShort)
}

private fun dayShort(date: LocalDate): String =
    date.format(DateTimeFormatter.ofPattern("EEE", Locale.forLanguageTag("es-CO")))

/** Agrupa horarios (p.ej. “Lun–Jue 12:00–22:00”). Independiente de nombres reales. */
private fun compressSchedules(schedules: List<Schedule>): List<Triple<String, String, Boolean>> {
    if (schedules.isEmpty()) return emptyList()

    val order = listOf(
        java.time.DayOfWeek.MONDAY,
        java.time.DayOfWeek.TUESDAY,
        java.time.DayOfWeek.WEDNESDAY,
        java.time.DayOfWeek.THURSDAY,
        java.time.DayOfWeek.FRIDAY,
        java.time.DayOfWeek.SATURDAY,
        java.time.DayOfWeek.SUNDAY
    )
    val fmt = DateTimeFormatter.ofPattern("HH:mm")
    val today = LocalDate.now().dayOfWeek

    val sorted = schedules.sortedBy { order.indexOf(it.component1()) }
    val groups = mutableListOf<MutableList<Schedule>>()
    var current = mutableListOf<Schedule>()

    fun sameRange(a: Schedule, b: Schedule): Boolean {
        val (_, ao, ac) = a
        val (_, bo, bc) = b
        return ao == bo && ac == bc
    }

    sorted.forEach { s ->
        if (current.isEmpty()) {
            current.add(s)
        } else {
            val last = current.last()
            val lastIndex = order.indexOf(last.component1())
            val sIndex = order.indexOf(s.component1())
            val consecutive = sIndex == lastIndex + 1
            if (consecutive && sameRange(last, s)) current.add(s) else {
                groups.add(current); current = mutableListOf(s)
            }
        }
    }
    if (current.isNotEmpty()) groups.add(current)

    return groups.map { g ->
        val first = g.first()
        val last = g.last()
        val firstDay = first.component1()
        val lastDay = last.component1()
        val (dOpen, dClose) = run {
            val (_, o, c) = first
            o to c
        }
        val label =
            if (g.size == 1)
                firstDay.getDisplayName(java.time.format.TextStyle.SHORT, Locale.forLanguageTag("es-CO"))
            else
                "${firstDay.getDisplayName(java.time.format.TextStyle.SHORT, Locale.forLanguageTag("es-CO"))}–${
                    lastDay.getDisplayName(java.time.format.TextStyle.SHORT, Locale.forLanguageTag("es-CO"))
                }"
        val hours = "${dOpen.format(fmt)}–${dClose.format(fmt)}"
        val includesToday = g.any { it.component1() == today }
        Triple(label.replace(".", ""), hours, includesToday)
    }
}

private fun niceType(place: Place): String =
    place.type.name.lowercase().replaceFirstChar { it.titlecase(Locale.getDefault()) }
