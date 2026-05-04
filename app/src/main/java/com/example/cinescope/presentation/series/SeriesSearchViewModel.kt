package com.example.cinescope.presentation.series

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cinescope.data.series.SeriesRepository
import com.example.cinescope.presentation.models.SeriesPoster
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SeriesSearchUiState {
    data object Idle : SeriesSearchUiState()
    data object Loading : SeriesSearchUiState()
    data class Success(val query: String, val results: List<SeriesPoster>) : SeriesSearchUiState()
    data class Error(val message: String) : SeriesSearchUiState()
}

@HiltViewModel
class SeriesSearchViewModel @Inject constructor(
    private val repository: SeriesRepository
) : ViewModel() {
    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _uiState = MutableStateFlow<SeriesSearchUiState>(SeriesSearchUiState.Idle)
    val uiState: StateFlow<SeriesSearchUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    fun onQueryChange(query: String) {
        _query.value = query
        searchJob?.cancel()

        val trimmed = query.trim()
        if (trimmed.isBlank()) {
            _uiState.value = SeriesSearchUiState.Idle
            return
        }

        searchJob = viewModelScope.launch {
            _uiState.value = SeriesSearchUiState.Loading
            try {
                val results = repository.searchSerials(query = trimmed)
                _uiState.value = SeriesSearchUiState.Success(trimmed, results)
            } catch (e: Exception) {
                _uiState.value = SeriesSearchUiState.Error(e.message ?: "Failed to search series")
            }
        }
    }
}