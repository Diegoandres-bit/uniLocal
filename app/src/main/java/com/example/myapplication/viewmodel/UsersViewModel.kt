package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.remote.FirebaseAuthRepository
import com.example.myapplication.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val infoMessage: String? = null,
    val recoveryCode: String? = null,
    val currentUser: User? = null
)

class UsersViewModel(
    private val authRepository: FirebaseAuthRepository = FirebaseAuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _loggedInUser = MutableStateFlow<User?>(null)
    val loggedInUser: StateFlow<User?> = _loggedInUser.asStateFlow()

    init {
        viewModelScope.launch {
            runCatching { authRepository.getCurrentUser() }
                .onSuccess { user ->
                    user?.let {
                        _loggedInUser.value = it
                        _uiState.update { state -> state.copy(currentUser = it) }
                    }
                }
        }
    }

    fun login(identifier: String, password: String) {
        viewModelScope.launch {
            val trimmed = identifier.trim()
            _uiState.update { it.copy(isLoading = true, error = null, infoMessage = null) }
            runCatching { authRepository.login(trimmed, password) }
                .onSuccess { user -> handleSuccessfulLogin(user) }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message ?: "Error") }
                }
        }
    }

    fun requestPasswordReset(identifier: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, infoMessage = null, recoveryCode = null) }
            runCatching { authRepository.requestPasswordReset(identifier.trim()) }
                .onSuccess { (email, code) ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            infoMessage = "Código enviado a $email",
                            recoveryCode = code
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message ?: "Error al enviar el código") }
                }
        }
    }

    fun logout() {
        authRepository.logout()
        _loggedInUser.value = null
        _uiState.update { it.copy(currentUser = null) }
    }

    fun consumeMessage() {
        _uiState.update { it.copy(infoMessage = null, error = null) }
    }

    private fun handleSuccessfulLogin(user: User) {
        _loggedInUser.value = user
        _uiState.update { it.copy(isLoading = false, currentUser = user, error = null) }
    }
}
