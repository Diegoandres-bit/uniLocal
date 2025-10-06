package com.example.myapplication.ui.screens.moderator.tabs

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.myapplication.viewmodel.PlacesViewModel
import com.example.myapplication.viewmodel.UsersViewModel
import com.example.myapplication.ui.components.Card
import java.time.format.DateTimeFormatter
import java.util.Locale
import com.example.myapplication.R


private val dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.getDefault())

@Composable
fun Dashboard(placesViewModel: PlacesViewModel, usersViewModel: UsersViewModel) {
    val places by placesViewModel.pendingPlaces.collectAsState()

    val users by usersViewModel.users.collectAsState()
    val context = LocalContext.current
    var selectedPlaceId by remember { mutableStateOf<String?>(null) }

    // Si hay un lugar seleccionado, mostramos la pantalla de detalle
    if (selectedPlaceId != null) {
        PlaceDetailScreen(
            id = selectedPlaceId!!,
            viewModel = placesViewModel,
            onBack = { selectedPlaceId = null } // al volver, regresa al Dashboard
        )
        return
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {

            PendientesPill(count = places.size)

            Spacer(Modifier.height(8.dp))


            ElevatedCard(
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    OutlinedTextField(
                        value = "",
                        onValueChange = { },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Buscar nombre o creador", maxLines = 1) },
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Outlined.Search, null) },
                        shape = RoundedCornerShape(16.dp)
                    )

                    //filters not lgoic yet just design
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = { /* solo diseño */ },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp),
                            shape = RoundedCornerShape(16.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp)
                        ) {
                            Icon(Icons.Outlined.Storefront, null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Categoría", maxLines = 1)
                        }

                        OutlinedButton(
                            onClick = {},
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp),
                            shape = RoundedCornerShape(16.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp)
                        ) {
                            Icon(Icons.Outlined.LocationOn, null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Ciudad", maxLines = 1)
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
        }
        if (places.isEmpty()) {
            item {
                Text(
                    text = "No hay lugares pendientes por revisar.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }



        items(places) { place ->
            val user = users.find { it.id == place.createdByUserId }

            Card(
                elevated = true, onClick = { selectedPlaceId = place.id },

                // ---------- CONTENIDO SUPERIOR ----------
                content = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val firstImage = place.images.firstOrNull()
                        if (firstImage != null) {
                            AsyncImage(
                                model = firstImage,
                                contentDescription = place.title,
                                modifier = Modifier
                                    .size(70.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = place.title,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                IconText(
                                    icon = Icons.Outlined.Storefront,
                                    text = place.type.name.lowercase()
                                        .replaceFirstChar { it.titlecase() })
                                IconText(
                                    icon = Icons.Outlined.LocationOn,
                                    text = place.city.name.lowercase()
                                        .replaceFirstChar { it.titlecase() })
                            }

                            val usernameText = user?.username?.let { "@$it" } ?: "Desconocido"
                            val dateText = place.createdAt.format(dateFormatter)

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                IconText(icon = Icons.Outlined.Person, text = usernameText)
                                IconText(icon = Icons.Outlined.CalendarToday, text = dateText)
                            }
                        }
                    }
                },

                // ---------- FOOTER: BOTONES ----------
                footer = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        OutlinedButton(
                            onClick = { selectedPlaceId = place.id },
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.onSurface
                            ),
                            border = ButtonDefaults.outlinedButtonBorder
                        ) {
                            Text("Ver")
                        }

                        Button(
                            onClick = {
                                placesViewModel.approvePlace(place.id)
                                Toast.makeText(
                                    context, "✅ Lugar aprobado correctamente", Toast.LENGTH_LONG
                                ).show()
                            },
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorResource(R.color.green),
                                contentColor = MaterialTheme.colorScheme.onPrimary // o Color.White
                            )
                        ) { Text("Autorizar") }

                        Button(
                            onClick = {
                                placesViewModel.rejectPlace(place.id)
                                Toast.makeText(context, "❌ Lugar rechazado", Toast.LENGTH_LONG)
                                    .show()
                            },
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = MaterialTheme.colorScheme.onError
                            )
                        ) { Text("Rechazar") }
                    }
                })
        }
    }
}


@Composable
private fun PendientesPill(count: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp))
                .background(colorResource(R.color.green))
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Pendientes",
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.titleSmall
            )

            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.15f))
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$count",
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}

@Composable
fun IconText(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}



