package com.example.cinescope.presentation.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cinescope.data.CineScopeRepository
import com.example.cinescope.presentation.models.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class DetailUiState {
    object Loading : DetailUiState()
    data class SuccessSeries(val data: SeriesDetailData) : DetailUiState()
    data class SuccessMovie(val data: MovieDetailData) : DetailUiState()
    data class Error(val message: String) : DetailUiState()
}

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: CineScopeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    fun loadMovieDetail(movieId: String) {
        viewModelScope.launch {
            _uiState.value = DetailUiState.Loading
            try {
                val dto = repository.getMovieDetail(movieId)
                
                if (dto.is_series) {
                    val detailData = SeriesDetailData(
                        title = dto.name,
                        rating = String.format("%.1f", dto.average_rating),
                        reviewCount = "2.4k Reviews",
                        storyline = dto.description,
                        genres = dto.categories.map { it.name },
                        tabs = listOf("Seasons", "Episodes", "Reviews"),
                        meta = listOf("${dto.created_at.take(4)}", "${dto.seasons_count} Seasons", "18+"),
                        seasons = (1..dto.seasons_count).map { "Season $it" },
                        episodes = dto.episodes.map { ep ->
                            EpisodeItem(
                                badge = "EPISODE ${ep.episode_number}",
                                title = ep.title,
                                duration = "${ep.duration}m",
                                theme = PosterTheme.VioletPop
                            )
                        },
                        cast = dto.actors.map { it.full_name },
                        reviews = listOf("Amazing show!", "Great plot twists.")
                    )
                    _uiState.value = DetailUiState.SuccessSeries(detailData)
                } else {
                    val detailData = MovieDetailData(
                        title = dto.name,
                        genres = dto.categories.map { it.name },
                        duration = "${dto.duration}m",
                        rating = String.format("%.1f", dto.average_rating),
                        tabs = listOf(MovieTab.Tickets, MovieTab.About, MovieTab.Comments),
                        dates = listOf(
                            MovieDateChip("Wed", "27 Apr", true),
                            MovieDateChip("Thu", "28 Apr", false),
                            MovieDateChip("Fri", "29 Apr", false)
                        ),
                        sessions = dto.episodes.map { ep -> // Используем эпизоды как сеансы для примера
                            MovieSession("${ep.episode_number}:00", "Hall ${ep.season_number}", "Available", "$15", false)
                        },
                        description = dto.description,
                        cast = dto.actors.map { it.full_name },
                        details = listOf("Director" to "Unknown", "Release" to dto.created_at.take(4)),
                        reviews = listOf(ReviewItem("5", 0.8f), ReviewItem("4", 0.6f))
                    )
                    _uiState.value = DetailUiState.SuccessMovie(detailData)
                }
            } catch (e: Exception) {
                _uiState.value = DetailUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
