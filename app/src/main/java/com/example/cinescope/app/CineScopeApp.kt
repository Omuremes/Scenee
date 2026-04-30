package com.example.cinescope.app

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.Tv
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.cinescope.data.auth.AuthRepository
import com.example.cinescope.data.home.HomeRepository
import com.example.cinescope.data.profile.ProfileRepository
import com.example.cinescope.data.tickets.TicketsRepository
import com.example.cinescope.presentation.models.CineScopeUiState
import com.example.cinescope.ui.components.CineScopeTopBar
import com.example.cinescope.ui.navigation.AppRoute
import com.example.cinescope.ui.navigation.BottomNavRoute
import com.example.cinescope.ui.navigation.CineScopeNavGraph
import com.example.cinescope.ui.navigation.bottomNavRouteSet
import com.example.cinescope.ui.navigation.navigateToBottomRoute
import com.example.cinescope.ui.theme.Crimson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class CineScopeViewModel @Inject constructor(
    authRepository: AuthRepository,
    private val profileRepository: ProfileRepository,
    private val homeRepository: HomeRepository,
    private val ticketsRepository: TicketsRepository
) : ViewModel() {

    private val authFlow = authRepository.getAuthToken()
    private val refreshTrigger = MutableStateFlow(0)

    val uiState: StateFlow<CineScopeUiState> = combine(
        authFlow,
        refreshTrigger
    ) { token: String?, _: Int ->
        val homeResult = runCatching { homeRepository.getHomeSections() }
        val profileSummary = if (token != null) {
            try {
                profileRepository.getProfileSummary()
            } catch (e: Exception) {
                null
            }
        } else null

        CineScopeUiState(
            isAuthenticated = token != null,
            homeLoading = false,
            homeErrorMessage = homeResult.exceptionOrNull()?.message,
            homeSections = homeResult.getOrDefault(emptyList()),
            categories = homeRepository.getCategories(),
            ticketTabs = ticketsRepository.getTicketTabs(),
            tickets = ticketsRepository.getTickets(),
            profileSummary = profileSummary
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CineScopeUiState(
            homeLoading = true,
            categories = homeRepository.getCategories(),
            ticketTabs = ticketsRepository.getTicketTabs(),
            tickets = ticketsRepository.getTickets()
        )
    )
    
    fun refresh() {
        refreshTrigger.value += 1
    }
}

@Composable
fun CineScopeApp(viewModel: CineScopeViewModel = hiltViewModel()) {
    val navController = rememberNavController()
    val uiState by viewModel.uiState.collectAsState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        topBar = {
            if (currentRoute != AppRoute.Login.route && currentRoute != AppRoute.Signup.route) {
                AppHeader(currentRoute, navController, uiState)
            }
        },
        bottomBar = {
            if (shouldShowBottomBar(currentRoute)) {
                BottomNavigationBar(navController, selectedBottomRoute(currentRoute))
            }
        }
    ) { innerPadding ->
        CineScopeNavGraph(
            navController = navController,
            appState = uiState,
            startDestination = BottomNavRoute.Home.route,
            onRetry = viewModel::refresh,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

private fun shouldShowBottomBar(currentRoute: String?): Boolean {
    if (currentRoute == null) return false
    return currentRoute in bottomNavRouteSet ||
        currentRoute == AppRoute.Movies.route ||
        currentRoute == AppRoute.Concerts.route ||
        currentRoute == AppRoute.Standup.route ||
        currentRoute.startsWith("movie_detail") ||
        currentRoute.startsWith("concert_detail") ||
        currentRoute.startsWith("standup_detail") ||
        currentRoute.startsWith("series_detail") ||
        currentRoute.startsWith("watch_series")
}

private fun selectedBottomRoute(currentRoute: String?): String? = when {
    currentRoute == null -> null
    currentRoute == BottomNavRoute.Tickets.route -> BottomNavRoute.Tickets.route
    currentRoute == BottomNavRoute.Profile.route -> BottomNavRoute.Profile.route
    currentRoute == BottomNavRoute.Series.route ||
        currentRoute.startsWith("series_detail") ||
        currentRoute.startsWith("watch_series") -> BottomNavRoute.Series.route
    else -> BottomNavRoute.Home.route
}

@Composable
private fun AppHeader(currentRoute: String?, navController: NavHostController, uiState: CineScopeUiState) {
    val initials = if (uiState.isAuthenticated) uiState.profileSummary?.initials else null
    val onProfileClick = { navController.navigateToBottomRoute(BottomNavRoute.Profile.route) }

    when (currentRoute) {
        BottomNavRoute.Home.route -> CineScopeTopBar(
            showLogo = true, 
            showMenu = true, 
            showProfile = true,
            profileInitials = initials,
            onProfileClick = onProfileClick
        )
        BottomNavRoute.Series.route -> CineScopeTopBar(
            showLogo = true, 
            showMenu = true, 
            showProfile = true,
            profileInitials = initials,
            onProfileClick = onProfileClick
        )
        BottomNavRoute.Tickets.route -> CineScopeTopBar(title = "My tickets", centeredTitle = true)
        BottomNavRoute.Profile.route -> CineScopeTopBar(title = "Profile", centeredTitle = true)
        AppRoute.Movies.route -> CineScopeTopBar(title = "All Movies", showBack = true, centeredTitle = true, onBackClick = { navController.popBackStack() })
        AppRoute.Concerts.route -> CineScopeTopBar(title = "All Concerts", showBack = true, centeredTitle = true, onBackClick = { navController.popBackStack() })
        AppRoute.Standup.route -> CineScopeTopBar(title = "Stand-Up", showBack = true, centeredTitle = true, onBackClick = { navController.popBackStack() })
        
        else -> {
            if (currentRoute?.startsWith("movie_detail") == true) {
                CineScopeTopBar(title = "Cinema", showBack = true, onBackClick = { navController.popBackStack() })
            } else if (currentRoute?.startsWith("concert_detail") == true) {
                CineScopeTopBar(title = "Concert", showBack = true, centeredTitle = true, onBackClick = { navController.popBackStack() })
            } else if (currentRoute?.startsWith("standup_detail") == true) {
                CineScopeTopBar(title = "Stand-Up", showBack = true, centeredTitle = true, onBackClick = { navController.popBackStack() })
            } else if (currentRoute?.startsWith("series_detail") == true) {
                CineScopeTopBar(title = "Series", showBack = true, centeredTitle = true, onBackClick = { navController.popBackStack() })
            } else if (currentRoute?.startsWith("watch_series") == true) {
                CineScopeTopBar(title = "Episodes", showBack = true, centeredTitle = true, onBackClick = { navController.popBackStack() })
            }
        }
    }
}

@Composable
private fun BottomNavigationBar(navController: NavHostController, currentRoute: String?) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        val items = listOf(
            BottomNavRoute.Home,
            BottomNavRoute.Series,
            BottomNavRoute.Tickets,
            BottomNavRoute.Profile
        )
        items.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                selected = selected,
                onClick = { navController.navigateToBottomRoute(item.route) },
                icon = {
                    Icon(
                        imageVector = getIconForRoute(item),
                        contentDescription = item.label,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                },
                label = { Text(item.label, style = MaterialTheme.typography.labelSmall) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Crimson,
                    selectedTextColor = Crimson,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

private fun getIconForRoute(route: BottomNavRoute): ImageVector = when (route) {
    BottomNavRoute.Home -> Icons.Outlined.Movie
    BottomNavRoute.Series -> Icons.Outlined.Tv
    BottomNavRoute.Tickets -> Icons.Outlined.ConfirmationNumber
    BottomNavRoute.Profile -> Icons.Outlined.AccountCircle
}
