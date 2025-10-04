package com.example.myapplication.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp


@Composable
fun Card(
    modifier: Modifier = Modifier,
    elevated: Boolean = true,
    padding: PaddingValues = PaddingValues(16.dp),
    onClick: (() -> Unit)? = null,
    header: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
    footer: (@Composable () -> Unit)? = null,
) {
    val base = if (onClick != null) {
        modifier
            .clip(MaterialTheme.shapes.extraLarge)
            .clickable(onClick = onClick)
    } else modifier

    val container: @Composable (@Composable () -> Unit) -> Unit = { inner ->
        if (elevated) {
            ElevatedCard(modifier = base) { inner() }
        } else {
            Card(modifier = base) { inner() }
        }
    }

    container {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            header?.let { it() }

            // Contenido principal (requerido)
            content()

            footer?.let { it() }
        }
    }
}
