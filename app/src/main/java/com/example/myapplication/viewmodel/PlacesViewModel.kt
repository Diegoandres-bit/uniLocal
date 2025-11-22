package com.example.myapplication.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.remote.FirestorePlacesRepository
import com.example.myapplication.model.Place
import com.example.myapplication.model.ReviewStatus
import java.time.LocalDate
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ---- UI models para la screen "Mis lugares" ----
data class UiAuthor(val username: String)
enum class PlaceStatus { PUBLISHED, PENDING, REJECTED }

data class UiPlace(
    val id: String,
    val name: String,
    val createdAt: LocalDate,
    val comments: Int,
    val author: UiAuthor,
    val city: String,
    val imageUrl: String?,
    val status: PlaceStatus
)

data class MyPlacesUiState(
    val isLoading: Boolean = false,
    val query: String = "",
    val filter: PlaceStatus? = null,
    val items: List<UiPlace> = emptyList(),
    val error: String? = null,
    val pendingDeleteId: String? = null
) {
    val filtered: List<UiPlace>
        get() = items.filter { p ->
            val q = query.isBlank() || p.name.contains(query, ignoreCase = true)
            val f = filter == null || p.status == filter
            q && f
        }
}

class PlacesViewModel : ViewModel() {

    private val repository = FirestorePlacesRepository()

    // ---------------- Fuente de verdad (dominio) ----------------
    private val _places = MutableStateFlow<List<Place>>(emptyList())
    val places: StateFlow<List<Place>> = _places.asStateFlow()

    // Para pantallas de moderador
    val pendingPlaces: StateFlow<List<Place>> =
        places.map { list -> list.filter { it.status == ReviewStatus.PENDING } }
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val approvedPlaces: StateFlow<List<Place>> =
        places.map { list -> list.filter { it.status == ReviewStatus.APPROVED } }
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // ---------------- Estado "Mis lugares" (UI) ----------------
    private val _ui = MutableStateFlow(MyPlacesUiState(isLoading = true))
    val ui: StateFlow<MyPlacesUiState> = _ui

    // ---------------- Estado "Reseña rápida" ----------------
    private val _selectedPlace = MutableStateFlow<Place?>(null)
    val selectedPlace: StateFlow<Place?> = _selectedPlace.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _reviewRating = MutableStateFlow(0)          // 0..5
    val reviewRating: StateFlow<Int> = _reviewRating.asStateFlow()

    private val _reviewComment = MutableStateFlow("")
    val reviewComment: StateFlow<String> = _reviewComment.asStateFlow()

    private val _isSubmittingReview = MutableStateFlow(false)
    val isSubmittingReview: StateFlow<Boolean> = _isSubmittingReview.asStateFlow()

    init {
        observePlaces()
    }

    private fun observePlaces() {
        viewModelScope.launch {
            repository.places
                .onStart { _ui.update { it.copy(isLoading = true, error = null) } }
                .catch { e ->
                    _ui.update { it.copy(isLoading = false, error = e.message ?: "Error cargando lugares") }
                }
                .collect { list ->
                    _places.value = list
                    _ui.update { state -> state.copy(isLoading = false, error = null, items = list.map(::toUi)) }
                    val selectedId = _selectedPlace.value?.id
                    if (selectedId != null) {
                        _selectedPlace.value = list.firstOrNull { it.id == selectedId }
                    }
                }
        }
    }

    // ---------------- Intents Mis lugares ----------------
    fun onSearch(value: String) = _ui.update { it.copy(query = value) }
    fun onFilter(status: PlaceStatus?) = _ui.update { it.copy(filter = status) }
    fun askDelete(id: String) = _ui.update { it.copy(pendingDeleteId = id) }
    fun dismissDelete() = _ui.update { it.copy(pendingDeleteId = null) }

    fun confirmDelete() {
        val id = _ui.value.pendingDeleteId ?: return
        delete(id)
        _ui.update { it.copy(pendingDeleteId = null) }
    }

    fun refresh() {
        viewModelScope.launch {
            _ui.update { it.copy(isLoading = true, error = null) }
            runCatching { repository.places.first() }
                .onSuccess { list ->
                    _places.value = list
                    _ui.update { state -> state.copy(isLoading = false, items = list.map(::toUi)) }
                }
                .onFailure { e ->
                    _ui.update { it.copy(isLoading = false, error = e.message ?: "No pudimos actualizar") }
                }
        }
    }

    fun delete(id: String) {
        viewModelScope.launch {
            repository.deletePlace(id)
                .onFailure { e ->
                    _ui.update { it.copy(error = e.message ?: "Error eliminando el lugar") }
                }
            if (_selectedPlace.value?.id == id) _selectedPlace.value = null
        }
    }

    fun approvePlace(id: String) {
        viewModelScope.launch {
            repository.updateStatus(id, ReviewStatus.APPROVED)
                .onFailure { e ->
                    _ui.update { it.copy(error = e.message ?: "No se pudo aprobar el lugar") }
                }
        }
    }

    fun rejectPlace(id: String) {
        viewModelScope.launch {
            repository.updateStatus(id, ReviewStatus.REJECTED)
                .onFailure { e ->
                    _ui.update { it.copy(error = e.message ?: "No se pudo rechazar el lugar") }
                }
        }
    }

    fun findById(id: String): Place? = _places.value.firstOrNull { it.id == id }

    // ---------------- Intents Reseña rápida ----------------
    fun selectPlace(id: String) { _selectedPlace.value = findById(id) }
    fun clearSelection() { _selectedPlace.value = null }

    fun setLoggedIn(value: Boolean) { _isLoggedIn.value = value }

    fun setReviewRating(value: Int) { _reviewRating.value = value.coerceIn(0, 5) }
    fun setReviewComment(value: String) { _reviewComment.value = value }

    fun cancelReview() {
        _reviewRating.value = 0
        _reviewComment.value = ""
        _isSubmittingReview.value = false
    }

    fun publishReview() {
        val place = _selectedPlace.value ?: return
        if (!_isLoggedIn.value) return
        if (_reviewRating.value !in 1..5 || _reviewComment.value.isBlank()) return

        viewModelScope.launch {
            _isSubmittingReview.value = true
            try {
                // Simulación: reemplaza por tu repositorio real
                delay(800)
                Log.d(
                    "PlacesViewModel",
                    "Reseña publicada para '${place.title}': rating=${_reviewRating.value}, comment='${_reviewComment.value}'"
                )
                cancelReview()
            } finally {
                _isSubmittingReview.value = false
            }
        }
    }

    // --------- Mapping dominio -> UI ----------
    private fun toUi(p: Place): UiPlace = UiPlace(
        id = p.id,
        name = p.title,
        createdAt = LocalDate.now(),
        comments = 0,
        author = UiAuthor(username = p.createdByUserId ?: "usuario"),
        city = p.city.name,
        imageUrl = p.images.firstOrNull(),
        status = when (p.status ?: ReviewStatus.PENDING) {
            ReviewStatus.APPROVED -> PlaceStatus.PUBLISHED
            ReviewStatus.PENDING  -> PlaceStatus.PENDING
            ReviewStatus.REJECTED -> PlaceStatus.REJECTED
        }
    )
}
