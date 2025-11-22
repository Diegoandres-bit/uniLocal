package com.example.myapplication.ui.screens


import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.myapplication.R
import com.example.myapplication.model.PlaceType
import com.example.myapplication.ui.components.Button
import com.example.myapplication.ui.components.InputTextField
import com.example.myapplication.viewmodel.CreatePlaceIntents
import com.example.myapplication.viewmodel.CreatePlaceUiState

@Composable
fun CreatePlaceScreen(
    ui: CreatePlaceUiState,
    intents: CreatePlaceIntents
) {
    val brandGreen = colorResource(R.color.green)
    val white = colorResource(R.color.white)
    val grey = colorResource(R.color.grey)
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia()
    ) { uris ->
        if (uris.isNotEmpty()) {
            intents.onPhotosSelected(uris)
        }
    }

    Scaffold(
        topBar = {
            CreatePlaceTopBar(
                title = "Crear lugar",
                onBack = intents.onBack,
                onDelete = intents.onDeleteDraft
            )
        },
        bottomBar = {
            BottomActionsBar(
                onBack = intents.onPrevious,
                onSaveDraft = intents.onSaveDraft,
                onNext = intents.onNext,
                nextEnabled = ui.canGoNext,
                isSavingDraft = ui.isSavingDraft,
                isPublishing = ui.isPublishing,
                brandGreen = brandGreen,
                white = white,
                grey = grey
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Stepper
            StepsRow(
                steps = ui.steps,
                current = ui.stepIndex,
                accent = brandGreen
            )

            // Form
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .padding(top = 12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Nombre *
                Labeled("Nombre *") {
                    InputTextField(
                        value = ui.name,
                        label = "Ej. Café Andino",
                        supportingText = "Requerido",
                        onValueChange = intents.onNameChange,
                        onValidate = { it.isBlank() },
                        iconDescription = "Nombre",
                        icon = null,
                        isPassword = false,
                        enabled = true
                    )
                }

                // Descripción *
                Labeled("Descripción *") {
                    // Reutilizamos tu InputTextField (singleLine). Si quieres multiline, cambia tu componente.
                    InputTextField(
                        value = ui.description,
                        label = "Breve descripción del lugar...",
                        supportingText = "Incluye lo más relevante para visitantes.",
                        onValueChange = intents.onDescriptionChange,
                        onValidate = { it.isBlank() },
                        iconDescription = "Descripción",
                        icon = null,
                        isPassword = false,
                        enabled = true
                    )
                }
                HelperText("Incluye lo más relevante para visitantes.")

                // Categoría *
                Labeled("Categoría *") {
                    CategoryDropdown(
                        selected = ui.category,
                        options = ui.categoryOptions,
                        placeholder = "Selecciona categoría",
                        onSelected = intents.onCategoryChange
                    )
                }

                // Teléfonos
                Labeled("Teléfonos") {
                    InputTextField(
                        value = ui.phones,
                        label = "+57 300 000 0000",
                        supportingText = "Opcional. Varios separados por coma.",
                        onValueChange = intents.onPhonesChange,
                        onValidate = { false }, // sin validación estricta aquí
                        iconDescription = "Teléfonos",
                        icon = null,
                        isPassword = false,
                        enabled = true
                    )
                }
                HelperText("Opcional. Puedes agregar varios separados por coma.")

                // Fotos (1–5)
                Labeled("Fotos * (1–5)") {
                    PhotosRow(
                        photos = ui.localPhotos,
                        onAddClick = {
                            if (ui.localPhotos.size < 5) {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }
                        },
                        onRemove = intents.onRemovePhoto
                    )
                }

                // Banner de subida
                if (ui.isUploadingPhotos || ui.uploadMessage != null) {
                    UploadingBanner(
                        text = ui.uploadMessage ?: "Subiendo...",
                        tint = brandGreen
                    )
                }

                Spacer(Modifier.height(80.dp)) // espacio para no tapar con bottomBar
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreatePlaceTopBar(
    title: String,
    onBack: () -> Unit,
    onDelete: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = { Text(title, fontWeight = FontWeight.SemiBold) },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Atrás"
                )
            }
        },
        actions = {
            FilledTonalIconButton(onClick = onDelete) {
                Icon(Icons.Outlined.DeleteOutline, contentDescription = "Eliminar borrador")
            }
        }
    )
}


@Composable
private fun StepsRow(
    steps: List<String>,
    current: Int,
    accent: Color
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        itemsIndexed(steps) { index, label ->
            AssistChip(
                onClick = { /* opcional: navegar al paso */ },
                label = { Text(label) },
                leadingIcon = {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(if (index == current) accent else accent.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) { Text("${index + 1}", color = Color.White, fontWeight = FontWeight.Bold) }
                },
                shape = RoundedCornerShape(100),
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = if (index == current) accent.copy(alpha = 0.12f) else Color(0xFFEFF1F1),
                    labelColor = if (index == current) accent else Color(0xFF5F6B68)
                )
            )
        }
    }
}


@Composable
private fun Labeled(title: String, content: @Composable () -> Unit) {
    Column {
        Text(title, style = MaterialTheme.typography.labelLarge)
        Spacer(Modifier.height(6.dp))
        content()
    }
}

@Composable
private fun HelperText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = Color.Gray,
        modifier = Modifier.padding(top = 2.dp)
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryDropdown(
    selected: PlaceType?,
    options: List<PlaceType>,
    placeholder: String,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        // Campo visual
        OutlinedTextField(
            value = selected?.displayName ?: "",
            onValueChange = {},
            readOnly = true,
            placeholder = { Text(placeholder) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { opt ->
                DropdownMenuItem(
                    text = { Text(opt.displayName) },
                    onClick = {
                        onSelected(opt.displayName)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun PhotosRow(
    photos: List<Uri>,
    onAddClick: () -> Unit,
    onRemove: (Uri) -> Unit
) {
    val addSlotsLeft = (5 - photos.size).coerceAtLeast(0)

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Botón agregar (si hay cupo)
        if (addSlotsLeft > 0) {
            item {
                AddPhotoCard(onClick = onAddClick)
            }
        }

        // Miniaturas
        items(photos.size) { i ->
            val uri = photos[i]
            PhotoThumb(uri = uri, onRemove = { onRemove(uri) })
        }
    }
}

@Composable
private fun AddPhotoCard(onClick: () -> Unit) {
    val brandGreen = colorResource(R.color.green)
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = brandGreen.copy(alpha = 0.08f),
        onClick = onClick,
        tonalElevation = 0.dp
    ) {
        Box(
            modifier = Modifier
                .size(88.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("+", color = brandGreen, style = MaterialTheme.typography.titleLarge)
        }
    }
}

@Composable
private fun PhotoThumb(uri: Uri, onRemove: () -> Unit) {
    Box {
        AsyncImage(
            model = uri,
            contentDescription = "Foto",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(88.dp)
                .clip(RoundedCornerShape(12.dp))
        )
        // (Opcional) botón de eliminar en la esquina:
        /*IconButton(
            onClick = onRemove,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(2.dp)
                .size(24.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.4f))
        ) {
            Icon(Icons.Outlined.Close, contentDescription = "Quitar", tint = Color.White)
        }*/
    }
}


@Composable
private fun UploadingBanner(text: String, tint: Color) {
    Surface(
        color = tint.copy(alpha = 0.12f),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                strokeWidth = 2.dp,
                color = tint
            )
            Text(text, color = tint, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun BottomActionsBar(
    onBack: () -> Unit,
    onSaveDraft: () -> Unit,
    onNext: () -> Unit,
    nextEnabled: Boolean,
    isSavingDraft: Boolean,
    isPublishing: Boolean,
    brandGreen: Color,
    white: Color,
    grey: Color
) {
    Surface(tonalElevation = 3.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Atrás (tonal)
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
            ) { Text("Atrás") }

            // Guardar borrador (usa tu LoadingButton si prefieres)
            Button(
                onClick = onSaveDraft,
                text = "Guardar borrador",
                color = grey.copy(alpha = 0.15f),
                contentColor = Color.Black,
                enabled = !isSavingDraft,
                isLoading = isSavingDraft,
                modifier = Modifier.weight(1f)
            )

            // Siguiente (principal)
            Button(
                onClick = onNext,
                text = "Siguiente",
                color = brandGreen,
                contentColor = white,
                enabled = nextEnabled,
                isLoading = isPublishing,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
