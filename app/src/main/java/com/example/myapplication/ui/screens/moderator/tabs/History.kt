package com.example.myapplication.ui.screens.moderator.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.model.ReviewStatus
import com.example.myapplication.ui.components.PlaceCard
import com.example.myapplication.ui.components.StatusFilterChips
import com.example.myapplication.ui.components.StatusPill
import com.example.myapplication.ui.theme.GreenCompany
import com.example.myapplication.ui.theme.RedCompany
import com.example.myapplication.viewmodel.PlacesViewModel
import com.example.myapplication.R
import com.example.myapplication.ui.theme.BlueCompany

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun History(viewModel: PlacesViewModel) {
    val places by viewModel.places.collectAsState()
    var selectedTab by remember { mutableStateOf<ReviewStatus?>(null) }
    var searchText by remember { mutableStateOf("") }
    var selectedPlaceId by remember { mutableStateOf<String?>(null) }

    // Mostrar detalle si se seleccionó un lugar
    if (selectedPlaceId != null) {
        PlaceDetailScreen(
            id = selectedPlaceId!!,
            viewModel = viewModel,
            readOnly = true,
            onBack = { selectedPlaceId = null }
        )
        return
    }

    // Filtrar lugares según estado y texto de búsqueda
    val filteredPlaces = places.filter { place ->
        place.status != ReviewStatus.PENDING &&
                (selectedTab == null || place.status == selectedTab) &&
                (place.title.contains(searchText, true) ||
                        place.city.name.contains(searchText, true))
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.txt_history_title)) },
                navigationIcon = {
                    IconButton(onClick = { /* volver */ }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.txt_volver)
                        )
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
            // Filtros reutilizables
            StatusFilterChips(selectedTab) { selectedTab = it }

            // Contadores de lugares gestionados
            val approvedCount = places.count { it.status == ReviewStatus.APPROVED }
            val rejectedCount = places.count { it.status == ReviewStatus.REJECTED }
            val totalCount = approvedCount + rejectedCount

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                when (selectedTab) {
                    ReviewStatus.APPROVED -> StatusPill(
                        approvedCount,
                        stringResource(R.string.lbl_autorizados),
                        GreenCompany
                    )

                    ReviewStatus.REJECTED -> StatusPill(
                        rejectedCount,
                        stringResource(R.string.lbl_rechazados),
                        RedCompany
                    )

                    else -> StatusPill(
                        totalCount,
                        stringResource(R.string.lbl_total_gestionados),
                        color = BlueCompany
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // 🔹 Barra de búsqueda
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                placeholder = { Text(stringResource(R.string.place_holder_buscar)) },
                trailingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = stringResource(R.string.icon_search_description)
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            // 🔹 Lista de lugares
            if (filteredPlaces.isEmpty()) {
                Text(
                    text = stringResource(R.string.txt_no_register),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(filteredPlaces) { place ->
                        PlaceCard(place) {
                            selectedPlaceId = place.id
                        }
                    }
                }
            }
        }
    }
}

