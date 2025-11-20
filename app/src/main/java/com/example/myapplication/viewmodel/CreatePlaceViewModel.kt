package com.example.myapplication.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.FakeCloudDataSource
import com.example.myapplication.model.City
import com.example.myapplication.model.Location
import com.example.myapplication.model.PlaceType
import com.example.myapplication.model.Schedule
import java.time.DayOfWeek
import java.time.LocalTime
import java.util.UUID
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CreatePlaceUiState(
    val stepIndex: Int = 0,
    val steps: List<String> = listOf("Básicos", "Ubicación", "Horario", "Revisión"),

    val name: String = "",
    val description: String = "",
    val category: PlaceType? = null,
    val categoryOptions: List<PlaceType> = PlaceType.entries,

    val phones: String = "",
    val localPhotos: List<Uri> = emptyList(),   // URIs seleccionadas
    val uploadedPhotos: List<String> = emptyList(), // URLs "en la nube"
    val isUploadingPhotos: Boolean = false,
    val uploadMessage: String? = null,     // ej: "Subiendo a Firebase..."

    val isSavingDraft: Boolean = false,
    val canGoNext: Boolean = false,
    val lastMessage: String? = null,
    val isPublishing: Boolean = false,
    val createdPlaceId: String? = null
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
    private val _ui = MutableStateFlow(CreatePlaceUiState())
    val ui: StateFlow<CreatePlaceUiState> = _ui

    // Validaciones simples para canGoNext
    private fun recompute() {
        val s = _ui.value
        val canNext = s.name.isNotBlank() &&
            s.description.isNotBlank() &&
            s.category != null &&
            s.uploadedPhotos.isNotEmpty()
        _ui.value = s.copy(canGoNext = canNext)
    }

    fun onNameChange(v: String) { _ui.update { it.copy(name = v) }; recompute() }
    fun onDescriptionChange(v: String) { _ui.update { it.copy(description = v) }; recompute() }
    fun onCategoryChange(v: String) {
        val selected = _ui.value.categoryOptions.firstOrNull { it.displayName == v || it.name == v } ?: _ui.value.category
        _ui.update { it.copy(category = selected) }
        recompute()
    }
    fun onPhonesChange(v: String) { _ui.update { it.copy(phones = v) } }

    fun addPhoto(uri: Uri, remoteUrl: String) {
        _ui.update {
            it.copy(
                localPhotos = it.localPhotos + uri,
                uploadedPhotos = it.uploadedPhotos + remoteUrl,
                uploadMessage = "Foto subida"
            )
        }
        recompute()
    }

    fun removePhoto(uri: Uri) {
        val index = _ui.value.localPhotos.indexOf(uri)
        if (index == -1) return
        _ui.update {
            it.copy(
                localPhotos = it.localPhotos - uri,
                uploadedPhotos = it.uploadedPhotos.filterIndexed { i, _ -> i != index }
            )
        }
        recompute()
    }

    fun simulatePhotoUpload() {
        viewModelScope.launch {
            val fakeLocal = Uri.parse("content://upload/${UUID.randomUUID()}")
            _ui.update { it.copy(isUploadingPhotos = true, uploadMessage = "Subiendo foto...") }
            val result = FakeCloudDataSource.uploadPhoto(fakeLocal)
            delay(100)
            result.onSuccess { url -> addPhoto(fakeLocal, url) }
            result.onFailure { e ->
                _ui.update { it.copy(uploadMessage = e.message ?: "Error subiendo foto") }
            }
            _ui.update { it.copy(isUploadingPhotos = false) }
        }
    }

    fun saveDraft() {
        viewModelScope.launch {
            _ui.update { it.copy(isSavingDraft = true, lastMessage = null) }
            delay(400)
            _ui.update { it.copy(isSavingDraft = false, lastMessage = "Borrador guardado en la nube (simulado)") }
        }
    }

    fun next() {
        val current = _ui.value
        if (current.stepIndex < current.steps.lastIndex) {
            _ui.update { it.copy(stepIndex = it.stepIndex + 1) }
        } else if (!current.isPublishing) {
            publishPlace()
        }
    }

    fun back() {
        _ui.update { state -> state.copy(stepIndex = (state.stepIndex - 1).coerceAtLeast(0)) }
    }

    fun deleteDraft() {
        _ui.value = CreatePlaceUiState()
    }

    fun consumeMessage() { _ui.update { it.copy(lastMessage = null) } }

    private fun publishPlace() {
        val state = _ui.value
        val category = state.category ?: return
        val draft = FakeCloudDataSource.CreatePlaceDraft(
            name = state.name,
            description = state.description,
            category = category,
            phones = state.phones,
            imageUrls = state.uploadedPhotos,
            address = "Cra 0 # 00-00", // quemado
            city = City.ARMENIA,
            location = Location(
                latitude = 4.514 + state.uploadedPhotos.size * 0.001,
                longitude = -75.677 + state.uploadedPhotos.size * 0.002
            ),
            schedule = listOf(
                Schedule(DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(18, 0))
            )
        )

        viewModelScope.launch {
            _ui.update { it.copy(isPublishing = true, lastMessage = null) }
            FakeCloudDataSource.createPlace(draft)
                .onSuccess { place ->
                    _ui.update {
                        it.copy(
                            isPublishing = false,
                            createdPlaceId = place.id,
                            lastMessage = "Enviado a moderación",
                            stepIndex = 0,
                            name = "",
                            description = "",
                            category = null,
                            phones = "",
                            localPhotos = emptyList(),
                            uploadedPhotos = emptyList(),
                            canGoNext = false
                        )
                    }
                }
                .onFailure { e ->
                    _ui.update { it.copy(isPublishing = false, lastMessage = e.message ?: "Error publicando") }
                }
        }
    }
}
