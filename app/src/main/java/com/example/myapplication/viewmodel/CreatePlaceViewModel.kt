package com.example.myapplication.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class CreatePlaceUiState(
    val stepIndex: Int = 0,
    val steps: List<String> = listOf("Básicos", "Ubicación", "Horario", "Revisión"),

    val name: String = "",
    val description: String = "",
    val category: String? = null,
    val categoryOptions: List<String> = emptyList(),

    val phones: String = "",
    val photos: List<Uri> = emptyList(),   // URIs de galería/cámara
    val isUploadingPhotos: Boolean = false,
    val uploadMessage: String? = null,     // ej: "Subiendo a Firebase..."

    val isSavingDraft: Boolean = false,
    val canGoNext: Boolean = false         // calculado por VM según validaciones
)

data class CreatePlaceIntents(
    val onBack: () -> Unit,
    val onDeleteDraft: () -> Unit,
    val onStepClick: (Int) -> Unit,

    val onNameChange: (String) -> Unit,
    val onDescriptionChange: (String) -> Unit,
    val onCategoryChange: (String) -> Unit,
    val onPhonesChange: (String) -> Unit,

    val onAddPhotoClick: () -> Unit,       // abre picker
    val onRemovePhoto: (Uri) -> Unit,

    val onSaveDraft: () -> Unit,
    val onNext: () -> Unit,
    val onPrevious: () -> Unit
)

class CreatePlaceViewModel : ViewModel() {
    private val _ui = MutableStateFlow(
        CreatePlaceUiState(
            stepIndex = 0,
            categoryOptions = listOf("Café", "Restaurante", "Coworking", "Parque")
        )
    )
    val ui: StateFlow<CreatePlaceUiState> = _ui

    // Validaciones simples para canGoNext
    private fun recompute() {
        val s = _ui.value
        val canNext = s.name.isNotBlank() && s.description.isNotBlank() && s.category != null && s.photos.isNotEmpty()
        _ui.value = s.copy(canGoNext = canNext)
    }

    fun onNameChange(v: String) { _ui.value = _ui.value.copy(name = v); recompute() }
    fun onDescriptionChange(v: String) { _ui.value = _ui.value.copy(description = v); recompute() }
    fun onCategoryChange(v: String) { _ui.value = _ui.value.copy(category = v); recompute() }
    fun onPhonesChange(v: String) { _ui.value = _ui.value.copy(phones = v) }

    fun addPhoto(uri: Uri) { _ui.value = _ui.value.copy(photos = _ui.value.photos + uri); recompute() }
    fun removePhoto(uri: Uri) { _ui.value = _ui.value.copy(photos = _ui.value.photos - uri); recompute() }

    fun saveDraft() { _ui.value = _ui.value.copy(isSavingDraft = true); /* TODO */ _ui.value = _ui.value.copy(isSavingDraft = false) }
    fun next() { /* TODO navegar al paso 2 */ }
    fun back() { }
    fun deleteDraft() { /* TODO */ }
}