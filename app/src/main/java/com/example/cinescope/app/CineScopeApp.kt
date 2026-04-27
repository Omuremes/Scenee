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
import com.example.cinescope.data.CineScopeRepository
import com.example.cinescope.presentation.models.CineScopeUiState
import com.example.cinescope.ui.components.CineScopeTopBar
import com.example.cinescope.ui.navigation.AppRoute
import com.example.cinescope.ui.navigation.BottomNavRoute
import com.example.cinescope.ui.navigation.CineScopeNavGraph
import com.example.cinescope.ui.navigation.bottomNavRouteSet
import com.example.cinescope.ui.navigation.navigateToBottomRoute
import com.example.cinescope.ui.theme.Crimson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class CineScopeViewModel @Inject constructor(
    private val repository: CineScopeRepository
) : ViewModel() {
    val uiState: StateFlow<CineScopeUiState> = repository.getAuthToken()
        .map { token ->
            repository.loadInitialState(isAuthenticated = token != null)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = repository.loadInitialState(isAuthenticated = false)
        )
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
                AppHeader(currentRoute, navController)
            }
        },
        bottomBar = {
            if (currentRoute in bottomNavRouteSet) {
                BottomNavigationBar(navController, currentRoute)
            }
        }
    ) { innerPadding ->
        CineScopeNavGraph(
            navController = navController,
            appState = uiState,
            startDestination = if (uiState.isAuthenticated) BottomNavRoute.Home.route else AppRoute.Login.route,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
private fun AppHeader(currentRoute: String?, navController: NavHostController) {
    when (currentRoute) {
        BottomNavRoute.Home.route -> CineScopeTopBar(showLogo = true, showMenu = true, showSearch = true, showProfile = true)
        BottomNavRoute.Series.route -> CineScopeTopBar(showLogo = true, showMenu = true, showSearch = true, showProfile = true)
        BottomNavRoute.Tickets.route -> CineScopeTopBar(title = "My tickets", centeredTitle = true)
        BottomNavRoute.Profile.route -> CineScopeTopBar(title = "Profile", centeredTitle = true)
        
        AppRoute.MovieDetail.route -> CineScopeTopBar(title = "Cinema", showBack = true, onBackClick = { navController.popBackStack() })
        AppRoute.ConcertDetail.route -> CineScopeTopBar(title = "Concert", showBack = true, onBackClick = { navController.popBackStack() })
        AppRoute.StandupDetail.route -> CineScopeTopBar(title = "Stand-Up", showBack = true, onBackClick = { navController.popBackStack() })
        AppRoute.SeriesDetail.route -> CineScopeTopBar(title = "Series", showBack = true, onBackClick = { navController.popBackStack() })
        AppRoute.WatchSeries.route -> CineScopeTopBar(title = "Episodes", showBack = true, onBackClick = { navController.popBackStack() })
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
