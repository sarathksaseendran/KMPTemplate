package com.template.shared.presentation

import androidx.lifecycle.viewModelScope
import com.template.shared.domain.models.User
import com.template.shared.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class AuthUiState

data object AuthUiStateIdle : AuthUiState()
data object AuthUiStateLoading : AuthUiState()
data class AuthUiStateSuccess(val user: User) : AuthUiState()
data class AuthUiStateError(val message: String) : AuthUiState()

class AuthViewModel(private val repository: AuthRepository) : BaseViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiStateIdle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _isLoggedIn = repository.getAuthStatus()
    val isLoggedIn = _isLoggedIn

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _uiState.update { AuthUiStateLoading }
            val result = repository.login(username, password)
            result.onSuccess { user ->
                _uiState.update { AuthUiStateSuccess(user) }
            }.onFailure { e ->
                _uiState.update { AuthUiStateError(e.message ?: "Login Failed") }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _uiState.update { AuthUiStateIdle }
        }
    }
    
    fun getLoggedInUser() = repository.getLoggedInUser()

    override fun onCleared() {
        super.onCleared()
        // Add any cleanup if needed
    }
}
