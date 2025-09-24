package com.example.myapplication.viewmodel

import CommentItemUi
import ProfileUiState
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        ProfileUiState(
            isEditing = true,
            name = "María González",
            username = "maria.g",
            city = "Bogotá",
            email = "maria@example.com",
            avatarRes = null, // usa un drawable si quieres
            comments = listOf(
                CommentItemUi("Excelente café en Chapinero, buen WiFi.", "Hoy • Café Andino"),
                CommentItemUi("Parque tranquilo para estudiar.", "Ayer • Parque Simón Bolívar")
            ),
            chips = listOf("Categoría", "Ciudad", "Buscar"),
            isSaving = false
        )
    )
    val uiState: StateFlow<ProfileUiState> = _uiState

    fun toggleEdit() {
        _uiState.value = _uiState.value.copy(isEditing = !_uiState.value.isEditing)
    }

    fun updateName(newName: String) {
        _uiState.value = _uiState.value.copy(name = newName)
    }

    fun updateUsername(newUser: String) {
        _uiState.value = _uiState.value.copy(username = newUser)
    }

    fun updateCity(newCity: String) {
        _uiState.value = _uiState.value.copy(city = newCity)
    }

    fun save(context: Context) {
        // aquí harías tu lógica real (repo, API, etc.)
        _uiState.value = _uiState.value.copy(isSaving = true)
        // Simulación rápida
        _uiState.value = _uiState.value.copy(isSaving = false, isEditing = false)
        Toast.makeText(context, "Cambios guardados", Toast.LENGTH_SHORT).show()
    }
}