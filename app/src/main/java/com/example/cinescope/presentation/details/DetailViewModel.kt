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
    data class Loading(val requestKey: String? = null) : DetailUiState()
    data class SuccessSeries(val requestKey: String, val data: SeriesDetailData) : DetailUiState()
    data class SuccessMovie(val requestKey: String, val data: MovieDetailData) : DetailUiState()
    data class SuccessEvent(val requestKey: String, val data: EventDetailData) : DetailUiState()
    data class Error(val message: String, val requestKey: String? = null) : DetailUiState()
}

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val movieRepository: MovieRepository,
    private val seriesRepository: SeriesRepository,
    private val eventRepository: EventRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    fun loadMovieDetail(movieId: String) {
        val requestKey = movieRequestKey(movieId)
        viewModelScope.launch {
            _uiState.value = DetailUiState.Loading(requestKey)
            try {
                _uiState.value = DetailUiState.SuccessMovie(requestKey, movieRepository.getMovieDetail(movieId))
            } catch (e: Exception) {
                _uiState.value = DetailUiState.Error(e.message ?: "Unknown error", requestKey)
            }
        }
    }

    fun loadCinemaEventDetail(eventId: String) {
        val requestKey = cinemaEventRequestKey(eventId)
        viewModelScope.launch {
            _uiState.value = DetailUiState.Loading(requestKey)
            try {
                _uiState.value = DetailUiState.SuccessMovie(requestKey, eventRepository.getCinemaEventDetail(eventId))
            } catch (e: Exception) {
                _uiState.value = DetailUiState.Error(e.message ?: "Unknown error", requestKey)
            }
        }
    }

    fun loadEventDetail(eventId: String) {
        val requestKey = eventRequestKey(eventId)
        viewModelScope.launch {
            _uiState.value = DetailUiState.Loading(requestKey)
            try {
                _uiState.value = DetailUiState.SuccessEvent(requestKey, eventRepository.getEventDetail(eventId))
            } catch (e: Exception) {
                _uiState.value = DetailUiState.Error(e.message ?: "Unknown error", requestKey)
            }
        }
    }

    fun loadSeriesDetail(serialId: String) {
        val requestKey = seriesRequestKey(serialId)
        viewModelScope.launch {
            _uiState.value = DetailUiState.Loading(requestKey)
            try {
                _uiState.value = DetailUiState.SuccessSeries(requestKey, seriesRepository.getSerialDetail(serialId))
            } catch (e: Exception) {
                _uiState.value = DetailUiState.Error(e.message ?: "Unknown error", requestKey)
            }
        }
    }

    suspend fun submitSeriesReview(serialId: String, rating: Float, text: String) {
        seriesRepository.submitSerialReview(serialId, rating, text)
        val requestKey = seriesRequestKey(serialId)
        _uiState.value = DetailUiState.SuccessSeries(requestKey, seriesRepository.getSerialDetail(serialId))
    }

    suspend fun updateSeriesReview(serialId: String, reviewId: String, rating: Float, text: String) {
        seriesRepository.updateSerialReview(reviewId, rating, text)
        val requestKey = seriesRequestKey(serialId)
        _uiState.value = DetailUiState.SuccessSeries(requestKey, seriesRepository.getSerialDetail(serialId))
    }

    suspend fun deleteSeriesReview(serialId: String, reviewId: String) {
        seriesRepository.deleteSerialReview(reviewId)
        val requestKey = seriesRequestKey(serialId)
        _uiState.value = DetailUiState.SuccessSeries(requestKey, seriesRepository.getSerialDetail(serialId))
    }

    companion object {
        fun movieRequestKey(movieId: String): String = "movie:$movieId"
        fun cinemaEventRequestKey(eventId: String): String = "cinema-event:$eventId"
        fun eventRequestKey(eventId: String): String = "event:$eventId"
        fun seriesRequestKey(serialId: String): String = "series:$serialId"
    }
}
