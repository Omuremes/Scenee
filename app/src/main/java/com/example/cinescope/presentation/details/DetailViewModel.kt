package com.example.cinescope.presentation.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cinescope.data.event.EventRepository
import com.example.cinescope.data.movie.MovieRepository
import com.example.cinescope.data.series.SeriesRepository
import com.example.cinescope.presentation.models.EventDetailData
import com.example.cinescope.presentation.models.MovieDetailData
import com.example.cinescope.presentation.models.SeriesDetailData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class DetailUiState {
    data object Loading : DetailUiState()
    data class SuccessSeries(val data: SeriesDetailData) : DetailUiState()
    data class SuccessMovie(val data: MovieDetailData) : DetailUiState()
    data class SuccessEvent(val data: EventDetailData) : DetailUiState()
    data class Error(val message: String) : DetailUiState()
}

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val movieRepository: MovieRepository,
    private val seriesRepository: SeriesRepository,
    private val eventRepository: EventRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    fun loadMovieDetail(movieId: String) {
        viewModelScope.launch {
            _uiState.value = DetailUiState.Loading
            try {
                _uiState.value = DetailUiState.SuccessMovie(movieRepository.getMovieDetail(movieId))
            } catch (e: Exception) {
                _uiState.value = DetailUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun loadCinemaEventDetail(eventId: String) {
        viewModelScope.launch {
            _uiState.value = DetailUiState.Loading
            try {
                _uiState.value = DetailUiState.SuccessMovie(eventRepository.getCinemaEventDetail(eventId))
            } catch (e: Exception) {
                _uiState.value = DetailUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun loadEventDetail(eventId: String) {
        viewModelScope.launch {
            _uiState.value = DetailUiState.Loading
            try {
                _uiState.value = DetailUiState.SuccessEvent(eventRepository.getEventDetail(eventId))
            } catch (e: Exception) {
                _uiState.value = DetailUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun loadSeriesDetail(serialId: String) {
        viewModelScope.launch {
            _uiState.value = DetailUiState.Loading
            try {
                _uiState.value = DetailUiState.SuccessSeries(seriesRepository.getSerialDetail(serialId))
            } catch (e: Exception) {
                _uiState.value = DetailUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    suspend fun submitSeriesReview(serialId: String, rating: Float, text: String) {
        seriesRepository.submitSerialReview(serialId, rating, text)
        _uiState.value = DetailUiState.SuccessSeries(seriesRepository.getSerialDetail(serialId))
    }

    suspend fun updateSeriesReview(serialId: String, reviewId: String, rating: Float, text: String) {
        seriesRepository.updateSerialReview(reviewId, rating, text)
        _uiState.value = DetailUiState.SuccessSeries(seriesRepository.getSerialDetail(serialId))
    }

    suspend fun deleteSeriesReview(serialId: String, reviewId: String) {
        seriesRepository.deleteSerialReview(reviewId)
        _uiState.value = DetailUiState.SuccessSeries(seriesRepository.getSerialDetail(serialId))
    }
}
