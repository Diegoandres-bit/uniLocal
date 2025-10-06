package com.example.myapplication.ui.screens.moderator.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.myapplication.model.Place
import com.example.myapplication.model.ReviewStatus
import com.example.myapplication.ui.theme.GreenCompany
import com.example.myapplication.ui.theme.RedCompany
import com.example.myapplication.viewmodel.PlacesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun History(viewModel: PlacesViewModel) {
    val places by viewModel.places.collectAsState()

    // Estado actual de los filtros
    var selectedTab by remember { mutableStateOf<ReviewStatus?>(null) }
    var searchText by remember { mutableStateOf("") }

    // Filtrar según estado y texto
    val filteredPlaces = places.filter { place ->
        // Mostrar solo los que ya fueron revisados
        place.status != ReviewStatus.PENDING &&
                (selectedTab == null || place.status == selectedTab) &&
                (place.title.contains(searchText, true) ||
                        place.city.name.contains(searchText, true))
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Historial del Moderador") },
                navigationIcon = {
                    IconButton(onClick = { /* volver */ }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Chips: Todos / Autorizados / Rechazados
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                FilterChip(
                    selected = selectedTab == null,
                    onClick = { selectedTab = null },
                    label = { Text("Todos") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = Color.White
                    )
                )

                FilterChip(
                    selected = selectedTab == ReviewStatus.APPROVED,
                    onClick = { selectedTab = ReviewStatus.APPROVED },
                    label = { Text("Autorizados") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GreenCompany,
                        selectedLabelColor = Color.White
                    )
                )

                FilterChip(
                    selected = selectedTab == ReviewStatus.REJECTED,
                    onClick = { selectedTab = ReviewStatus.REJECTED },
                    label = { Text("Rechazados") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = RedCompany,
                        selectedLabelColor = Color.White
                    )
                )
            }

            val approvedCount = places.count { it.status == ReviewStatus.APPROVED }
            val rejectedCount = places.count { it.status == ReviewStatus.REJECTED }
            val totalCount = approvedCount + rejectedCount

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center // 🔹 Centra el contenido horizontalmente
            ) {
                when (selectedTab) {
                    ReviewStatus.APPROVED -> GestionadosPill(
                        approvedCount,
                        "Autorizados",
                        GreenCompany
                    )

                    ReviewStatus.REJECTED -> GestionadosPill(
                        rejectedCount,
                        "Rechazados",
                        RedCompany
                    )

                    else -> GestionadosPill(
                        totalCount,
                        "Total gestionados",
                        Color(0xFF2196F3)
                    ) // Azul
                }
            }

            Spacer(Modifier.height(20.dp)) // 🔹 Espacio adicional antes de la barra de búsqueda

            // Barra de búsqueda
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                placeholder = { Text("Buscar por nombre o ciudad") },
                trailingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            // Lista de resultados
            if (filteredPlaces.isEmpty()) {
                Text(
                    text = "No hay registros que coincidan con tu búsqueda.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(filteredPlaces) { place ->
                        HistoryCard(place)
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryCard(place: Place) {
    val (color, label) = when (place.status) {
        ReviewStatus.APPROVED -> GreenCompany to "Autorizado"
        ReviewStatus.REJECTED -> RedCompany to "Rechazado"
        else -> Color.Gray to "Pendiente"
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = place.images.firstOrNull(),
                contentDescription = place.title,
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(12.dp))
            )

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(color)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(text = label, color = Color.White, fontSize = 12.sp)
                    }

                    Text(
                        text = place.createdAt.toLocalDate().toString(),
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Text(place.title, fontWeight = FontWeight.Bold)
                Text(
                    "${place.type.name.lowercase().replaceFirstChar { it.uppercase() }} • " +
                            "${place.city.name.lowercase().replaceFirstChar { it.uppercase() }}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                Spacer(Modifier.height(4.dp))
                Text("Motivo: ${place.description}", fontSize = 13.sp)
            }
        }
    }
}

@Composable
fun GestionadosPill(count: Int, label: String, color: Color) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(color)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            color = Color.White,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp)
        )

        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.2f))
                .padding(horizontal = 8.dp, vertical = 3.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$count",
                color = Color.White,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp)
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ModeratorHistoryPreview() {
    val vm = PlacesViewModel()
    History(vm)
}
