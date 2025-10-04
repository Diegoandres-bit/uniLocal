package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.myapplication.ui.theme.GreenCompany
import com.example.myapplication.ui.theme.RedCompany
import com.example.myapplication.viewmodel.PlacesViewModel
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceDetailScreen(
    id: String,
    viewModel: PlacesViewModel,
    onBack: () -> Unit = {}
) {
    val place = viewModel.findById(id)

    if (place == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Lugar no encontrado!")
        }
        return
    }

    val pagerState = rememberPagerState(pageCount = { place.images.size })

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
                    IconButton(onClick = { /* Compartir */ }) {
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

            //Carrusel de imágenes
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            ) { page ->
                AsyncImage(
                    model = place.images[page],
                    contentDescription = "Imagen del lugar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            //Indicadores de imagen
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                repeat(place.images.size) { index ->
                    val color =
                        if (pagerState.currentPage == index) GreenCompany
                        else Color.LightGray

                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(color)
                    )
                }
            }

            //Info principal
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                AsyncImage(
                    model = place.images.firstOrNull(),
                    contentDescription = null,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.LightGray, CircleShape)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(place.title, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(" ${place.city}", fontSize = 13.sp, color = Color.Gray)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            " ${place.type.name.lowercase().replaceFirstChar { it.uppercase() }}",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            Divider(modifier = Modifier.padding(vertical = 4.dp))

            //Descripción y detalles
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Description, contentDescription = null, tint = Color.Gray)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Descripción", fontWeight = FontWeight.Bold)
                }
                Text(place.description, modifier = Modifier.padding(vertical = 6.dp))

                Spacer(modifier = Modifier.height(8.dp))
                if (place.schedules.isNotEmpty()) {
                    Text(
                        "Horarios:",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.getDefault())


                    place.schedules.forEach { schedule ->
                        val day = schedule.day.name.lowercase().replaceFirstChar { it.uppercase() }
                        val open = schedule.open.format(timeFormatter)
                        val close = schedule.close.format(timeFormatter)

                        Text("- $day: $open - $close", fontSize = 13.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Call, contentDescription = null, tint = Color.Gray)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Contacto: ${place.phoneNumber}", fontSize = 13.sp)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Public, contentDescription = null, tint = Color.Gray)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Ciudad: ${place.city}", fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Dirección: ${place.address}", fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            //Botones
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                OutlinedButton(onClick = { }) { Text("Ver en mapa") }
                Button(
                    onClick = { },
                    colors = ButtonDefaults.buttonColors(containerColor = GreenCompany)
                ) { Text("Autorizar") }
                Button(
                    onClick = { },
                    colors = ButtonDefaults.buttonColors(containerColor = RedCompany)
                ) { Text("Rechazar") }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Pendiente de revisión",
                fontSize = 13.sp,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PlaceDetailScreenPreview() {
    val viewModel = PlacesViewModel()
    PlaceDetailScreen(id = "1", viewModel = viewModel)
}
