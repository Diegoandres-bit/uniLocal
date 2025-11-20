package com.example.myapplication.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R
import com.example.myapplication.model.Schedule
import java.time.format.DateTimeFormatter
import java.util.*

@Composable
fun PlaceDetailsSection(
    description: String,
    schedules: List<Schedule>,
    phone: String,
    city: String,
    modifier: Modifier = Modifier
) {
    val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.getDefault())

    Column(modifier = modifier.padding(16.dp)) {

        // Descripción
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Outlined.Description,
                contentDescription = stringResource(R.string.icon_description),
                tint = Color.Gray
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = stringResource(R.string.label_description),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Text(
            text = description,
            modifier = Modifier.padding(vertical = 8.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Divider()

        // Horarios
        if (schedules.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.label_schedules),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            schedules.forEach { schedule ->
                val day = schedule.day.name.lowercase().replaceFirstChar { it.uppercase() }
                val open = schedule.open.format(timeFormatter)
                val close = schedule.close.format(timeFormatter)
                Text("- $day: $open - $close", fontSize = 13.sp, color = Color.Gray)
            }
        }

        Spacer(Modifier.height(8.dp))

        // Teléfono
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Outlined.Call,
                contentDescription = stringResource(R.string.icon_phone),
                tint = Color.Gray
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = "${stringResource(R.string.label_phone)}: $phone",
                fontSize = 13.sp,
                color = Color.Gray
            )
        }

        // iudad
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Outlined.Public,
                contentDescription = stringResource(R.string.icon_city),
                tint = Color.Gray
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = "${stringResource(R.string.label_city)}: $city",
                fontSize = 13.sp,
                color = Color.Gray
            )
        }
    }
}
