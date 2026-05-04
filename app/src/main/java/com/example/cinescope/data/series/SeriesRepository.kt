package com.example.cinescope.data.series

import com.example.cinescope.data.remote.SerialApiService
import com.example.cinescope.data.remote.dto.SerialReviewCreateRequest
import com.example.cinescope.data.remote.dto.SerialReviewUpdateRequest
import com.example.cinescope.data.remote.dto.SerialDetailDto
import com.example.cinescope.data.remote.dto.SerialDto
import com.example.cinescope.data.remote.dto.SerialEpisodeDto
import com.example.cinescope.data.local.SessionManager
import com.example.cinescope.presentation.models.EpisodeItem
import com.example.cinescope.presentation.models.PosterTheme
import com.example.cinescope.presentation.models.SeriesCastMember
import com.example.cinescope.presentation.models.SeriesFilterCategory
import com.example.cinescope.presentation.models.SeriesDetailData
import com.example.cinescope.presentation.models.SeriesReviewItem
import com.example.cinescope.presentation.models.SeriesPoster
import com.example.cinescope.presentation.models.SeriesSection
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.first

@Singleton
class SeriesRepository @Inject constructor(
    private val serialApiService: SerialApiService,
    private val sessionManager: SessionManager
) {
    suspend fun getSeriesSections(): List<SeriesSection> {
        val popular = serialApiService.getPopularSerials().map(::mapToPoster)
        val new = serialApiService.getNewSerials().map(::mapToPoster)
        val all = serialApiService.getSerials().items.map(::mapToPoster)

        return listOf(
            SeriesSection("Popular Series", popular),
            SeriesSection("New Releases", new),
            SeriesSection("All Series", all)
        ).filter { it.items.isNotEmpty() }
    }

    suspend fun searchSerials(
        query: String? = null,
        categoryId: String? = null,
        skip: Int = 0,
        limit: Int = 20
    ): List<SeriesPoster> {
        return serialApiService.getSerials(query, categoryId, skip, limit).items.map(::mapToPoster)
    }

    suspend fun getSerialDetail(serialId: String): SeriesDetailData {
        return serialApiService.getSerialDetail(serialId).toDetailData()
    }

    suspend fun submitSerialReview(serialId: String, rating: Float, text: String) {
        val normalizedText = text.trim().takeIf { it.isNotBlank() }
        serialApiService.createSerialReview(
            token = bearerToken(),
            SerialReviewCreateRequest(
                serial_id = serialId,
                rating = rating.toDouble(),
                text = normalizedText
            )
        )
    }

    suspend fun updateSerialReview(reviewId: String, rating: Float, text: String) {
        val normalizedText = text.trim().takeIf { it.isNotBlank() }
        serialApiService.updateSerialReview(
            token = bearerToken(),
            reviewId = reviewId,
            request = SerialReviewUpdateRequest(
                rating = rating.toDouble(),
                text = normalizedText
            )
        )
    }

    suspend fun deleteSerialReview(reviewId: String) {
        serialApiService.deleteSerialReview(
            token = bearerToken(),
            reviewId = reviewId
        )
    }

    suspend fun getSeasonEpisodes(serialId: String, seasonNumber: Int): List<EpisodeItem> {
        val seasonLabel = "Season $seasonNumber"
        return serialApiService.getSeasonEpisodes(serialId, seasonNumber).map { episode ->
            mapToEpisode(episode, seasonLabel)
        }
    }

    private fun mapToPoster(dto: SerialDto): SeriesPoster {
        return SeriesPoster(
            id = dto.id,
            title = dto.name,
            genre = dto.categories.joinToString(" - ") { it.name },
            rating = String.format("%.1f", dto.average_rating),
            posterUrl = dto.poster_url,
            categories = dto.categories.map {
                SeriesFilterCategory(
                    id = it.id,
                    name = it.name
                )
            },
            theme = PosterTheme.VioletPop
        )
    }

    private fun SerialDetailDto.toDetailData(): SeriesDetailData {
        val sortedSeasons = seasons.sortedBy { it.season_number }
        val episodes = sortedSeasons.flatMap { season ->
            season.episodes.sortedBy { it.episode_number }.map { episode ->
                mapToEpisode(episode, season.title ?: "Season ${season.season_number}")
            }
        }

        return SeriesDetailData(
            title = name,
            genres = categories.map { it.name },
            storyline = description,
            rating = String.format("%.1f", average_rating),
            reviewCount = "${reviews.size} Reviews",
            cast = actors.map { actor ->
                SeriesCastMember(
                    name = actor.full_name,
                    photoUrl = actor.photo_url
                )
            },
            reviews = reviews.map { review ->
                SeriesReviewItem(
                    id = review.id,
                    userId = review.user_id,
                    userName = review.user.username?.takeIf { it.isNotBlank() } ?: "Viewer",
                    userAvatarUrl = review.user.avatar_url,
                    rating = review.rating.toFloat(),
                    text = review.text.orEmpty(),
                    createdAt = review.created_at
                )
            },
            seasons = sortedSeasons.map { it.title ?: "Season ${it.season_number}" },
            episodes = episodes,
            trailerUrl = trailer_url,
            tabs = listOf("Seasons", "Episodes", "Reviews"),
            meta = listOf(created_at.take(4), "${sortedSeasons.size} Seasons", "Series")
        )
    }

    private fun mapToEpisode(dto: SerialEpisodeDto, seasonLabel: String): EpisodeItem {
        return EpisodeItem(
            id = dto.id,
            badge = "EPISODE ${dto.episode_number}",
            seasonLabel = seasonLabel,
            title = dto.title,
            duration = dto.duration.toDurationLabel(),
            description = dto.description.orEmpty(),
            videoUrl = dto.episode_file?.video_url,
            theme = PosterTheme.VioletPop
        )
    }

    private fun Int.toDurationLabel(): String {
        if (this <= 0) return "Duration unknown"
        val hours = this / 3600
        val minutes = (this % 3600) / 60
        val seconds = this % 60

        return when {
            hours > 0 -> String.format(Locale.ENGLISH, "%dh %02dm", hours, minutes)
            minutes > 0 -> String.format(Locale.ENGLISH, "%dm", minutes)
            else -> String.format(Locale.ENGLISH, "%ds", seconds)
        }
    }

    private suspend fun bearerToken(): String {
        val token = sessionManager.authToken.first()
        require(!token.isNullOrBlank()) { "Authorization required" }
        return "Bearer $token"
    }
}
