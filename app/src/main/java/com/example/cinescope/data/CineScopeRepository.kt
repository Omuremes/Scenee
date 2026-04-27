package com.example.cinescope.data

import androidx.compose.ui.graphics.Color
import com.example.cinescope.data.local.SessionManager
import com.example.cinescope.data.remote.AuthApiService
import com.example.cinescope.data.remote.dto.LoginRequest
import com.example.cinescope.data.remote.dto.RegisterRequest
import com.example.cinescope.data.remote.dto.UserDto
import com.example.cinescope.presentation.models.*
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CineScopeRepository @Inject constructor(
    private val authApiService: AuthApiService,
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

    fun loadInitialState(): CineScopeUiState {
        return CineScopeUiState(
            homeSections = listOf(
                HomeSection(
                    title = "Cinema",
                    items = listOf(
                        MediaPoster("Odyssey Horizon", "Sci-Fi • 2h 14m", "8.4", PosterTheme.CrimsonNight),
                        MediaPoster("The Director's Cut", "Drama • 1h 58m", "9.1", PosterTheme.GoldenStage),
                        MediaPoster("Neon Pursuit", "Action • 2h 05m", "7.8", PosterTheme.CobaltRush)
                    )
                ),
                HomeSection(
                    title = "Concerts",
                    items = listOf(
                        MediaPoster("Electric Pulse Tour", "The Synthetics", "Oct 12 • City Arena", PosterTheme.UltraBlue),
                        MediaPoster("Acoustic Sessions", "Elena Rivers", "Nov 05 • Symphony Hall", PosterTheme.SoftAmber)
                    )
                ),
                HomeSection(
                    title = "Stand-Up",
                    items = listOf(
                        MediaPoster("Laughs & Lore", "Marcus Webb", "\"Unfiltered and hilarious.\"", PosterTheme.MonoSmoke),
                        MediaPoster("Modern Problems", "Sarah Jenkins", "\"A fresh take on daily life.\"", PosterTheme.VioletPop)
                    )
                )
            ),
            categories = listOf(
                HomeCategory("Cinema", CategoryIcon.Movie, true),
                HomeCategory("Series", CategoryIcon.Series, false),
                HomeCategory("Concerts", CategoryIcon.Music, false),
                HomeCategory("Stand-Up", CategoryIcon.Mic, false),
                HomeCategory("Kids", CategoryIcon.Kids, false),
                HomeCategory("Events", CategoryIcon.Stadium, false)
            ),
            seriesSections = listOf(
                SeriesSection(
                    title = "Trending Series",
                    items = listOf(
                        SeriesPoster("Neon Horizon", "Sci-Fi • Thriller", "8.9", PosterTheme.CrimsonNight),
                        SeriesPoster("The Crown Legacy", "Drama • History", "8.5", PosterTheme.SoftAmber),
                        SeriesPoster("Midnight Echoes", "Mystery • Crime", "9.2", PosterTheme.MonoSmoke),
                        SeriesPoster("Velocity Prime", "Action • Adventure", "8.1", PosterTheme.UltraBlue)
                    )
                ),
                SeriesSection(
                    title = "New Releases",
                    items = listOf(
                        SeriesPoster("Aetheria", "Fantasy • Epic", "7.8", PosterTheme.VioletPop),
                        SeriesPoster("Summer in Seoul", "Romance • Comedy", "8.3", PosterTheme.SoftAmber),
                        SeriesPoster("Monolith", "Psychological • Horror", "8.7", PosterTheme.MonoSmoke),
                        SeriesPoster("Ghost Protocol", "Cyberpunk • Sci-Fi", "9.0", PosterTheme.CobaltRush)
                    )
                )
            ),
            ticketTabs = listOf("Upcoming", "Past"),
            tickets = listOf(
                TicketSummary(
                    title = "Odyssey Horizon",
                    category = "Cinema",
                    dateTime = "Fri, Apr 17 • 20:00",
                    venue = "Grand Cinema • Hall A",
                    accent = Color(0xFFE50914),
                    posterTheme = PosterTheme.CrimsonNight
                ),
                TicketSummary(
                    title = "Electric Pulse Tour",
                    category = "Concert",
                    dateTime = "Sat, Apr 18 • 22:30",
                    venue = "City Arena • Main Stage",
                    accent = Color(0xFF2563EB),
                    posterTheme = PosterTheme.UltraBlue
                ),
                TicketSummary(
                    title = "Midnight Laughs",
                    category = "Stand-Up",
                    dateTime = "Sun, Apr 19 • 21:00",
                    venue = "The Laugh Factory • Room 2",
                    accent = Color(0xFF3F3F46),
                    posterTheme = PosterTheme.MonoSmoke
                )
            ),
            profileSummary = ProfileSummary(
                name = "Alex Johnson",
                email = "alex.j@example.com",
                actions = listOf(
                    ProfileAction("Personal Info", CategoryIcon.Person),
                    ProfileAction("Payment Methods", CategoryIcon.Payments),
                    ProfileAction("Order History", CategoryIcon.History)
                )
            ),
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
                    MovieSession("14:30", "Main Floor", "112 Seats Left", "$45.00", false, false),
                    MovieSession("19:00", "VIP Front", "45 Seats Left", "$85.00", true, false),
                    MovieSession("22:45", "Gallery", "80 Seats Left", "$55.00", false, false)
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
                    MovieSession("21:00", "Hall B", "112 Seats Left", "$35.00", false, false),
                    MovieSession("23:30", "Hall A", "45 Seats Left", "$45.00", true, false),
                    MovieSession("01:00", "Lounge", "80 Seats Left", "$40.00", false, false)
                ),
                theme = PosterTheme.MonoSmoke
            ),
            movieDetail = MovieDetailData(
                title = "Odyssey Horizon",
                genres = listOf("Sci-Fi", "Action"),
                duration = "2h 14m",
                rating = "8.7",
                tabs = listOf(MovieTab.Tickets, MovieTab.About, MovieTab.Comments),
                dates = listOf(
                    MovieDateChip("Thu", "16", false),
                    MovieDateChip("Fri", "17", true),
                    MovieDateChip("Sat", "18", false),
                    MovieDateChip("Sun", "19", false),
                    MovieDateChip("Mon", "20", false)
                ),
                sessions = listOf(
                    MovieSession("14:30", "Hall B", "112 Seats Left", "$15.00", false, false),
                    MovieSession("17:15", "IMAX 3D", "45 Seats Left", "$22.50", true, false),
                    MovieSession("20:00", "Hall A", "SOLD OUT", "$15.00", false, true),
                    MovieSession("22:45", "Hall C", "80 Seats Left", "$15.00", false, false)
                ),
                description = "In the year 2145, humanity's last hope rests on a rogue crew of scientists and explorers as they venture into a mysterious horizon at the edge of the galaxy. Odyssey Horizon explores the thin line between reality and the unknown, blending breathtaking visuals with a deeply emotional journey into what it means to be human in an infinite universe.",
                cast = listOf("Julian Thorne", "Elena Vance", "Markus Reeds", "Sarah Croft"),
                details = listOf(
                    "Movie" to "Odyssey Horizon",
                    "Duration" to "2h 14m",
                    "Premiere" to "March 15, 2024",
                    "Production" to "Nova Studios",
                    "Producer" to "Christopher Nolan"
                ),
                reviews = listOf(
                    ReviewItem("5", 0.75f),
                    ReviewItem("4", 0.15f),
                    ReviewItem("3", 0.06f),
                    ReviewItem("2", 0.02f),
                    ReviewItem("1", 0.02f)
                )
            ),
            seriesDetail = SeriesDetailData(
                title = "Neon Genesis",
                genres = listOf("Sci-Fi", "Cyberpunk", "2 Seasons"),
                storyline = "In a world where digital consciousness can be harvested, a renegade data-thief uncovers a conspiracy that spans the floating cities of Neo-Tokyo. As the lines between human and machine blur, the survival of the species rests on a single encrypted code.",
                rating = "4.8",
                reviewCount = "12k Reviews",
                cast = listOf("Marcus Vane", "Sarah Chen", "David Rossi", "Aria Thorne", "Julian Black"),
                reviews = listOf(
                    "The visual direction of this show is unmatched. I haven't seen world-building this detailed in years.",
                    "Brilliant performance by Sarah Chen. Her character arc is so compelling and the pacing keeps you on your toes.",
                    "Cyberpunk at its best. It actually tries to say something meaningful about technology."
                ),
                seasons = listOf("Season 1", "Season 2", "Season 3", "Season 4"),
                episodes = listOf(
                    EpisodeItem("EPISODE 1", "The Beginning", "45m", PosterTheme.CrimsonNight),
                    EpisodeItem("EPISODE 2", "Celestial Rift", "48m", PosterTheme.UltraBlue),
                    EpisodeItem("EPISODE 3", "The Synthesis", "52m", PosterTheme.CobaltRush),
                    EpisodeItem("EPISODE 4", "Horizon's Edge", "45m", PosterTheme.VioletPop),
                    EpisodeItem("EPISODE 5", "Parallel Echoes", "50m", PosterTheme.MonoSmoke)
                )
            )
        )
    }
}
