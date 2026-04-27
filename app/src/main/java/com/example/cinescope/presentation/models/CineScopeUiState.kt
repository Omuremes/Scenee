package com.example.cinescope.presentation.models

import androidx.compose.ui.graphics.Color

data class CineScopeUiState(
    val isAuthenticated: Boolean = false,
    val homeSections: List<HomeSection>,
    val categories: List<HomeCategory>,
    val seriesSections: List<SeriesSection>,
    val ticketTabs: List<String>,
    val tickets: List<TicketSummary>,
    val profileSummary: ProfileSummary,
    val concertDetail: EventDetailData,
    val standupDetail: EventDetailData,
    val movieDetail: MovieDetailData,
    val seriesDetail: SeriesDetailData
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
    Movie, Series, Music, Mic, Kids, Stadium, Person, Payments, History
}

data class ProfileSummary(
    val name: String,
    val email: String,
    val actions: List<ProfileAction>
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
    val selected: Boolean,
    val soldOut: Boolean
)

data class ReviewItem(val label: String, val progress: Float)

data class EventDetailData(
    val screenTitle: String,
    val badge: String,
    val ageLabel: String,
    val title: String,
    val accentTitle: String,
    val venue: String,
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
    val tabs: List<MovieTab>,
    val dates: List<MovieDateChip>,
    val sessions: List<MovieSession>,
    val description: String,
    val cast: List<String>,
    val details: List<Pair<String, String>>,
    val reviews: List<ReviewItem>
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
    val episodes: List<EpisodeItem>
)

data class HomeSection(
    val title: String,
    val items: List<MediaPoster>
)

data class MediaPoster(
    val title: String,
    val subtitle: String,
    val meta: String,
    val theme: PosterTheme
)

data class HomeCategory(
    val label: String,
    val icon: CategoryIcon,
    val selected: Boolean
)

data class SeriesSection(
    val title: String,
    val items: List<SeriesPoster>
)

data class SeriesPoster(
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
    val posterTheme: PosterTheme
)
