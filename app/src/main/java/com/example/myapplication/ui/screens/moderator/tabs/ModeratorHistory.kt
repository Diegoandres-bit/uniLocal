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
import com.example.myapplication.theme.GreenCompany
import com.example.myapplication.theme.RedCompany
import com.example.myapplication.viewmodel.PlacesViewModel
import com.example.myapplication.model.Place
import com.example.myapplication.model.PlaceType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModeratorHistoryScreen(viewModel: PlacesViewModel) {
    val places by viewModel.places.collectAsState()

    var selectedTab by remember { mutableStateOf("Autorizados") }
    var searchText by remember { mutableStateOf("") }

    // Simulación: filtramos por tipo como “estado”
    val approvedPlaces = places.filter {
        it.type == PlaceType.RESTAURANT || it.type == PlaceType.BAR
    }

    val rejectedPlaces = places.filterNot {
        it.type == PlaceType.RESTAURANT || it.type == PlaceType.BAR
    }

    val filteredList = (if (selectedTab == "Autorizados") approvedPlaces else rejectedPlaces)
        .filter {
            it.title.contains(searchText, true) || it.city.name.contains(searchText, true)
        }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Historial del Moderador") },
                navigationIcon = {
                    IconButton(onClick = { /* Navegar atrás */ }) {
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

            // Chips: Autorizados / Rechazados
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                FilterChip(
                    selected = selectedTab == "Autorizados",
                    onClick = { selectedTab = "Autorizados" },
                    label = { Text("Autorizados") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GreenCompany,
                        selectedLabelColor = Color.White
                    )
                )
                FilterChip(
                    selected = selectedTab == "Rechazados",
                    onClick = { selectedTab = "Rechazados" },
                    label = { Text("Rechazados") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = RedCompany,
                        selectedLabelColor = Color.White
                    )
                )
            }

            Spacer(Modifier.height(16.dp))

            // Barra de búsqueda
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                placeholder = { Text("Buscar por nombre o ciudad") },
                trailingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            // Lista de lugares filtrados
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(filteredList) { place ->
                    HistoryCard(place, isApproved = selectedTab == "Autorizados")
                }
            }
        }
    }
}

@Composable
fun HistoryCard(place: Place, isApproved: Boolean) {
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
                            .background(if (isApproved) GreenCompany else RedCompany)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (isApproved) "Autorizado" else "Rechazado",
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    }

                    Text(
                        text = "12 Jun 2025", // Simulado
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

@Preview(showBackground = true)
@Composable
fun ModeratorHistoryPreview() {
    val vm = PlacesViewModel()
    ModeratorHistoryScreen(vm)
}
