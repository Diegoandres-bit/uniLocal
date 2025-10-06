package com.example.myapplication.ui.screens.moderator.tabs

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.myapplication.ui.theme.GreenCompany
import com.example.myapplication.ui.theme.RedCompany
import com.example.myapplication.viewmodel.PlacesViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceDetailScreen(
    id: String,
    viewModel: PlacesViewModel,
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val place = viewModel.findById(id)

    if (place == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Lugar no encontrado.")
        }
        return
    }

    val pagerState = rememberPagerState(pageCount = { place.images.size })
    val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.getDefault())

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

            // Carrusel
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
            ) { page ->
                AsyncImage(
                    model = place.images[page],
                    contentDescription = "Imagen del lugar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Indicadores
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(place.images.size) { index ->
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (pagerState.currentPage == index) GreenCompany else Color.LightGray)
                    )
                }
            }

            // Información principal
            Column(Modifier.padding(16.dp)) {
                Text(
                    text = place.title,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, null, tint = Color.Gray)
                    Text(" ${place.address}", fontSize = 14.sp, color = Color.Gray)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Person, null, tint = Color.Gray)
                    Text(" ${place.type}", fontSize = 14.sp, color = Color.Gray)
                }
            }

            Divider()

            // Descripción y detalles
            Column(Modifier.padding(16.dp)) {

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Description, null, tint = Color.Gray)
                    Spacer(Modifier.width(6.dp))
                    Text("Descripción", fontWeight = FontWeight.Bold)
                }
                Text(place.description, modifier = Modifier.padding(vertical = 8.dp))

                if (place.schedules.isNotEmpty()) {
                    Text("Horarios:", fontWeight = FontWeight.Bold)
                    place.schedules.forEach { schedule ->
                        val day = schedule.day.name.lowercase().replaceFirstChar { it.uppercase() }
                        val open = schedule.open.format(timeFormatter)
                        val close = schedule.close.format(timeFormatter)
                        Text("- $day: $open - $close", fontSize = 13.sp)
                    }
                }

                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Call, null, tint = Color.Gray)
                    Spacer(Modifier.width(6.dp))
                    Text("Tel: ${place.phoneNumber}", fontSize = 13.sp)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Public, null, tint = Color.Gray)
                    Spacer(Modifier.width(6.dp))
                    Text("Ciudad: ${place.city}", fontSize = 13.sp)
                }
            }

            // Botones de acción
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                OutlinedButton(onClick = { /* ver en mapa */ }) {
                    Text("Ver en mapa")
                }

                val coroutineScope = rememberCoroutineScope()
                val context = LocalContext.current

                Button(
                    onClick = {
                        viewModel.approvePlace(place.id)
                        Toast.makeText(
                            context,
                            "✅ Lugar aprobado correctamente",
                            Toast.LENGTH_LONG
                        ).show()

                        coroutineScope.launch {
                            kotlinx.coroutines.delay(1500)
                            onBack()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GreenCompany)
                ) { Text("Autorizar") }

                Button(
                    onClick = {
                        viewModel.rejectPlace(place.id)
                        Toast.makeText(
                            context,
                            "❌ Lugar rechazado",
                            Toast.LENGTH_LONG
                        ).show()

                        coroutineScope.launch {
                            kotlinx.coroutines.delay(1500)
                            onBack()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RedCompany)
                ) { Text("Rechazar") }
            }

            Spacer(Modifier.height(8.dp))
            Text(
                text = when (place.status) {
                    com.example.myapplication.model.ReviewStatus.APPROVED -> "✅ Aprobado"
                    com.example.myapplication.model.ReviewStatus.REJECTED -> "❌ Rechazado"
                    else -> "⏳ Pendiente de revisión"
                },
                color = Color.Gray,
                fontSize = 13.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PlaceDetailPreview() {
    val vm = PlacesViewModel()
    PlaceDetailScreen(id = "1", viewModel = vm)
}
