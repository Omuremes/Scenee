package com.example.cinescope.data

import androidx.compose.ui.graphics.Color
import com.example.cinescope.data.local.SessionManager
import com.example.cinescope.data.remote.AuthApiService
import com.example.cinescope.data.remote.MovieApiService
import com.example.cinescope.data.remote.dto.LoginRequest
import com.example.cinescope.data.remote.dto.MovieDto
import com.example.cinescope.data.remote.dto.RegisterRequest
import com.example.cinescope.data.remote.dto.UserDto
import com.example.cinescope.presentation.models.CategoryIcon
import com.example.cinescope.presentation.models.CineScopeUiState
import com.example.cinescope.presentation.models.EpisodeItem
import com.example.cinescope.presentation.models.EventDetailData
import com.example.cinescope.presentation.models.HomeCategory
import com.example.cinescope.presentation.models.HomeSection
import com.example.cinescope.presentation.models.MediaPoster
import com.example.cinescope.presentation.models.MovieDateChip
import com.example.cinescope.presentation.models.MovieDetailData
import com.example.cinescope.presentation.models.MovieSession
import com.example.cinescope.presentation.models.MovieTab
import com.example.cinescope.presentation.models.PosterTheme
import com.example.cinescope.presentation.models.ProfileSummary
import com.example.cinescope.presentation.models.ReviewItem
import com.example.cinescope.presentation.models.SeriesDetailData
import com.example.cinescope.presentation.models.SeriesPoster
import com.example.cinescope.presentation.models.SeriesSection
import com.example.cinescope.presentation.models.TicketSummary
import com.example.cinescope.ui.theme.Crimson
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CineScopeRepository @Inject constructor(
    private val authApiService: AuthApiService,
    private val movieApiService: MovieApiService,
    private val sessionManager: SessionManager
) {
    suspend fun register(request: RegisterRequest) = authApiService.register(request)
    suspend fun login(request: LoginRequest) = authApiService.login(request)

    suspend fun getMe(): UserDto {
        val token = sessionManager.authToken.first() ?: throw Exception("Not authenticated")
        return authApiService.getMe("Bearer $token")
    }

    suspend fun saveToken(token: String) = sessionManager.saveAuthToken(token)
    fun getAuthToken() = sessionManager.authToken
    suspend fun logout() = sessionManager.clearSession()

    suspend fun getPopularMovies(): List<MovieDto> = movieApiService.getPopularMovies()
    suspend fun getNewMovies(): List<MovieDto> = movieApiService.getNewMovies()
    suspend fun getMovieDetail(id: String) = movieApiService.getMovieDetail(id)

    fun mapToSeriesPoster(dto: MovieDto): SeriesPoster {
        val genres = dto.categories.joinToString(" • ") { it.name }
        return SeriesPoster(
            id = dto.id,
            title = dto.name,
            genre = genres,
            rating = String.format("%.1f", dto.average_rating),
            theme = PosterTheme.VioletPop
        )
    }

    fun loadInitialState(
        isAuthenticated: Boolean = false,
        popularSeries: List<SeriesPoster> = emptyList(),
        newSeries: List<SeriesPoster> = emptyList()
    ): CineScopeUiState {
        return CineScopeUiState(
            isAuthenticated = isAuthenticated,
            homeSections = listOf(
                HomeSection(
                    "Cinema",
                    listOf(
                        MediaPoster("1", "Stellar Odyssey", "SCI-FI • 2H 14M", "8.4", PosterTheme.VioletPop),
                        MediaPoster("2", "Neon Echoes", "THRILLER • 1H 52M", "7.9", PosterTheme.CrimsonNight),
                        MediaPoster("3", "The Last Canvas", "DRAMA • 2H 05M", "9.1", PosterTheme.MonoSmoke),
                        MediaPoster("8", "Frame by Frame", "DOCUMENTARY • 1H 38M", "8.2", PosterTheme.SoftAmber)
                    )
                ),
                HomeSection(
                    "Concerts",
                    listOf(
                        MediaPoster("4", "Neon Dreams Tour", "ELECTRONIC POP • 2H 45M", "9.5", PosterTheme.VioletPop),
                        MediaPoster("5", "Electric Pulse", "TECHNO HOUSE • 3H 20M", "9.2", PosterTheme.CrimsonNight),
                        MediaPoster("9", "Vocal Legends", "CLASSIC JAZZ • 2H 15M", "9.8", PosterTheme.SoftAmber),
                        MediaPoster("10", "Midnight Rebellion", "ALT ROCK • 1H 50M", "8.9", PosterTheme.MonoSmoke)
                    )
                ),
                HomeSection(
                    "Stand-Up",
                    listOf(
                        MediaPoster("6", "Midnight Laughs", "IMPROV COMEDY • 1H 15M", "9.5", PosterTheme.MonoSmoke),
                        MediaPoster("7", "Comedy Central Live", "STAND-UP SPECIAL • 1H 45M", "9.2", PosterTheme.VioletPop),
                        MediaPoster("11", "Laugh Out Loud", "OBSERVATIONAL • 1H 30M", "9.8", PosterTheme.CrimsonNight),
                        MediaPoster("12", "The Punchline Session", "STORYTELLING • 2H 00M", "8.9", PosterTheme.SoftAmber)
                    )
                )
            ),
            categories = listOf(
                HomeCategory("Cinema", CategoryIcon.Movie, true),
                HomeCategory("Series", CategoryIcon.Series, false),
                HomeCategory("Concerts", CategoryIcon.Music, false),
                HomeCategory("Stand-Up", CategoryIcon.Mic, false),
                HomeCategory("Kids", CategoryIcon.Child, false),
                HomeCategory("Events", CategoryIcon.Stadium, false)
            ),
            seriesSections = listOf(
                SeriesSection("Trending Series", popularSeries),
                SeriesSection("New Releases", newSeries)
            ),
            ticketTabs = listOf("ALL", "CINEMA", "CONCERTS"),
            tickets = listOf(
                TicketSummary("Acoustic Sessions", "CONCERTS", "Nov 05, 2026 • 20:00", "Symphony Hall", Crimson, PosterTheme.CrimsonNight),
                TicketSummary("Midnight Echoes", "CINEMA", "Oct 15, 2026 • 21:30", "Grand Cinema", Color(0xFF9333EA), PosterTheme.VioletPop)
            ),
            profileSummary = ProfileSummary("Alex Johnson", "alex.johnson@example.com", "AJ"),
            concertDetail = EventDetailData(
                screenTitle = "Concert",
                badge = "CONCERT",
                ageLabel = "18+",
                title = "The Synthetics:",
                accentTitle = "Electric Pulse Tour",
                venue = "City Arena, Metropolis",
                confirmLabel = "Confirm Booking",
                dates = listOf(
                    MovieDateChip("Thu", "16", false),
                    MovieDateChip("Fri", "17", true),
                    MovieDateChip("Sat", "18", false),
                    MovieDateChip("Sun", "19", false),
                    MovieDateChip("Mon", "20", false)
                ),
                sessions = listOf(
                    MovieSession("14:30", "Hall B", "112 Seats Left", "$45.00", false),
                    MovieSession("19:00", "Hall A", "45 Seats Left", "$85.00", true),
                    MovieSession("22:45", "Hall C", "80 Seats Left", "$55.00", false)
                ),
                theme = PosterTheme.VioletPop
            ),
            standupDetail = EventDetailData(
                screenTitle = "Stand-Up",
                badge = "STAND-UP",
                ageLabel = "18+",
                title = "Midnight Laughs:",
                accentTitle = "Live & Uncut",
                venue = "The Laugh Factory, Metropolis",
                confirmLabel = "Confirm Selection",
                dates = listOf(
                    MovieDateChip("Thu", "16", false),
                    MovieDateChip("Fri", "17", true),
                    MovieDateChip("Sat", "18", false),
                    MovieDateChip("Sun", "19", false),
                    MovieDateChip("Mon", "20", false)
                ),
                sessions = listOf(
                    MovieSession("21:00", "Hall B", "112 Seats Left", "$35.00", false),
                    MovieSession("23:30", "Hall A", "45 Seats Left", "$45.00", true),
                    MovieSession("01:00", "Lounge", "80 Seats Left", "$40.00", false)
                ),
                theme = PosterTheme.MonoSmoke
            ),
            movieDetail = MovieDetailData(
                title = "Odyssey Horizon",
                genres = listOf("Sci-Fi", "Action"),
                duration = "2h 14m",
                rating = "8.4",
                tabs = listOf(MovieTab.Tickets, MovieTab.About, MovieTab.Comments),
                dates = listOf(
                    MovieDateChip("Wed", "27 Apr", true),
                    MovieDateChip("Thu", "28 Apr", false),
                    MovieDateChip("Fri", "29 Apr", false)
                ),
                sessions = listOf(
                    MovieSession("10:00", "Hall 1", "Available", "$12", false),
                    MovieSession("13:30", "Hall 4", "Fast Selling", "$15", true),
                    MovieSession("17:00", "Hall 2", "Sold Out", "$15", false, true)
                ),
                description = "In a future where humanity has reached the stars, one pilot must navigate a galactic conspiracy that threatens to rewrite history itself.",
                cast = listOf("John Doe", "Jane Smith", "Robert Brown", "Sarah White"),
                details = listOf(
                    "Director" to "James Cameron",
                    "Release" to "Oct 2026",
                    "Budget" to "$150M"
                ),
                reviews = listOf(
                    ReviewItem("5", 0.8f),
                    ReviewItem("4", 0.6f),
                    ReviewItem("3", 0.3f),
                    ReviewItem("2", 0.1f),
                    ReviewItem("1", 0.05f)
                ),
                theme = PosterTheme.VioletPop
            ),
            seriesDetail = SeriesDetailData(
                title = "Neon Horizon",
                genres = listOf("Action", "Sci-Fi", "Thriller"),
                storyline = "In a neon-drenched future, a group of outcasts uncovers a truth that could change their world forever.",
                rating = "4.9",
                reviewCount = "2.4k Reviews",
                cast = listOf("Actor 1", "Actor 2", "Actor 3"),
                reviews = listOf("Amazing show!", "Best sci-fi in years.", "Can't wait for season 3!"),
                seasons = listOf("Season 1", "Season 2"),
                episodes = listOf(
                    EpisodeItem("EPISODE 1", "The Awakening", "42m", PosterTheme.VioletPop),
                    EpisodeItem("EPISODE 2", "Neon Streets", "45m", PosterTheme.CrimsonNight),
                    EpisodeItem("EPISODE 3", "System Crash", "38m", PosterTheme.MonoSmoke),
                    EpisodeItem("EPISODE 4", "Horizon's Edge", "45m", PosterTheme.VioletPop),
                    EpisodeItem("EPISODE 5", "Parallel Echoes", "50m", PosterTheme.MonoSmoke)
                ),
                tabs = listOf("Seasons", "Episodes", "Reviews"),
                meta = listOf("2026", "2 Seasons", "18+")
            )
        )
    }
}
