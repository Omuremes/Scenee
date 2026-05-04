package com.example.cinescope.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ListAlt
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.cinescope.presentation.auth.AuthScreen
import com.example.cinescope.presentation.booking.BookingScreen
import com.example.cinescope.presentation.booking.BookingViewModel
import com.example.cinescope.presentation.catalog.ConcertsCatalogScreen
import com.example.cinescope.presentation.catalog.EventsCatalogScreen
import com.example.cinescope.presentation.catalog.KidsCatalogScreen
import com.example.cinescope.presentation.catalog.MoviesCatalogScreen
import com.example.cinescope.presentation.catalog.StandupCatalogScreen
import com.example.cinescope.presentation.details.*
import com.example.cinescope.presentation.home.HomeScreen
import com.example.cinescope.presentation.models.CineScopeUiState
import com.example.cinescope.presentation.profile.ProfileScreen
import com.example.cinescope.presentation.series.SeriesErrorScreen
import com.example.cinescope.presentation.series.SeriesLoadingScreen
import com.example.cinescope.presentation.series.SeriesScreen
import com.example.cinescope.presentation.series.SeriesUiState
import com.example.cinescope.presentation.series.SeriesViewModel
import com.example.cinescope.presentation.tickets.TicketsScreen
import com.example.cinescope.presentation.tickets.TicketsViewModel

sealed class BottomNavRoute(val route: String, val label: String) {
    data object Home : BottomNavRoute("home", "Poster")
    data object Series : BottomNavRoute("series", "Series")
    data object Tickets : BottomNavRoute("tickets", "Tickets")
    data object Profile : BottomNavRoute("profile", "Profile")
}

sealed class AppRoute(val route: String) {
    data object Login : AppRoute("login")
    data object Signup : AppRoute("signup")
    data object Movies : AppRoute("movies")
    data object Concerts : AppRoute("concerts")
    data object Standup : AppRoute("standup")
    data object Kids : AppRoute("kids")
    data object Events : AppRoute("events")
    data object MovieDetail : AppRoute("movie_detail/{movieId}") {
        fun createRoute(id: String) = "movie_detail/$id"
    }
    data object ConcertDetail : AppRoute("concert_detail/{eventId}") {
        fun createRoute(id: String) = "concert_detail/$id"
    }
    data object StandupDetail : AppRoute("standup_detail/{eventId}") {
        fun createRoute(id: String) = "standup_detail/$id"
    }
    data object EventDetail : AppRoute("event_detail/{eventId}") {
        fun createRoute(id: String) = "event_detail/$id"
    }
    data object SeriesDetail : AppRoute("series_detail/{movieId}") {
        fun createRoute(id: String) = "series_detail/$id"
    }
    data object WatchSeries : AppRoute("watch_series/{movieId}") {
        fun createRoute(id: String) = "watch_series/$id"
    }
    data object EpisodePlayer : AppRoute("episode_player/{title}?videoUrl={videoUrl}") {
        fun createRoute(title: String, videoUrl: String): String {
            val encodedTitle = android.net.Uri.encode(title)
            val encodedUrl = android.net.Uri.encode(videoUrl)
            return "episode_player/$encodedTitle?videoUrl=$encodedUrl"
        }
    }
    data object BookSeats : AppRoute("book_seats/{eventId}?sessionId={sessionId}") {
        fun createRoute(eventId: String, sessionId: String?) = if (sessionId.isNullOrBlank()) {
            "book_seats/$eventId"
        } else {
            "book_seats/$eventId?sessionId=$sessionId"
        }
    }
}

val bottomNavRouteSet = setOf(
    BottomNavRoute.Home.route,
    BottomNavRoute.Series.route,
    BottomNavRoute.Tickets.route,
    BottomNavRoute.Profile.route
)

@Composable
fun CineScopeNavGraph(
    navController: NavHostController,
    appState: CineScopeUiState,
    startDestination: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(BottomNavRoute.Home.route) {
            HomeScreen(
                categories = appState.categories,
                sections = appState.homeSections,
                isLoading = appState.homeLoading,
                errorMessage = appState.homeErrorMessage,
                onRetry = onRetry,
                onMovieClick = { id -> navController.navigate(AppRoute.MovieDetail.createRoute(id)) },
                onConcertClick = { id -> navController.navigate(AppRoute.ConcertDetail.createRoute(id)) },
                onStandupClick = { id -> navController.navigate(AppRoute.StandupDetail.createRoute(id)) },
                onEventClick = { id -> navController.navigate(AppRoute.EventDetail.createRoute(id)) },
                onMoviesOpen = { navController.navigate(AppRoute.Movies.route) },
                onConcertsOpen = { navController.navigate(AppRoute.Concerts.route) },
                onStandupOpen = { navController.navigate(AppRoute.Standup.route) },
                onKidsOpen = { navController.navigate(AppRoute.Kids.route) },
                onEventsOpen = { navController.navigate(AppRoute.Events.route) },
                onSeriesOpen = { navController.navigateToBottomRoute(BottomNavRoute.Series.route) }
            )
        }
        composable(BottomNavRoute.Series.route) {
            val seriesViewModel: SeriesViewModel = hiltViewModel()
            val seriesState by seriesViewModel.uiState.collectAsState()

            when (val state = seriesState) {
                SeriesUiState.Loading -> SeriesLoadingScreen()
                is SeriesUiState.Success -> {
                    SeriesScreen(
                        sections = state.sections,
                        onSeriesClick = { id -> navController.navigate(AppRoute.SeriesDetail.createRoute(id)) }
                    )
                }
                is SeriesUiState.Error -> {
                    SeriesErrorScreen(
                        message = state.message,
                        onRetry = seriesViewModel::loadSeries
                    )
                }
            }
        }
        composable(BottomNavRoute.Tickets.route) {
            val ticketsViewModel: TicketsViewModel = hiltViewModel()
            val ticketsState by ticketsViewModel.uiState.collectAsState()

            LaunchedEffect(appState.isAuthenticated) {
                if (appState.isAuthenticated) {
                    ticketsViewModel.loadTickets()
                }
            }

            TicketsScreen(
                tabs = ticketsState.tabs,
                tickets = ticketsState.tickets,
                isAuthenticated = appState.isAuthenticated,
                isLoading = ticketsState.isLoading,
                errorMessage = ticketsState.errorMessage,
                cancellingBookingId = ticketsState.cancellingBookingId,
                onLoginClick = { navController.navigate(AppRoute.Login.route) },
                onRetry = ticketsViewModel::loadTickets,
                onCancelTicket = ticketsViewModel::cancelTicket
            )
        }
        composable(BottomNavRoute.Profile.route) {
            ProfileScreen(
                isAuthenticated = appState.isAuthenticated,
                onLoginClick = { navController.navigate(AppRoute.Login.route) }
            )
        }
        composable(AppRoute.Movies.route) {
            PosterCatalogContent(appState = appState, onRetry = onRetry) {
                MoviesCatalogScreen(
                    items = appState.homeSections.firstOrNull { it.title == "Cinema" }?.items.orEmpty(),
                    onMovieClick = { id -> navController.navigate(AppRoute.MovieDetail.createRoute(id)) }
                )
            }
        }
        composable(AppRoute.Concerts.route) {
            PosterCatalogContent(appState = appState, onRetry = onRetry) {
                ConcertsCatalogScreen(
                    items = appState.homeSections.firstOrNull { it.title == "Concerts" }?.items.orEmpty(),
                    onConcertClick = { id -> navController.navigate(AppRoute.ConcertDetail.createRoute(id)) }
                )
            }
        }
        composable(AppRoute.Standup.route) {
            PosterCatalogContent(appState = appState, onRetry = onRetry) {
                StandupCatalogScreen(
                    items = appState.homeSections.firstOrNull { it.title == "Stand-Up" }?.items.orEmpty(),
                    onStandupClick = { id -> navController.navigate(AppRoute.StandupDetail.createRoute(id)) }
                )
            }
        }
        composable(AppRoute.Kids.route) {
            PosterCatalogContent(appState = appState, onRetry = onRetry) {
                KidsCatalogScreen(
                    items = appState.homeSections.firstOrNull { it.title == "Kids" }?.items.orEmpty(),
                    onEventClick = { id -> navController.navigate(AppRoute.EventDetail.createRoute(id)) }
                )
            }
        }
        composable(AppRoute.Events.route) {
            PosterCatalogContent(appState = appState, onRetry = onRetry) {
                EventsCatalogScreen(
                    items = appState.homeSections.firstOrNull { it.title == "Events" }?.items.orEmpty(),
                    onEventClick = { id -> navController.navigate(AppRoute.EventDetail.createRoute(id)) }
                )
            }
        }
        composable(AppRoute.Login.route) {
            AuthScreen(
                isSignup = false,
                onBack = { navController.popBackStack() },
                onSwitch = {
                    navController.navigate(AppRoute.Signup.route)
                },
                onSuccess = { _ ->
                    navController.navigate(BottomNavRoute.Home.route) {
                        popUpTo(AppRoute.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(AppRoute.Signup.route) {
            AuthScreen(
                isSignup = true,
                onBack = { navController.popBackStack() },
                onSwitch = {
                    navController.navigate(AppRoute.Login.route)
                },
                onSuccess = { _ ->
                    navController.navigate(BottomNavRoute.Home.route) {
                        popUpTo(AppRoute.Signup.route) { inclusive = true }
                    }
                }
            )
        }
        composable(
            route = AppRoute.MovieDetail.route,
            arguments = listOf(navArgument("movieId") { type = NavType.StringType })
        ) { backStackEntry ->
            val movieId = backStackEntry.arguments?.getString("movieId") ?: ""
            val detailViewModel: DetailViewModel = hiltViewModel()
            val detailState by detailViewModel.uiState.collectAsState()
            var showAuthRequired by remember { mutableStateOf(false) }

            LaunchedEffect(movieId) {
                detailViewModel.loadCinemaEventDetail(movieId)
            }

            when (val state = detailState) {
                is DetailUiState.Loading -> SeriesLoadingScreen()
                is DetailUiState.SuccessMovie -> {
                    MovieDetailScreen(
                        data = state.data,
                        onBack = { navController.popBackStack() },
                        onBook = { sessionId ->
                            if (appState.isAuthenticated) {
                                navController.navigate(AppRoute.BookSeats.createRoute(movieId, sessionId))
                            } else {
                                showAuthRequired = true
                            }
                        }
                    )
                }
                is DetailUiState.SuccessSeries -> {
                    SeriesDetailScreen(
                        data = state.data,
                        onBack = { navController.popBackStack() },
                        onEpisodesClick = { navController.navigate(AppRoute.WatchSeries.createRoute(movieId)) }
                    )
                }
                is DetailUiState.SuccessEvent -> {
                    EventDetailScreen(
                        data = state.data,
                        onBack = { navController.popBackStack() },
                        onBook = { sessionId ->
                            if (appState.isAuthenticated) {
                                navController.navigate(AppRoute.BookSeats.createRoute(movieId, sessionId))
                            } else {
                                showAuthRequired = true
                            }
                        }
                    )
                }
                is DetailUiState.Error -> SeriesErrorScreen(
                    message = state.message,
                    onRetry = { detailViewModel.loadCinemaEventDetail(movieId) }
                )
            }
            if (showAuthRequired) {
                AuthRequiredDialog(
                    onDismiss = { showAuthRequired = false },
                    onLogin = {
                        showAuthRequired = false
                        navController.navigate(AppRoute.Login.route)
                    }
                )
            }
        }
        composable(
            route = AppRoute.SeriesDetail.route,
            arguments = listOf(navArgument("movieId") { type = NavType.StringType })
        ) { backStackEntry ->
            val movieId = backStackEntry.arguments?.getString("movieId") ?: ""
            val detailViewModel: DetailViewModel = hiltViewModel()
            val detailState by detailViewModel.uiState.collectAsState()

            LaunchedEffect(movieId) {
                detailViewModel.loadSeriesDetail(movieId)
            }

            when (val state = detailState) {
                is DetailUiState.Loading -> SeriesLoadingScreen()
                is DetailUiState.SuccessMovie -> {
                    MovieDetailScreen(
                        data = state.data,
                        onBack = { navController.popBackStack() },
                        onBook = { }
                    )
                }
                is DetailUiState.SuccessSeries -> {
                    SeriesDetailScreen(
                        data = state.data,
                        onBack = { navController.popBackStack() },
                        onEpisodesClick = { navController.navigate(AppRoute.WatchSeries.createRoute(movieId)) }
                    )
                }
                is DetailUiState.SuccessEvent -> EventDetailScreen(
                    data = state.data,
                    onBack = { navController.popBackStack() },
                    onBook = { }
                )
                is DetailUiState.Error -> SeriesErrorScreen(
                    message = state.message,
                    onRetry = { detailViewModel.loadSeriesDetail(movieId) }
                )
            }
        }
        composable(
            route = AppRoute.ConcertDetail.route,
            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            val detailViewModel: DetailViewModel = hiltViewModel()
            val detailState by detailViewModel.uiState.collectAsState()
            var showAuthRequired by remember { mutableStateOf(false) }

            LaunchedEffect(eventId) {
                detailViewModel.loadEventDetail(eventId)
            }

            when (val state = detailState) {
                is DetailUiState.Loading -> SeriesLoadingScreen()
                is DetailUiState.SuccessEvent -> {
                    EventDetailScreen(
                        data = state.data,
                        onBack = { navController.popBackStack() },
                        onBook = { sessionId ->
                            if (appState.isAuthenticated) {
                                navController.navigate(AppRoute.BookSeats.createRoute(eventId, sessionId))
                            } else {
                                showAuthRequired = true
                            }
                        }
                    )
                }
                is DetailUiState.Error -> SeriesErrorScreen(
                    message = state.message,
                    onRetry = { detailViewModel.loadEventDetail(eventId) }
                )
                is DetailUiState.SuccessMovie -> MovieDetailScreen(
                    data = state.data,
                    onBack = { navController.popBackStack() },
                    onBook = { sessionId ->
                        if (appState.isAuthenticated) {
                            navController.navigate(AppRoute.BookSeats.createRoute(eventId, sessionId))
                        } else {
                            showAuthRequired = true
                        }
                    }
                )
                is DetailUiState.SuccessSeries -> SeriesErrorScreen(
                    message = "Selected item is not an event.",
                    onRetry = { detailViewModel.loadEventDetail(eventId) }
                )
            }
            if (showAuthRequired) {
                AuthRequiredDialog(
                    onDismiss = { showAuthRequired = false },
                    onLogin = {
                        showAuthRequired = false
                        navController.navigate(AppRoute.Login.route)
                    }
                )
            }
        }
        composable(
            route = AppRoute.StandupDetail.route,
            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            val detailViewModel: DetailViewModel = hiltViewModel()
            val detailState by detailViewModel.uiState.collectAsState()
            var showAuthRequired by remember { mutableStateOf(false) }

            LaunchedEffect(eventId) {
                detailViewModel.loadEventDetail(eventId)
            }

            when (val state = detailState) {
                is DetailUiState.Loading -> SeriesLoadingScreen()
                is DetailUiState.SuccessEvent -> {
                    EventDetailScreen(
                        data = state.data,
                        onBack = { navController.popBackStack() },
                        onBook = { sessionId ->
                            if (appState.isAuthenticated) {
                                navController.navigate(AppRoute.BookSeats.createRoute(eventId, sessionId))
                            } else {
                                showAuthRequired = true
                            }
                        }
                    )
                }
                is DetailUiState.Error -> SeriesErrorScreen(
                    message = state.message,
                    onRetry = { detailViewModel.loadEventDetail(eventId) }
                )
                is DetailUiState.SuccessMovie -> MovieDetailScreen(
                    data = state.data,
                    onBack = { navController.popBackStack() },
                    onBook = { sessionId ->
                        if (appState.isAuthenticated) {
                            navController.navigate(AppRoute.BookSeats.createRoute(eventId, sessionId))
                        } else {
                            showAuthRequired = true
                        }
                    }
                )
                is DetailUiState.SuccessSeries -> SeriesErrorScreen(
                    message = "Selected item is not an event.",
                    onRetry = { detailViewModel.loadEventDetail(eventId) }
                )
            }
            if (showAuthRequired) {
                AuthRequiredDialog(
                    onDismiss = { showAuthRequired = false },
                    onLogin = {
                        showAuthRequired = false
                        navController.navigate(AppRoute.Login.route)
                    }
                )
            }
        }
        composable(
            route = AppRoute.EventDetail.route,
            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            val detailViewModel: DetailViewModel = hiltViewModel()
            val detailState by detailViewModel.uiState.collectAsState()
            var showAuthRequired by remember { mutableStateOf(false) }

            LaunchedEffect(eventId) {
                detailViewModel.loadEventDetail(eventId)
            }

            when (val state = detailState) {
                is DetailUiState.Loading -> SeriesLoadingScreen()
                is DetailUiState.SuccessEvent -> {
                    EventDetailScreen(
                        data = state.data,
                        onBack = { navController.popBackStack() },
                        onBook = { sessionId ->
                            if (appState.isAuthenticated) {
                                navController.navigate(AppRoute.BookSeats.createRoute(eventId, sessionId))
                            } else {
                                showAuthRequired = true
                            }
                        }
                    )
                }
                is DetailUiState.Error -> SeriesErrorScreen(
                    message = state.message,
                    onRetry = { detailViewModel.loadEventDetail(eventId) }
                )
                is DetailUiState.SuccessMovie -> MovieDetailScreen(
                    data = state.data,
                    onBack = { navController.popBackStack() },
                    onBook = { sessionId ->
                        if (appState.isAuthenticated) {
                            navController.navigate(AppRoute.BookSeats.createRoute(eventId, sessionId))
                        } else {
                            showAuthRequired = true
                        }
                    }
                )
                is DetailUiState.SuccessSeries -> SeriesErrorScreen(
                    message = "Selected item is not an event.",
                    onRetry = { detailViewModel.loadEventDetail(eventId) }
                )
            }
            if (showAuthRequired) {
                AuthRequiredDialog(
                    onDismiss = { showAuthRequired = false },
                    onLogin = {
                        showAuthRequired = false
                        navController.navigate(AppRoute.Login.route)
                    }
                )
            }
        }
        composable(
            route = AppRoute.BookSeats.route,
            arguments = listOf(
                navArgument("eventId") { type = NavType.StringType },
                navArgument("sessionId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) {
            val bookingViewModel: BookingViewModel = hiltViewModel()
            val bookingState by bookingViewModel.uiState.collectAsState()

            if (appState.isAuthenticated) {
                BookingScreen(
                    state = bookingState,
                    onSessionSelect = bookingViewModel::selectSession,
                    onSeatSelect = bookingViewModel::selectSeat,
                    onIncreaseSeats = bookingViewModel::increaseSeats,
                    onDecreaseSeats = bookingViewModel::decreaseSeats,
                    onConfirm = bookingViewModel::createBooking,
                    onRetry = { bookingViewModel.load() },
                    onViewTickets = { navController.navigateToBottomRoute(BottomNavRoute.Tickets.route) }
                )
            } else {
                AuthRequiredDialog(
                    onDismiss = { navController.popBackStack() },
                    onLogin = { navController.navigate(AppRoute.Login.route) }
                )
            }
        }
        composable(
            route = AppRoute.WatchSeries.route,
            arguments = listOf(navArgument("movieId") { type = NavType.StringType })
        ) { backStackEntry ->
            val movieId = backStackEntry.arguments?.getString("movieId") ?: ""
            val detailViewModel: DetailViewModel = hiltViewModel()
            val detailState by detailViewModel.uiState.collectAsState()

            LaunchedEffect(movieId) {
                detailViewModel.loadSeriesDetail(movieId)
            }

            when (val state = detailState) {
                is DetailUiState.SuccessSeries -> {
                    if (appState.isAuthenticated) {
                        WatchSeriesScreen(
                            data = state.data,
                            onBack = { navController.popBackStack() },
                            onEpisodeClick = { episode ->
                                val videoUrl = episode.videoUrl
                                if (!videoUrl.isNullOrBlank()) {
                                    navController.navigate(
                                        AppRoute.EpisodePlayer.createRoute(
                                            title = episode.title,
                                            videoUrl = videoUrl
                                        )
                                    )
                                }
                            }
                        )
                    } else {
                        LaunchedEffect(Unit) {
                            navController.navigate(AppRoute.Login.route)
                        }
                    }
                }
                is DetailUiState.Loading -> SeriesLoadingScreen()
                is DetailUiState.Error -> SeriesErrorScreen(
                    message = state.message,
                    onRetry = { detailViewModel.loadSeriesDetail(movieId) }
                )
                is DetailUiState.SuccessMovie -> SeriesErrorScreen(
                    message = "Selected item is not a series.",
                    onRetry = { detailViewModel.loadSeriesDetail(movieId) }
                )
                is DetailUiState.SuccessEvent -> SeriesErrorScreen(
                    message = "Selected item is not a series.",
                    onRetry = { detailViewModel.loadSeriesDetail(movieId) }
                )
            }
        }
        composable(
            route = AppRoute.EpisodePlayer.route,
            arguments = listOf(
                navArgument("title") { type = NavType.StringType },
                navArgument("videoUrl") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val title = backStackEntry.arguments?.getString("title").orEmpty()
            val videoUrl = backStackEntry.arguments?.getString("videoUrl").orEmpty()
            EpisodePlayerScreen(
                title = title,
                videoUrl = videoUrl,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

fun NavHostController.navigateToBottomRoute(route: String) {
    navigate(route) {
        popUpTo(BottomNavRoute.Home.route) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
private fun PosterCatalogContent(
    appState: CineScopeUiState,
    onRetry: () -> Unit,
    content: @Composable () -> Unit
) {
    when {
        appState.homeLoading -> SeriesLoadingScreen()
        appState.homeErrorMessage != null -> SeriesErrorScreen(
            message = appState.homeErrorMessage,
            onRetry = onRetry
        )
        else -> content()
    }
}

@Composable
private fun AuthRequiredDialog(onDismiss: () -> Unit, onLogin: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Sign in required") },
        text = { Text("Log in to book seats and keep your tickets in your profile.") },
        confirmButton = {
            Button(onClick = onLogin) {
                Text("Sign in")
            }
        }
    )
}

@Composable fun PaymentsIcon() = Icon(Icons.Outlined.Payments, null)
@Composable fun HistoryIcon() = Icon(Icons.AutoMirrored.Outlined.ListAlt, null)
