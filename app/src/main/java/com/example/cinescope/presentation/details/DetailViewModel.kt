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
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
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
    private var loadJob: Job? = null
    private var loadGeneration = 0L

    fun loadMovieDetail(movieId: String) {
        val requestKey = movieRequestKey(movieId)
        val generation = nextLoadGeneration()
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.value = DetailUiState.Loading(requestKey)
            try {
                val detail = movieRepository.getMovieDetail(movieId)
                updateIfCurrent(requestKey, generation) {
                    DetailUiState.SuccessMovie(requestKey, detail)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                updateIfCurrent(requestKey, generation) {
                    DetailUiState.Error(e.message ?: "Unknown error", requestKey)
                }
            }
        }
    }

    fun loadCinemaEventDetail(eventId: String) {
        val requestKey = cinemaEventRequestKey(eventId)
        val generation = nextLoadGeneration()
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.value = DetailUiState.Loading(requestKey)
            try {
                val detail = eventRepository.getCinemaEventDetail(eventId)
                updateIfCurrent(requestKey, generation) {
                    DetailUiState.SuccessMovie(requestKey, detail)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                updateIfCurrent(requestKey, generation) {
                    DetailUiState.Error(e.message ?: "Unknown error", requestKey)
                }
            }
        }
    }

    fun loadEventDetail(eventId: String) {
        val requestKey = eventRequestKey(eventId)
        val generation = nextLoadGeneration()
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.value = DetailUiState.Loading(requestKey)
            try {
                val detail = eventRepository.getEventDetail(eventId)
                updateIfCurrent(requestKey, generation) {
                    DetailUiState.SuccessEvent(requestKey, detail)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                updateIfCurrent(requestKey, generation) {
                    DetailUiState.Error(e.message ?: "Unknown error", requestKey)
                }
            }
        }
    }

    fun loadSeriesDetail(serialId: String) {
        val requestKey = seriesRequestKey(serialId)
        val generation = nextLoadGeneration()
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.value = DetailUiState.Loading(requestKey)
            try {
                val detail = seriesRepository.getSerialDetail(serialId)
                updateIfCurrent(requestKey, generation) {
                    DetailUiState.SuccessSeries(requestKey, detail)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                updateIfCurrent(requestKey, generation) {
                    DetailUiState.Error(e.message ?: "Unknown error", requestKey)
                }
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

    private fun nextLoadGeneration(): Long {
        loadGeneration += 1
        return loadGeneration
    }

    private inline fun updateIfCurrent(requestKey: String, generation: Long, stateFactory: () -> DetailUiState) {
        if (loadGeneration == generation && _uiState.value.activeRequestKey == requestKey) {
            _uiState.value = stateFactory()
        }
    }

    companion object {
        fun movieRequestKey(movieId: String): String = "movie:$movieId"
        fun cinemaEventRequestKey(eventId: String): String = "cinema-event:$eventId"
        fun eventRequestKey(eventId: String): String = "event:$eventId"
        fun seriesRequestKey(serialId: String): String = "series:$serialId"
    }
}

private val DetailUiState.activeRequestKey: String?
    get() = when (this) {
        is DetailUiState.Loading -> requestKey
        is DetailUiState.SuccessSeries -> requestKey
        is DetailUiState.SuccessMovie -> requestKey
        is DetailUiState.SuccessEvent -> requestKey
        is DetailUiState.Error -> requestKey
    }
