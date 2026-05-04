package com.example.cinescope.presentation.models

import androidx.compose.ui.graphics.Color

data class CineScopeUiState(
    val isAuthenticated: Boolean = false,
    val homeLoading: Boolean = false,
    val homeErrorMessage: String? = null,
    val homeSections: List<HomeSection> = emptyList(),
    val categories: List<HomeCategory> = emptyList(),
    val seriesSections: List<SeriesSection> = emptyList(),
    val ticketTabs: List<String> = emptyList(),
    val tickets: List<TicketSummary> = emptyList(),
    val profileSummary: ProfileSummary? = null,
    val concertDetail: EventDetailData? = null,
    val standupDetail: EventDetailData? = null,
    val movieDetail: MovieDetailData? = null,
    val seriesDetail: SeriesDetailData? = null
)

enum class PosterTheme(val start: Color, val end: Color) {
    CrimsonNight(Color(0xFFE50914), Color(0xFF5E0408)),
    GoldenStage(Color(0xFFFFD700), Color(0xFFB8860B)),
    CobaltRush(Color(0xFF2563EB), Color(0xFF1E3A8A)),
    UltraBlue(Color(0xFF0000FF), Color(0xFF00008B)),
    SoftAmber(Color(0xFFFBBF24), Color(0xFFD97706)),
    MonoSmoke(Color(0xFF3F3F46), Color(0xFF18181B)),
    VioletPop(Color(0xFF8B5CF6), Color(0xFF4C1D95))
}

enum class CategoryIcon {
    Movie, Series, Music, Mic, Child, Stadium, Person, Payments, History
}

data class ProfileSummary(
    val name: String,
    val email: String,
    val initials: String,
    val actions: List<ProfileAction> = emptyList()
)

data class ProfileAction(
    val title: String,
    val icon: CategoryIcon
)

enum class MovieTab { Tickets, About, Comments }

data class MovieDateChip(val day: String, val date: String, val selected: Boolean)

data class MovieSession(
    val time: String,
    val hall: String,
    val status: String,
    val price: String,
    val selected: Boolean = false,
    val soldOut: Boolean = false,
    val id: String? = null
)

data class ReviewItem(val label: String, val progress: Float)

data class EventDetailData(
    val screenTitle: String,
    val eventTypeCode: String = "",
    val badge: String,
    val ageLabel: String,
    val title: String,
    val accentTitle: String,
    val venue: String,
    val description: String,
    val confirmLabel: String,
    val dates: List<MovieDateChip>,
    val sessions: List<MovieSession>,
    val theme: PosterTheme
)

data class MovieDetailData(
    val title: String,
    val genres: List<String>,
    val duration: String,
    val rating: String,
    val reviewCount: String = "0 Reviews",
    val tabs: List<MovieTab>,
    val dates: List<MovieDateChip>,
    val sessions: List<MovieSession>,
    val description: String,
    val cast: List<String>,
    val details: List<Pair<String, String>>,
    val reviews: List<ReviewItem>,
    val comments: List<String> = emptyList(),
    val theme: PosterTheme = PosterTheme.VioletPop
)

data class EpisodeItem(
    val badge: String,
    val title: String,
    val duration: String,
    val theme: PosterTheme
)

data class SeriesDetailData(
    val title: String,
    val genres: List<String>,
    val storyline: String,
    val rating: String,
    val reviewCount: String,
    val cast: List<String>,
    val reviews: List<String>,
    val seasons: List<String>,
    val episodes: List<EpisodeItem>,
    val tabs: List<String> = emptyList(),
    val meta: List<String> = emptyList()
)

data class HomeSection(
    val title: String,
    val items: List<MediaPoster>
)

data class MediaPoster(
    val id: String = "",
    val title: String,
    val subtitle: String,
    val meta: String,
    val theme: PosterTheme
)

data class HomeCategory(
    val label: String,
    val icon: CategoryIcon,
    val selected: Boolean = false
)

data class SeriesSection(
    val title: String,
    val items: List<SeriesPoster>
)

data class SeriesPoster(
    val id: String = "",
    val title: String,
    val genre: String,
    val rating: String,
    val theme: PosterTheme
)

data class TicketSummary(
    val title: String,
    val category: String,
    val dateTime: String,
    val venue: String,
    val accent: Color,
    val posterTheme: PosterTheme,
    val id: String = "",
    val bookingReference: String = "",
    val status: String = "",
    val seatsCount: Int = 1,
    val totalPrice: String = "",
    val priceRange: String = "",
    val seatLabel: String = ""
)

data class BookingSelectionData(
    val eventId: String,
    val title: String,
    val eventTypeCode: String,
    val eventType: String,
    val venue: String,
    val availableSeats: Int? = null,
    val sessions: List<BookingSessionOption>,
    val seats: List<BookingSeatOption>,
    val selectedSessionId: String?,
    val theme: PosterTheme
)

data class BookingSessionOption(
    val id: String,
    val startsAt: String,
    val hall: String,
    val price: String,
    val basePrice: Double? = null,
    val selected: Boolean = false
)

data class BookingSeatOption(
    val id: String,
    val label: String,
    val zone: String,
    val price: String,
    val rawPrice: Double? = null,
    val available: Boolean
)
