package com.example.cinescope.presentation.series

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cinescope.data.series.SeriesRepository
import com.example.cinescope.presentation.models.SeriesPoster
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
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

    init {
        observeQuery()
    }

    fun onQueryChange(query: String) {
        _query.value = query
    }

    fun clearSearch() {
        _query.value = ""
        _uiState.value = SeriesSearchUiState.Idle
    }

    @OptIn(FlowPreview::class)
    private fun observeQuery() {
        viewModelScope.launch {
            _query
                .debounce(350)
                .map(::normalizeSeriesSearchQuery)
                .distinctUntilChanged()
                .collectLatest(::search)
        }
    }

    private suspend fun search(query: String) {
        if (query.isBlank()) {
            _uiState.value = SeriesSearchUiState.Idle
            return
        }

        _uiState.value = SeriesSearchUiState.Loading
        try {
            val results = repository.searchSerials(query = query)
            _uiState.value = SeriesSearchUiState.Success(query, results)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            _uiState.value = SeriesSearchUiState.Error(e.message ?: "Failed to search series")
        }
    }
}
