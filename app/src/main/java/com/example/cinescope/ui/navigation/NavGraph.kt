package com.example.cinescope.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ListAlt
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.cinescope.presentation.auth.AuthScreen
import com.example.cinescope.presentation.catalog.ConcertsCatalogScreen
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
    data object MovieDetail : AppRoute("movie_detail/{movieId}") {
        fun createRoute(id: String) = "movie_detail/$id"
    }
    data object ConcertDetail : AppRoute("concert_detail/{eventId}") {
        fun createRoute(id: String) = "concert_detail/$id"
    }
    data object StandupDetail : AppRoute("standup_detail/{eventId}") {
        fun createRoute(id: String) = "standup_detail/$id"
    }
    data object SeriesDetail : AppRoute("series_detail/{movieId}") {
        fun createRoute(id: String) = "series_detail/$id"
    }
    data object WatchSeries : AppRoute("watch_series/{movieId}") {
        fun createRoute(id: String) = "watch_series/$id"
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
                onMoviesOpen = { navController.navigate(AppRoute.Movies.route) },
                onConcertsOpen = { navController.navigate(AppRoute.Concerts.route) },
                onStandupOpen = { navController.navigate(AppRoute.Standup.route) },
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
            TicketsScreen(
                tabs = appState.ticketTabs,
                tickets = appState.tickets,
                isAuthenticated = appState.isAuthenticated,
                onLoginClick = { navController.navigate(AppRoute.Login.route) }
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

            LaunchedEffect(movieId) {
                detailViewModel.loadCinemaEventDetail(movieId)
            }

            when (val state = detailState) {
                is DetailUiState.Loading -> SeriesLoadingScreen()
                is DetailUiState.SuccessMovie -> {
                    MovieDetailScreen(
                        data = state.data,
                        onBack = { navController.popBackStack() }
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
                        onBack = { navController.popBackStack() }
                    )
                }
                is DetailUiState.Error -> SeriesErrorScreen(
                    message = state.message,
                    onRetry = { detailViewModel.loadCinemaEventDetail(movieId) }
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
                        onBack = { navController.popBackStack() }
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
                    onBack = { navController.popBackStack() }
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

            LaunchedEffect(eventId) {
                detailViewModel.loadEventDetail(eventId)
            }

            when (val state = detailState) {
                is DetailUiState.Loading -> SeriesLoadingScreen()
                is DetailUiState.SuccessEvent -> {
                    EventDetailScreen(
                        data = state.data,
                        onBack = { navController.popBackStack() }
                    )
                }
                is DetailUiState.Error -> SeriesErrorScreen(
                    message = state.message,
                    onRetry = { detailViewModel.loadEventDetail(eventId) }
                )
                is DetailUiState.SuccessMovie -> MovieDetailScreen(
                    data = state.data,
                    onBack = { navController.popBackStack() }
                )
                is DetailUiState.SuccessSeries -> SeriesErrorScreen(
                    message = "Selected item is not an event.",
                    onRetry = { detailViewModel.loadEventDetail(eventId) }
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

            LaunchedEffect(eventId) {
                detailViewModel.loadEventDetail(eventId)
            }

            when (val state = detailState) {
                is DetailUiState.Loading -> SeriesLoadingScreen()
                is DetailUiState.SuccessEvent -> {
                    EventDetailScreen(
                        data = state.data,
                        onBack = { navController.popBackStack() }
                    )
                }
                is DetailUiState.Error -> SeriesErrorScreen(
                    message = state.message,
                    onRetry = { detailViewModel.loadEventDetail(eventId) }
                )
                is DetailUiState.SuccessMovie -> MovieDetailScreen(
                    data = state.data,
                    onBack = { navController.popBackStack() }
                )
                is DetailUiState.SuccessSeries -> SeriesErrorScreen(
                    message = "Selected item is not an event.",
                    onRetry = { detailViewModel.loadEventDetail(eventId) }
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
                            onBack = { navController.popBackStack() }
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

@Composable fun PaymentsIcon() = Icon(Icons.Outlined.Payments, null)
@Composable fun HistoryIcon() = Icon(Icons.AutoMirrored.Outlined.ListAlt, null)
