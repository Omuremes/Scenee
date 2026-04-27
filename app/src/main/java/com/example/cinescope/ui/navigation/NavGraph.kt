package com.example.cinescope.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.cinescope.presentation.auth.AuthScreen
import com.example.cinescope.presentation.details.EventDetailScreen
import com.example.cinescope.presentation.details.MovieDetailScreen
import com.example.cinescope.presentation.details.SeriesDetailScreen
import com.example.cinescope.presentation.details.WatchSeriesScreen
import com.example.cinescope.presentation.home.HomeScreen
import com.example.cinescope.presentation.models.CineScopeUiState
import com.example.cinescope.presentation.profile.ProfileScreen
import com.example.cinescope.presentation.series.SeriesScreen
import com.example.cinescope.presentation.tickets.TicketsScreen
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ListAlt
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material3.Icon

sealed class BottomNavRoute(val route: String, val label: String) {
    data object Home : BottomNavRoute("home", "Poster")
    data object Series : BottomNavRoute("series", "Series")
    data object Tickets : BottomNavRoute("tickets", "Tickets")
    data object Profile : BottomNavRoute("profile", "Profile")
}

sealed class AppRoute(val route: String) {
    data object Login : AppRoute("login")
    data object Signup : AppRoute("signup")
    data object MovieDetail : AppRoute("movie_detail")
    data object ConcertDetail : AppRoute("concert_detail")
    data object StandupDetail : AppRoute("standup_detail")
    data object SeriesDetail : AppRoute("series_detail")
    data object WatchSeries : AppRoute("watch_series")
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
                onMovieClick = { navController.navigate(AppRoute.MovieDetail.route) },
                onConcertClick = { navController.navigate(AppRoute.ConcertDetail.route) },
                onStandupClick = { navController.navigate(AppRoute.StandupDetail.route) },
                onSeriesOpen = { navController.navigateToBottomRoute(BottomNavRoute.Series.route) }
            )
        }
        composable(BottomNavRoute.Series.route) {
            SeriesScreen(
                sections = appState.seriesSections,
                onSeriesClick = { navController.navigate(AppRoute.SeriesDetail.route) }
            )
        }
        composable(BottomNavRoute.Tickets.route) {
            TicketsScreen(appState.ticketTabs, appState.tickets)
        }
        composable(BottomNavRoute.Profile.route) {
            ProfileScreen(
                onLoginClick = { navController.navigate(AppRoute.Login.route) }
            )
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
        composable(AppRoute.MovieDetail.route) {
            MovieDetailScreen(
                data = appState.movieDetail,
                onBack = { navController.popBackStack() }
            )
        }
        composable(AppRoute.ConcertDetail.route) {
            EventDetailScreen(
                data = appState.concertDetail,
                onBack = { navController.popBackStack() }
            )
        }
        composable(AppRoute.StandupDetail.route) {
            EventDetailScreen(
                data = appState.standupDetail,
                onBack = { navController.popBackStack() }
            )
        }
        composable(AppRoute.SeriesDetail.route) {
            SeriesDetailScreen(
                data = appState.seriesDetail,
                onBack = { navController.popBackStack() },
                onEpisodesClick = { navController.navigate(AppRoute.WatchSeries.route) }
            )
        }
        composable(AppRoute.WatchSeries.route) {
            WatchSeriesScreen(
                data = appState.seriesDetail,
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

@Composable fun PaymentsIcon() = Icon(Icons.Outlined.Payments, null)
@Composable fun HistoryIcon() = Icon(Icons.AutoMirrored.Outlined.ListAlt, null)
