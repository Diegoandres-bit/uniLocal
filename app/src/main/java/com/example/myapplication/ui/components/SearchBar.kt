package com.example.myapplication.ui.components


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication.R

@Composable
fun CompactSearchBar(query: String,
                     onQueryChange: (String) -> Unit,
                     placeHolder:String = "Busca por nombre",
                     filter:Boolean =false,
) {
    androidx.compose.material3.TextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text(placeHolder) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Buscar",
                tint = Color.Gray

            )
        },

        trailingIcon = {
            Button(
                onClick = { /* Acción al presionar */ },
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                modifier = Modifier.height(40.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.lightgreen),
                    contentColor = colorResource(R.color.teal_700),
                    disabledContainerColor = colorResource(R.color.grey),
                    disabledContentColor = colorResource(R.color.dark_grey)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = "Filtros"

                    ,
                )
                Text("Filtros",
                    fontWeight = FontWeight.Bold
                )
            }
        },

        singleLine = true,
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .height(54.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, Color.Gray, RoundedCornerShape(12.dp)),
        colors = androidx.compose.material3.TextFieldDefaults.colors(
            focusedContainerColor = Color.White,    // 👈 fondo blanco al enfocar
            unfocusedContainerColor = Color.White,  // 👈 fondo blanco sin foco
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = Color.Black
        )
    )
}
