package com.example.myapplication.ui.screens.moderator.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.components.ImageCarousel
import com.example.myapplication.ui.components.PlaceActionButtons
import com.example.myapplication.ui.components.PlaceDetailsSection
import com.example.myapplication.ui.components.PlaceHeaderInfo
import com.example.myapplication.viewmodel.PlacesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceDetailScreen(
    id: String,
    viewModel: PlacesViewModel,
    readOnly: Boolean = false,
    onBack: () -> Unit = {}
) {
    val place = viewModel.findById(id)

    if (place == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Lugar no encontrado.")
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(place.title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { /* compartir */ }) {
                        Icon(Icons.Default.Share, contentDescription = "Compartir")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {

            ImageCarousel(images = place.images)

            PlaceHeaderInfo(
                title = place.title,
                address = place.address,
                type = place.type.name
            )

            Divider()

            // Descripción y detalles
            PlaceDetailsSection(
                description = place.description,
                schedules = place.schedules,
                phone = place.phoneNumber,
                city = place.city.name
            )

            // Botones de acción
            if (!readOnly) {
                PlaceActionButtons(
                    placeId = place.id,
                    viewModel = viewModel,
                    onBack = onBack
                )
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}
