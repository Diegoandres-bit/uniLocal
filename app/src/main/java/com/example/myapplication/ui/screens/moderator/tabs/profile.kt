package com.example.myapplication.ui.screens.moderator.tabs

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AlternateEmail
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import com.example.myapplication.model.City
import com.example.myapplication.model.Role
import com.example.myapplication.model.User
import com.example.myapplication.viewmodel.UsersViewModel

@Composable
fun Profile(
    usersViewModel: UsersViewModel,
    loggedInUser: User?
) {
    val user = loggedInUser

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (user == null) {
            EmptyStateNoUser()
        } else {
            ProfileContent(user = user, usersViewModel = usersViewModel)
        }
    }
}

@Composable
private fun ProfileContent(user: User, usersViewModel: UsersViewModel) {
    var displayUser by remember(user.id) { mutableStateOf(user) }
    var showEdit by remember { mutableStateOf(false) }
    var draftName by remember { mutableStateOf(displayUser.name) }
    var draftEmail by remember { mutableStateOf(displayUser.email) }
    var draftCity by remember { mutableStateOf(displayUser.city) }


    val canSave = draftName.isNotBlank() && draftEmail.contains("@")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header (igual)
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {

                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_launcher_foreground),
                            contentDescription = "Avatar",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                        )
                    }

                    Spacer(Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = displayUser.name,
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.AlternateEmail,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "@${displayUser.username} • ${displayUser.email}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                AssistChip(
                    onClick = { },
                    label = {
                        Text(
                            text = roleLabel(displayUser),
                            style = MaterialTheme.typography.labelLarge
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Badge,
                            contentDescription = null ,

                                    tint = colorResource(id = R.color.green)
                        )
                    }
                )
            }
        }

        //  "Editar perfil"
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Datos básicos",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )
            TextButton(onClick = {
                draftName = displayUser.name
                draftEmail = displayUser.email
                draftCity = displayUser.city
                showEdit = true
            }) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = null,
                    tint = colorResource(id = R.color.green)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "Editar perfil",
                    color = colorResource(id = R.color.green)
                )
            }
        }

        // Card Datos básicos
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column {
                DataRow(
                    icon = Icons.Outlined.Person,
                    label = "Nombre",
                    value = displayUser.name
                )
                Divider()
                DataRow(
                    icon = Icons.Outlined.Email,
                    label = "Correo",
                    value = displayUser.email
                )
                Divider()
                DataRow(
                    icon = Icons.Outlined.Badge,
                    label = "Rol",
                    value = roleLabel(displayUser)
                )
                Divider()
                DataRow(
                    icon = Icons.Outlined.LocationOn,
                    label = "Ciudad base",
                    value = cityLabel(displayUser)
                )
            }
        }


        Button(
            onClick = {  },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError
            )
        ) {
            Text("Cerrar sesión")
        }
    }

    // ===== edición =====
    if (showEdit) {
        AlertDialog(
            onDismissRequest = { showEdit = false },
            icon = { Icon(Icons.Outlined.Edit, contentDescription = null) },
            title = { Text("Editar perfil") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = draftName,
                        onValueChange = { draftName = it },
                        singleLine = true,
                        label = { Text("Nombre") },
                        isError = draftName.isBlank(),
                        supportingText = {
                            if (draftName.isBlank()) Text("El nombre no puede estar vacío")
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = draftEmail,
                        onValueChange = { draftEmail = it },
                        singleLine = true,
                        label = { Text("Correo") },
                        isError = !draftEmail.contains("@"),
                        supportingText = {
                            if (!draftEmail.contains("@")) Text("Correo inválido")
                        },
                        modifier = Modifier.fillMaxWidth()
                    )


                    SimpleDropdownField(
                        label = "Ciudad base",
                        valueLabel = cityLabel(displayUser.copy(city = draftCity)),
                        options = City.values().toList(),
                        onOptionSelected = { draftCity = it },
                        optionText = { c -> c.name.lowercase().replaceFirstChar { it.titlecase() } }
                    )


                    Text(
                        text = "Rol: ${roleLabel(displayUser)}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            },
            confirmButton = {
                TextButton(
                    enabled = canSave,
                    onClick = {
                        val updated = displayUser.copy(
                            name = draftName,
                            email = draftEmail,
                            city = draftCity
                        )
                        usersViewModel.update(updated)
                        displayUser = updated
                        showEdit = false
                    }
                ) { Text("Guardar") }
            },
            dismissButton = {
                TextButton(onClick = { showEdit = false }) { Text("Cancelar") }
            }
        )
    }
}

// ====== Helpers ======

@Composable
private fun DataRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}


@Composable
private fun <T> SimpleDropdownField(
    label: String,
    valueLabel: String,
    options: List<T>,
    onOptionSelected: (T) -> Unit,
    optionText: (T) -> String
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
        ) {
            OutlinedTextField(
                value = valueLabel,
                onValueChange = { },
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth(),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.KeyboardArrowDown,
                        contentDescription = null
                    )
                }
            )

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable { expanded = true }
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.width(IntrinsicSize.Max)
            ) {
                options.forEach { opt ->
                    DropdownMenuItem(
                        text = { Text(optionText(opt)) },
                        onClick = {
                            onOptionSelected(opt)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyStateNoUser() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Outlined.Person,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(56.dp)
        )
        Spacer(Modifier.height(12.dp))
        Text(
            "No hay usuario en sesión",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(Modifier.height(4.dp))
        Text(
            "Inicia sesión para ver el perfil.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun roleLabel(user: User): String =
    when (user.role) {
        Role.ADMIN -> "Moderador"
        Role.USER -> "Usuario"
    }

private fun cityLabel(user: User): String =
    user.city.name.lowercase().replaceFirstChar { it.titlecase() }
