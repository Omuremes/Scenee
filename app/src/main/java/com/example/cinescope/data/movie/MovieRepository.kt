package com.example.cinescope.data.movie

import com.example.cinescope.data.remote.MovieApiService
import com.example.cinescope.presentation.models.MovieDateChip
import com.example.cinescope.presentation.models.MovieDetailData
import com.example.cinescope.presentation.models.MovieSession
import com.example.cinescope.presentation.models.MovieTab
import com.example.cinescope.presentation.models.PosterTheme
import com.example.cinescope.presentation.models.ReviewItem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepository @Inject constructor(
    private val movieApiService: MovieApiService
) {
    suspend fun getMovieDetail(movieId: String): MovieDetailData {
        val dto = movieApiService.getMovieDetail(movieId)
        return MovieDetailData(
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
            sessions = dto.episodes.map { ep ->
                MovieSession("${ep.episode_number}:00", "Hall ${ep.season_number}", "Available", "$15")
            },
            description = dto.description,
            cast = dto.actors.map { it.full_name },
            details = listOf("Director" to "Unknown", "Release" to dto.created_at.take(4)),
            reviews = listOf(ReviewItem("5", 0.8f), ReviewItem("4", 0.6f)),
            theme = PosterTheme.VioletPop
        )
    }
}
