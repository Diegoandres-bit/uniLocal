package com.example.myapplication.ui.a.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.example.myapplication.R

@Composable
fun Button(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    color: Color,
    text: String,
    contentColor: Color,
    icon: ImageVector? = null,
    iconDescription: String? = null,
    isLoading: Boolean = false               // 👈 nuevo parámetro
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .width(200.dp)
            .height(48.dp),
        enabled = enabled && !isLoading,      // 👈 desactiva mientras carga
        colors = ButtonDefaults.buttonColors(
            containerColor = color,
            contentColor = contentColor,
            disabledContainerColor = colorResource(R.color.grey),
            disabledContentColor = colorResource(R.color.dark_grey)
        )
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(20.dp)
                        .padding(end = 8.dp),
                    strokeWidth = 2.dp,
                )
            } else if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = iconDescription ?: text,
                    modifier = Modifier
                        .size(20.dp)
                        .padding(end = 8.dp)
                )
            }

            Text(text)
        }
    }
}
