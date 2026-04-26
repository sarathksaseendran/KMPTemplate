package com.template.shared.presentation

import androidx.lifecycle.viewModelScope
import com.template.shared.domain.models.Item
import com.template.shared.domain.repository.ItemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class MainUiState

data object MainUiStateLoading : MainUiState()
data class MainUiStateSuccess(val items: List<Item>) : MainUiState()
data class MainUiStateError(val message: String) : MainUiState()

class MainViewModel(private val repository: ItemRepository) : BaseViewModel() {

    private val _uiState = MutableStateFlow<MainUiState>(MainUiStateLoading)
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        loadItems()
    }

    fun loadItems() {
        viewModelScope.launch {
            _uiState.update { MainUiStateLoading }
            try {
                val items = repository.getItems()
                _uiState.update { MainUiStateSuccess(items) }
            } catch (e: Exception) {
                _uiState.update { MainUiStateError(e.message ?: "Unknown Error") }
            }
        }
    }
}
