import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.EditOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.myapplication.components.Button
import com.example.myapplication.components.InputTextField
import com.example.myapplication.R

/* ---------- UI STATE Y MODELOS DINÁMICOS ---------- */

data class ProfileUiState(
    val isEditing: Boolean,
    val name: String,
    val username: String,
    val city: String,
    val email: String,
    val avatarRes: Int?,                    // si usas recurso local, o null si no hay
    val comments: List<CommentItemUi>,      // lista dinámica
    val chips: List<String>,                // ["Categoría", "Ciudad", "Buscar"] u otros
    val isSaving: Boolean = false
)

data class CommentItemUi(val text: String, val meta: String)

/* ---------- ENTRADA PÚBLICA ---------- */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    uiState: ProfileUiState,
    onBack: () -> Unit,
    onToggleEdit: () -> Unit,                 // devuelve el nuevo estado (opcional)
    onSave: (Context) -> Unit,
    onNameChange: (String) -> Unit,
    onUsernameChange: (String) -> Unit,
    onCityChange: (String) -> Unit
) {
    val isNameError = uiState.name.length !in 2..40
    val isUserError = uiState.username.isBlank()
    val isCityError = uiState.city.isBlank()
    val isFormValid = !isNameError && !isUserError && !isCityError
    val context = LocalContext.current

    val primary = colorResource(id = R.color.green)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar( // título centrado
                title = { Text("Perfil", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Outlined.KeyboardArrowLeft, contentDescription = "Atrás")
                    }
                },
                actions = {
                    TextButton(onClick = { onToggleEdit() }) {
                        Icon(if (uiState.isEditing) Icons.Outlined.EditOff else Icons.Outlined.Edit , contentDescription = null, modifier = Modifier.padding(end = 6.dp))
                        Text(if (uiState.isEditing) "Cancelar" else "Editar", color = primary)
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                ProfileHeader(
                    avatarRes = uiState.avatarRes,
                    name = uiState.name,
                    handle = "@${uiState.username}"
                )
            }

            if (uiState.chips.isNotEmpty()) {
                item { FilterChipsRow(chips = uiState.chips, chipColor = primary) }
            }

            item { Divider(Modifier.padding(top = 8.dp)) }

            // Info “view mode” (dinámico)
            item {
                Column(Modifier.fillMaxWidth()) {
                    InfoRow(label = "Email", value = uiState.email)
                    InfoRow(label = "Ciudad", value = uiState.city)
                    InfoRow(label = "Username", value = uiState.username)
                }
            }

            if (uiState.comments.isNotEmpty()) {
                item { Divider() }
                item { SectionTitle("Tus comentarios") }
                items(uiState.comments) { c ->
                    CommentBubble(item = c, bubbleColor = primary.copy(alpha = 0.12f))
                }
            }

            item { Divider() }

            item { SectionTitle("Editar perfil") }

            // Form de edición (reutiliza tu InputTextField)
            item {
                Column(Modifier.padding(horizontal = 16.dp)) {
                    InputTextField(
                        value = uiState.name,
                        label = "Nombre",
                        supportingText = "Min 2 — Máx 40 caracteres",
                        onValueChange = onNameChange,
                        onValidate = { it.length !in 2..40 },
                        iconDescription = "Nombre",
                        icon = null,
                        enabled = uiState.isEditing
                    )

                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Column(Modifier.weight(1f)) {
                            InputTextField(
                                value = uiState.username,
                                label = "Username",
                                supportingText = "Requerido",
                                onValueChange = onUsernameChange,
                                onValidate = { it.isBlank() },
                                iconDescription = "Usuario",
                                icon = null,
                                enabled = uiState.isEditing
                            )
                        }
                        Column(Modifier.weight(1f)) {
                            InputTextField(
                                value = uiState.city,
                                label = "Ciudad",
                                supportingText = "Requerido",
                                onValueChange = onCityChange,
                                onValidate = { it.isBlank() },
                                iconDescription = "Ciudad",
                                icon = null,
                                enabled = uiState.isEditing
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // Reutiliza tu LoadingButton (sin quemar texto/estado)
                    Button(
                        onClick = { onSave(context) },
                        text = if (uiState.isEditing) "Guardar cambios" else "Editar",
                        color = primary,
                        contentColor = Color.White,
                        enabled = uiState.isEditing && isFormValid && !uiState.isSaving,
                        isLoading = uiState.isSaving,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

/* ---------- REUTILIZABLES (no inputs ni buttons) ---------- */

@Composable
private fun ProfileHeader(
    avatarRes: Int?,
    name: String,
    handle: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (avatarRes != null) {
            Image(
                painter = painterResource(id = avatarRes),
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE9ECEF)),
                contentScale = ContentScale.Crop
            )
        } else {
            // placeholder cuando no hay avatar
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE9ECEF))
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(handle, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        }
    }
}

@Composable
private fun FilterChipsRow(
    chips: List<String>,
    chipColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        chips.forEach { label ->
            AssistChip(
                onClick = { /* TODO hook dinámico si lo necesitas */ },
                label = { Text(label) },
                shape = RoundedCornerShape(12.dp),
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = chipColor.copy(alpha = 0.12f),
                    labelColor = chipColor
                )
            )
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = Color.Gray)
        Text(value, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun CommentBubble(item: CommentItemUi, bubbleColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        // avatar “dot” del comentario
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0xFFE9ECEF))
        )
        Spacer(Modifier.width(12.dp))

        Column(Modifier.weight(1f)) {
            Surface(color = bubbleColor, shape = RoundedCornerShape(16.dp)) {
                Text(
                    text = item.text,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    color = Color(0xFF104C3C) // tono verdoso legible
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = item.meta,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
    )
}