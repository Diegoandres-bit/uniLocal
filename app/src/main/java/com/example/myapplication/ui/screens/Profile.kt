package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.myapplication.ui.components.Button as AppButton
import com.example.myapplication.ui.components.Card as AppCard

/**
 * Firma pensada para tu ViewModel actual (no se crean modelos nuevos).
 * - uiState: debes pasar el estado que ya expones desde ProfileViewModel.
 * - onBack / onToggleEdit / onSave / onNameChange / onUsernameChange / onCityChange:
 *   callbacks que ya usas en tu Navigation.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    uiState: Any,                         // <-- usa el tipo de tu UI state real (ej. ProfileUiState)
    onBack: () -> Unit,
    onToggleEdit: () -> Unit,
    onSave: (android.content.Context) -> Unit,
    onNameChange: (String) -> Unit,
    onUsernameChange: (String) -> Unit,
    onCityChange: (String) -> Unit
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Perfil") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.Person, contentDescription = "Atrás")
                    }
                },
                actions = {
                    AppButton(
                        onClick = onToggleEdit,
                        text = "Editar",
                        color = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // ======= Header (avatar, nombre, username, chips) =======
            AppCard(
                elevated = true,
                padding = PaddingValues(16.dp),
                content = {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Avatar(url = uiState.read<String?>("avatarUrl"))

                        Column(Modifier.weight(1f)) {
                            Text(
                                uiState.read<String>("fullName"),
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = "@${uiState.read<String>("username")}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.height(12.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                FilterPill("Categoría")
                                FilterPill("Ciudad")
                                FilterPill("Buscar")
                            }
                        }
                    }
                }
            )

            // ======= Datos de solo lectura =======
            AppCard(
                elevated = true,
                padding = PaddingValues(0.dp),
                content = {
                    ReadonlyRow(label = "Email", value = uiState.read<String>("email"))
                    HorizontalDivider()
                    ReadonlyRow(label = "Ciudad", value = uiState.read<String>("city"))
                    HorizontalDivider()
                    ReadonlyRow(label = "Username", value = uiState.read<String>("username"))
                }
            )

            // ======= Formulario de edición =======
            AppCard(
                elevated = true,
                padding = PaddingValues(16.dp),
                content = {
                    Text("Editar perfil", style = MaterialTheme.typography.titleMedium)

                    Spacer(Modifier.height(8.dp))

                    LabeledField(label = "Nombre") {
                        OutlinedTextField(
                            value = uiState.read<String>("fullName"),
                            onValueChange = onNameChange,
                            enabled = uiState.read<Boolean>("isEditing") && !uiState.read<Boolean>("isSaving"),
                            placeholder = { Text("María González") },
                            supportingText = { Text("Min 2 — Máx 40 caracteres") },
                            isError = uiState.read<String?>("nameError") != null,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                        )
                        uiState.read<String?>("nameError")?.let {
                            Text(
                                it,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Column(Modifier.weight(1f)) {
                            LabeledField("Username") {
                                OutlinedTextField(
                                    value = uiState.read<String>("username"),
                                    onValueChange = onUsernameChange,
                                    enabled = uiState.read<Boolean>("isEditing") && !uiState.read<Boolean>("isSaving"),
                                    placeholder = { Text("maria.g") },
                                    isError = uiState.read<String?>("usernameError") != null,
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                uiState.read<String?>("usernameError")?.let {
                                    Text(
                                        it,
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }
                        }
                        Column(Modifier.weight(1f)) {
                            LabeledField("Ciudad") {
                                OutlinedTextField(
                                    value = uiState.read<String>("city"),
                                    onValueChange = onCityChange,
                                    enabled = uiState.read<Boolean>("isEditing") && !uiState.read<Boolean>("isSaving"),
                                    placeholder = { Text("Bogotá") },
                                    isError = uiState.read<String?>("cityError") != null,
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                uiState.read<String?>("cityError")?.let {
                                    Text(
                                        it,
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AppButton(
                            onClick = onToggleEdit,
                            text = "Cancelar",
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurface,
                            enabled = uiState.read<Boolean>("isEditing") && !uiState.read<Boolean>("isSaving")
                        )
                        AppButton(
                            onClick = { onSave(context) },
                            text = "Guardar",
                            color = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            enabled = uiState.read<Boolean>("isEditing") && !uiState.read<Boolean>("isSaving"),
                            isLoading = uiState.read<Boolean>("isSaving")
                        )
                    }
                }
            )
        }
    }
}

/* =================== Subcomponentes =================== */

@Composable
private fun LabeledField(
    label: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label, style = MaterialTheme.typography.labelLarge)
        content()
    }
}

@Composable
private fun ReadonlyRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun FilterPill(text: String) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
private fun Avatar(url: String?) {
    val size = 72.dp
    if (url.isNullOrBlank()) {
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.Person, contentDescription = null)
        }
    } else {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(url)
                .crossfade(true)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
        )
    }
}

/* ============ Util para leer campos del uiState sin castear arriba ============ */
/* Sustituye por tu tipo real si prefieres. */
@Suppress("UNCHECKED_CAST")
private inline fun <reified T> Any.read(key: String): T {
    // Si tu uiState es una data class, reemplaza esto por acceso directo (uiState.key)
    // Aquí se asume que es un Map-like o que tienes un método get(String).
    return when (this) {
        is Map<*, *> -> this[key] as T
        else -> {
            // Reflection básica para data class/POJO
            val prop = this::class.members.first { it.name == key }
            prop.call(this) as T
        }
    }
}
