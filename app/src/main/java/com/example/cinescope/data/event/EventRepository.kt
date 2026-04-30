package com.example.cinescope.data.event

import com.example.cinescope.data.remote.EventApiService
import com.example.cinescope.data.remote.dto.EventCardDto
import com.example.cinescope.data.remote.dto.EventDetailDto
import com.example.cinescope.data.remote.dto.EventReviewDto
import com.example.cinescope.data.remote.dto.EventReviewsSummaryDto
import com.example.cinescope.data.remote.dto.EventSeatDto
import com.example.cinescope.data.remote.dto.EventSessionDto
import com.example.cinescope.presentation.models.EventDetailData
import com.example.cinescope.presentation.models.HomeSection
import com.example.cinescope.presentation.models.MediaPoster
import com.example.cinescope.presentation.models.MovieDateChip
import com.example.cinescope.presentation.models.MovieDetailData
import com.example.cinescope.presentation.models.MovieSession
import com.example.cinescope.presentation.models.MovieTab
import com.example.cinescope.presentation.models.PosterTheme
import com.example.cinescope.presentation.models.ReviewItem
import java.time.Duration
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

@Singleton
class EventRepository @Inject constructor(
    private val eventApiService: EventApiService
) {
    suspend fun getPosterSections(): List<HomeSection> {
        return posterTypes.mapNotNull { section ->
            val events = eventApiService.getEvents(type = section.type, skip = 0, limit = 20)
            if (events.isEmpty()) {
                null
            } else {
                HomeSection(
                    title = section.title,
                    items = events.map(::mapCardToPoster)
                )
            }
        }
    }

    suspend fun getCinemaEventDetail(eventId: String): MovieDetailData = coroutineScope {
        val detail = eventApiService.getEventDetail(eventId)
        val seatsBySession = loadAvailableSeats(detail.sessions)
        val summaryDeferred = async { loadCinemaReviewsSummary(detail) }
        val reviewsDeferred = async { loadCinemaReviews(detail) }
        val summary = summaryDeferred.await()
        val reviews = reviewsDeferred.await()
        val rating = summary?.average_rating ?: detail.average_rating

        MovieDetailData(
            title = detail.title,
            genres = listOfNotNull(detail.category?.name, detail.type.toDisplayType()).ifEmpty { listOf("Cinema") },
            duration = detail.sessions.firstOrNull()?.durationLabel() ?: "Upcoming",
            rating = rating.formatRating(),
            reviewCount = summary?.reviews_count?.toReviewCount() ?: "0 Reviews",
            tabs = listOf(MovieTab.Tickets, MovieTab.About, MovieTab.Comments),
            dates = detail.sessions.toDateChips(),
            sessions = detail.sessions.map { session -> session.toMovieSession(seatsBySession[session.id].orEmpty()) },
            description = detail.description.orEmpty(),
            cast = emptyList(),
            details = detail.toDetails(),
            reviews = rating.toReviewBars(),
            comments = reviews.map { it.text },
            theme = detail.type.toPosterTheme()
        )
    }

    suspend fun getEventDetail(eventId: String): EventDetailData = coroutineScope {
        val detail = eventApiService.getEventDetail(eventId)
        val seatsBySession = loadAvailableSeats(detail.sessions)

        EventDetailData(
            screenTitle = detail.type.toDisplayType(),
            badge = detail.type.toDisplayType().uppercase(Locale.ENGLISH),
            ageLabel = detail.city ?: "Event",
            title = detail.title,
            accentTitle = detail.category?.name ?: detail.type.toDisplayType(),
            venue = detail.venueLabel(),
            description = detail.description.orEmpty(),
            confirmLabel = "Confirm Booking",
            dates = detail.sessions.toDateChips(),
            sessions = detail.sessions.map { session -> session.toMovieSession(seatsBySession[session.id].orEmpty()) },
            theme = detail.type.toPosterTheme()
        )
    }

    private suspend fun loadAvailableSeats(
        sessions: List<EventSessionDto>
    ): Map<String, List<EventSeatDto>> = coroutineScope {
        val deferredSeats = sessions.map { session ->
            session.id to async {
                runCatching {
                    eventApiService.getSessionSeats(session.id, onlyAvailable = true)
                }.getOrDefault(emptyList())
            }
        }
        val result = mutableMapOf<String, List<EventSeatDto>>()
        for ((sessionId, seats) in deferredSeats) {
            result[sessionId] = seats.await()
        }
        result
    }

    private suspend fun loadCinemaReviewsSummary(detail: EventDetailDto): EventReviewsSummaryDto? {
        if (detail.type != "cinema") return null
        return runCatching { eventApiService.getReviewsSummary(detail.id) }.getOrNull()
    }

    private suspend fun loadCinemaReviews(detail: EventDetailDto): List<EventReviewDto> {
        if (detail.type != "cinema") return emptyList()
        return runCatching { eventApiService.getReviews(detail.id, skip = 0, limit = 20) }.getOrDefault(emptyList())
    }

    private fun mapCardToPoster(event: EventCardDto): MediaPoster {
        return MediaPoster(
            id = event.id,
            title = event.title,
            subtitle = listOfNotNull(event.category?.name, event.city).ifEmpty { listOf(event.type.toDisplayType()) }
                .joinToString(" - "),
            meta = when {
                event.type == "cinema" -> event.average_rating.formatRating()
                event.available_seats != null -> "${event.available_seats} seats"
                event.min_price != null -> "from ${event.min_price.formatPrice()}"
                event.price != null -> event.price.formatPrice()
                else -> event.next_session_at?.toDateTime()?.format(cardDateFormatter).orEmpty()
            },
            theme = event.type.toPosterTheme()
        )
    }

    private fun EventDetailDto.toDetails(): List<Pair<String, String>> {
        return listOfNotNull(
            category?.name?.let { "Category" to it },
            city?.let { "City" to it },
            venueLabel().takeIf { it.isNotBlank() }?.let { "Venue" to it },
            sessions.firstOrNull()?.starts_at?.toDateTime()?.format(detailDateFormatter)?.let { "First session" to it }
        )
    }

    private fun EventDetailDto.venueLabel(): String {
        val venueName = venue?.name ?: venue?.title
        val address = venue?.address
        return listOfNotNull(venueName, address, city).distinct().joinToString(", ").ifBlank { city.orEmpty() }
    }

    private fun EventSessionDto.toMovieSession(availableSeats: List<EventSeatDto>): MovieSession {
        val startsAt = starts_at.toDateTime()
        val seatsCount = availableSeats.size.takeIf { it > 0 } ?: seats.count { it.is_available }
        val status = if (seatsCount > 0) "$seatsCount Seats Left" else "Sold out"
        val hall = hall_name ?: cinema_name ?: "General admission"
        val price = availableSeats.minOfOrNull { it.price ?: base_price ?: 0.0 }
            ?.takeIf { it > 0.0 }
            ?: base_price

        return MovieSession(
            time = startsAt?.format(timeFormatter) ?: starts_at,
            hall = hall,
            status = status,
            price = price?.formatPrice() ?: "Free",
            soldOut = seatsCount == 0
        )
    }

    private fun EventSessionDto.durationLabel(): String? {
        val start = starts_at.toDateTime() ?: return null
        val end = ends_at?.toDateTime() ?: return null
        val minutes = Duration.between(start, end).toMinutes().takeIf { it > 0 } ?: return null
        val hours = minutes / 60
        val remaining = minutes % 60
        return if (hours > 0) "${hours}h ${remaining}m" else "${minutes}m"
    }

    private fun List<EventSessionDto>.toDateChips(): List<MovieDateChip> {
        return mapNotNull { it.starts_at.toDateTime() }
            .distinctBy { it.toLocalDate() }
            .mapIndexed { index, dateTime ->
                MovieDateChip(
                    day = dateTime.format(dayFormatter),
                    date = dateTime.format(dateFormatter),
                    selected = index == 0
                )
            }
    }

    private fun String.toDisplayType(): String = when (this) {
        "cinema" -> "Cinema"
        "concerts" -> "Concerts"
        "sports" -> "Sports"
        "stand-up" -> "Stand-Up"
        "kids" -> "Kids"
        "events" -> "Events"
        else -> replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ENGLISH) else it.toString() }
    }

    private fun String.toPosterTheme(): PosterTheme = when (this) {
        "cinema" -> PosterTheme.VioletPop
        "concerts" -> PosterTheme.CrimsonNight
        "stand-up" -> PosterTheme.MonoSmoke
        "kids" -> PosterTheme.SoftAmber
        "sports" -> PosterTheme.CobaltRush
        else -> PosterTheme.GoldenStage
    }

    private fun Double.formatRating(): String = String.format(Locale.ENGLISH, "%.1f", this)

    private fun Double.formatPrice(): String {
        val whole = toLong()
        val value = if (this == whole.toDouble()) whole.toString() else String.format(Locale.ENGLISH, "%.2f", this)
        return "$value KGS"
    }

    private fun Int.toReviewCount(): String = when (this) {
        1 -> "1 Review"
        else -> "$this Reviews"
    }

    private fun Double.toReviewBars(): List<ReviewItem> {
        val normalized = (this / 10.0).toFloat().coerceIn(0f, 1f)
        return listOf(
            ReviewItem("5", normalized),
            ReviewItem("4", (normalized * 0.72f).coerceIn(0f, 1f)),
            ReviewItem("3", (normalized * 0.36f).coerceIn(0f, 1f)),
            ReviewItem("2", (normalized * 0.12f).coerceIn(0f, 1f)),
            ReviewItem("1", 0.05f)
        )
    }

    private fun String.toDateTime(): LocalDateTime? {
        return runCatching { LocalDateTime.parse(this) }.getOrElse {
            runCatching { OffsetDateTime.parse(this).atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime() }
                .getOrNull()
        }
    }

    private data class PosterSectionType(val type: String, val title: String)

    private companion object {
        val posterTypes = listOf(
            PosterSectionType("cinema", "Cinema"),
            PosterSectionType("concerts", "Concerts"),
            PosterSectionType("stand-up", "Stand-Up")
        )
        val dayFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("EEE", Locale.ENGLISH)
        val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd MMM", Locale.ENGLISH)
        val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH)
        val cardDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd MMM, HH:mm", Locale.ENGLISH)
        val detailDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm", Locale.ENGLISH)
    }
}
