package com.example.myapplication.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
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

@Composable
fun PlaceHeaderInfo(
    title: String,
    address: String,
    type: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(16.dp)) {

        // Título del lugar
        Text(
            text = title,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(Modifier.height(4.dp))

        // Dirección
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = stringResource(R.string.icon_location_description),
                tint = Color.Gray
            )
            Text(
                text = " $address",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }

        // Tipo de lugar
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = stringResource(R.string.icon_type_description),
                tint = Color.Gray
            )
            Text(
                text = " $type",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}
