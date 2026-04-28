package com.example.cinescope.data

import androidx.compose.ui.graphics.Color
import com.example.cinescope.data.local.SessionManager
import com.example.cinescope.data.remote.AuthApiService
import com.example.cinescope.data.remote.MovieApiService
import com.example.cinescope.data.remote.dto.LoginRequest
import com.example.cinescope.data.remote.dto.RegisterRequest
import com.example.cinescope.data.remote.dto.UserDto
import com.example.cinescope.data.remote.dto.MovieDto
import com.example.cinescope.presentation.models.*
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
                        MediaPoster("1", "Odyssey Horizon", "Sci-Fi • 2h 14m", "8.4", PosterTheme.VioletPop),
                        MediaPoster("2", "The Director's Cut", "Drama • 1h 58m", "9.1", PosterTheme.CrimsonNight),
                        MediaPoster("3", "Neon Pursuit", "Action • 2h 05m", "7.8", PosterTheme.MonoSmoke)
                    )
                ),
                HomeSection(
                    "Concerts",
                    listOf(
                        MediaPoster("4", "Electric Pulse Tour", "The Synthetics", "Oct 12 • City Arena", PosterTheme.VioletPop),
                        MediaPoster("5", "Acoustic Sessions", "Elena Rivers", "Nov 05 • Symphony Hall", PosterTheme.CrimsonNight)
                    )
                ),
                HomeSection(
                    "Stand-Up",
                    listOf(
                        MediaPoster("6", "Laughs & Lore", "Marcus Webb", "\"Unfiltered and hilarious.\"", PosterTheme.MonoSmoke),
                        MediaPoster("7", "Modern Problems", "Sarah Jenkins", "\"A fresh take on daily life.\"", PosterTheme.VioletPop)
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
                badge = "LIVE CONCERT", 
                ageLabel = "16+", 
                title = "Electric Pulse Tour", 
                accentTitle = "THE SYNTHETICS", 
                venue = "City Arena", 
                confirmLabel = "Book Tickets",
                dates = listOf(
                    MovieDateChip("Mon", "12 Oct", true),
                    MovieDateChip("Tue", "13 Oct", false),
                    MovieDateChip("Wed", "14 Oct", false)
                ),
                sessions = listOf(
                    MovieSession("19:30", "Main Hall", "Fast Selling", "from $85", true),
                    MovieSession("22:00", "Main Hall", "Available", "$75", false)
                ),
                theme = PosterTheme.VioletPop
            ),
            standupDetail = EventDetailData(
                screenTitle = "Stand-Up", 
                badge = "STAND-UP SHOW", 
                ageLabel = "18+", 
                title = "Laughs & Lore", 
                accentTitle = "MARCUS WEBB", 
                venue = "Comedy Club", 
                confirmLabel = "Get Tickets",
                dates = listOf(
                    MovieDateChip("Tue", "20 Oct", true),
                    MovieDateChip("Wed", "21 Oct", false)
                ),
                sessions = listOf(
                    MovieSession("20:00", "Stage A", "Limited", "$45", true),
                    MovieSession("22:30", "Stage A", "Available", "$40", false)
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
