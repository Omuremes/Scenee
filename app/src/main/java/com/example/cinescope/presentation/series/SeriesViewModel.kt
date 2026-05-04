package com.example.cinescope.presentation.series

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cinescope.data.series.SeriesRepository
import com.example.cinescope.presentation.models.SeriesSection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SeriesUiState {
    data object Loading : SeriesUiState()
    data class Success(val sections: List<SeriesSection>) : SeriesUiState()
    data class Error(val message: String) : SeriesUiState()
}

@HiltViewModel
class SeriesViewModel @Inject constructor(
    private val repository: SeriesRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<SeriesUiState>(SeriesUiState.Loading)
    val uiState: StateFlow<SeriesUiState> = _uiState.asStateFlow()

    init {
        loadSeries()
    }

    fun loadSeries() {
        viewModelScope.launch {
            _uiState.value = SeriesUiState.Loading
            try {
                _uiState.value = SeriesUiState.Success(repository.getSeriesSections())
            } catch (e: Exception) {
                _uiState.value = SeriesUiState.Error(e.message ?: "Failed to load series")
            }
        }
    }
}
