package com.example.myapplication.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.myapplication.model.Place
import com.example.myapplication.model.ReviewStatus
import com.example.myapplication.ui.theme.GreenCompany
import com.example.myapplication.ui.theme.RedCompany

@Composable
fun PlaceCard(
    place: Place,
    onClick: () -> Unit = {}
) {
    val (color, label) = when (place.status) {
        ReviewStatus.APPROVED -> GreenCompany to "Autorizado"
        ReviewStatus.REJECTED -> RedCompany to "Rechazado"
        else -> Color.Gray to "Pendiente"
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
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
