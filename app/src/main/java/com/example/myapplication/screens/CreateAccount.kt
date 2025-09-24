package com.example.myapplication.screens

import android.graphics.drawable.Icon
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Password
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import com.example.myapplication.components.Button
import com.example.myapplication.components.InputTextField


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAccount(
    onBack: () -> Unit = {},
    onAlreadyHaveAccount: () -> Unit = {}
) {
    val green = colorResource(R.color.green)
    val white = colorResource(R.color.white)
    val grey = colorResource(R.color.grey)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Crear cuenta", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text("Únete a uniLocal", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
            Text(
                "Crea tu cuenta para ver lugares cercanos y guardar tu historial.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            // Nombre (deshabilitado, solo maquetación)
            InputTextField(
                value = "",
                label = "Tu nombre",
                supportingText = "Requerido",
                onValueChange = {},
                onValidate = { false },
                iconDescription = "Nombre",
                icon = Icons.Outlined.Person,
            )

            // Email (deshabilitado)
            InputTextField(
                value = "tucorreo@ejemplo.com",
                label = "Correo electrónico",
                supportingText = "Correo válido",
                onValueChange = {},
                onValidate = { false },
                iconDescription = "Correo",
                icon = Icons.Outlined.Email,
            )

            // Contraseña (deshabilitado) + barra fija
            InputTextField(
                value = "••••••••",
                label = "Contraseña",
                supportingText = "mín. 8 caracteres",
                onValueChange = {},
                onValidate = { false },
                iconDescription = "Contraseña",
                icon = Icons.Outlined.Password,
                isPassword = true,
            )
            PasswordStrengthBarStatic(progress = 0.5f, label = "Segura", accent = green)

            // Ciudad / Categoría favorita (maqueta tipo dropdown)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                SelectionFieldStatic(
                    title = "Ciudad",
                    placeholder = "Selecciona ciudad",
                    modifier = Modifier.weight(1f)
                )
                SelectionFieldStatic(
                    title = "Categoría favorita",
                    placeholder = "Comida, cafés…",
                    modifier = Modifier.weight(1f)
                )
            }

            // Checkbox términos (maqueta deshabilitada)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFEFF2F1))
                    .padding(horizontal = 12.dp, vertical = 10.dp)
                    .fillMaxWidth()
            ) {
                Checkbox(checked = false, onCheckedChange = null, enabled = false)
                Spacer(Modifier.width(8.dp))
                Text("Acepto los Términos y la Privacidad.")
            }

            // Botón principal (sin acción real)
            Button(
                onClick = { /* no-op */ },
                text = "Crear cuenta",
                color = green,
                contentColor = white,
                isLoading = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            )

            // Secundario: Ya tengo cuenta
            OutlinedButton(
                onClick = onAlreadyHaveAccount,
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Ya tengo cuenta")
            }

            InfoBannerStatic(
                text = "Usaremos tu correo para confirmar tu cuenta.",
                tint = grey
            )

            Spacer(Modifier.height(12.dp))
        }
    }
}

/* ------------------- Helpers estáticos (sin estado) ------------------- */

@Composable
private fun PasswordStrengthBarStatic(progress: Float, label: String, accent: Color) {
    Column {
        LinearProgressIndicator(
            progress = { progress.coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(12.dp)),
            trackColor = Color(0xFFE6E8E7),
            color = accent
        )
        if (label.isNotEmpty()) {
            Text(label, color = accent, style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
private fun SelectionFieldStatic(
    title: String,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Text(title, style = MaterialTheme.typography.labelLarge)
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = "",
            onValueChange = {},
            readOnly = true,
            placeholder = { Text(placeholder) },
            enabled = false,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
    }
}

@Composable
private fun InfoBannerStatic(text: String, tint: Color) {
    Surface(
        color = tint.copy(alpha = 0.12f),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(tint)
            )
            Text(text, color = tint, style = MaterialTheme.typography.bodyMedium)
        }
    }
}