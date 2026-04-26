package com.example.cinescope.app

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Stadium
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.outlined.Tv
import androidx.compose.material.icons.outlined.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.cinescope.data.CategoryIcon
import com.example.cinescope.data.CineScopeRepository
import com.example.cinescope.data.HomeCategory
import com.example.cinescope.data.HomeSection
import com.example.cinescope.data.MediaPoster
import com.example.cinescope.data.PosterTheme
import com.example.cinescope.data.SeriesPoster
import com.example.cinescope.data.SeriesSection
import com.example.cinescope.data.TicketSummary
import com.example.cinescope.ui.theme.Crimson

@Composable
fun CineScopeApp(
    appViewModel: CineScopeViewModel = viewModel()
) {
    val navController = rememberNavController()
    val appState by appViewModel.uiState
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            val route = currentDestination?.route
            if (route in bottomNavRouteSet) {
                NavigationBar(
                    modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars),
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                    tonalElevation = 0.dp
                ) {
                    bottomNavItems.forEach { item ->
                        val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigateToBottomRoute(item.route)
                            },
                            icon = {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(if (selected) Crimson else Color.Transparent)
                                        .padding(horizontal = 18.dp, vertical = 8.dp)
                                ) {
                                    Icon(
                                        item.icon,
                                        contentDescription = item.label,
                                        tint = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            },
                            label = {
                                Text(
                                    item.label,
                                    color = if (selected) Crimson else MaterialTheme.colorScheme.onSurfaceVariant,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavRoute.Home.route,
            modifier = Modifier.padding(innerPadding)
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
                    profile = appState.profileSummary,
                    onLoginClick = { navController.navigate(AppRoute.Login.route) }
                )
            }
            composable(AppRoute.Login.route) {
                AuthScreen(
                    isSignup = false,
                    onBack = { navController.popBackStack() },
                    onSwitch = {
                        navController.navigate(AppRoute.Signup.route)
                    }
                )
            }
            composable(AppRoute.Signup.route) {
                AuthScreen(
                    isSignup = true,
                    onBack = { navController.popBackStack() },
                    onSwitch = {
                        navController.navigate(AppRoute.Login.route)
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
}

class CineScopeViewModel : ViewModel() {
    private val repository = CineScopeRepository()
    val uiState = mutableStateOf(repository.loadInitialState())
}

data class CineScopeUiState(
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

private sealed class BottomNavRoute(val route: String, val label: String) {
    data object Home : BottomNavRoute("home", "Poster")
    data object Series : BottomNavRoute("series", "Series")
    data object Tickets : BottomNavRoute("tickets", "Tickets")
    data object Profile : BottomNavRoute("profile", "Profile")
}

private sealed class AppRoute(val route: String) {
    data object Login : AppRoute("login")
    data object Signup : AppRoute("signup")
    data object MovieDetail : AppRoute("movie_detail")
    data object ConcertDetail : AppRoute("concert_detail")
    data object StandupDetail : AppRoute("standup_detail")
    data object SeriesDetail : AppRoute("series_detail")
    data object WatchSeries : AppRoute("watch_series")
}

private data class BottomNavItem(val route: String, val label: String, val icon: ImageVector)

private val bottomNavItems = listOf(
    BottomNavItem(BottomNavRoute.Home.route, "Poster", Icons.Outlined.Movie),
    BottomNavItem(BottomNavRoute.Series.route, "Series", Icons.Outlined.Tv),
    BottomNavItem(BottomNavRoute.Tickets.route, "Tickets", Icons.Outlined.ConfirmationNumber),
    BottomNavItem(BottomNavRoute.Profile.route, "Profile", Icons.Outlined.Person)
)

private val bottomNavRouteSet = setOf(
    BottomNavRoute.Home.route,
    BottomNavRoute.Series.route,
    BottomNavRoute.Tickets.route,
    BottomNavRoute.Profile.route
)

private fun NavHostController.navigateToBottomRoute(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
private fun HomeScreen(
    categories: List<HomeCategory>,
    sections: List<HomeSection>,
    onMovieClick: () -> Unit,
    onConcertClick: () -> Unit,
    onStandupClick: () -> Unit,
    onSeriesOpen: () -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 24.dp)) {
        item { HomeTopBar() }
        item { SearchRow("Search movies, concerts, shows...") }
        item { CategoryGrid(categories, onSeriesOpen) }
        items(sections) { section ->
            HomeSectionBlock(section, onMovieClick, onConcertClick, onStandupClick)
        }
    }
}

@Composable
private fun SeriesScreen(
    sections: List<SeriesSection>,
    onSeriesClick: () -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 24.dp)) {
        item { SeriesTopBar() }
        item {
            Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp)) {
                Text("Watch Series", style = MaterialTheme.typography.displayLarge, fontWeight = FontWeight.Bold)
            }
        }
        item { SearchRow("Search series, genres, or actors", true) }
        items(sections) { section ->
            SeriesSectionBlock(section, onSeriesClick)
        }
    }
}

@Composable
private fun TicketsScreen(tabs: List<String>, tickets: List<TicketSummary>) {
    var selectedTab by remember { mutableStateOf(tabs.first()) }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 24.dp, top = 0.dp, end = 24.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Box(
                modifier = Modifier.fillMaxWidth().background(Color.White.copy(alpha = 0.85f)).padding(top = 16.dp, bottom = 8.dp),
                contentAlignment = Alignment.Center
            ) { Text("My tickets", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) }
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text("Your Collection", style = MaterialTheme.typography.displayLarge, fontWeight = FontWeight.ExtraBold)
                Text("Ready for your next experience?", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(999.dp)).padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                tabs.forEach { tab ->
                    val selected = selectedTab == tab
                    Box(
                        modifier = Modifier.weight(1f).clip(RoundedCornerShape(999.dp)).background(if (selected) Color.White else Color.Transparent).clickable { selectedTab = tab }.padding(vertical = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(tab, style = MaterialTheme.typography.labelLarge, color = if (selected) Crimson else MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        items(tickets) { ticket -> TicketCard(ticket) }
    }
}

@Composable
private fun ProfileScreen(profile: ProfileSummary, onLoginClick: () -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 24.dp, top = 0.dp, end = 24.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Box(
                modifier = Modifier.fillMaxWidth().background(Color.White).border(BorderStroke(1.dp, Color(0xFFF1F1F1))).padding(vertical = 18.dp),
                contentAlignment = Alignment.Center
            ) { Text("My Profile", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold) }
        }
        item {
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(contentAlignment = Alignment.BottomEnd) {
                    Box(
                        modifier = Modifier.size(128.dp).clip(CircleShape).background(Brush.linearGradient(listOf(Color(0xFFF5D0D3), Color(0xFFE50914)))).border(4.dp, Color(0xFFF3F4F5), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(profile.name.split(" ").map { it.first() }.joinToString(""), style = MaterialTheme.typography.displayLarge, color = Color.White, fontWeight = FontWeight.Black)
                    }
                    Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(Crimson).border(4.dp, Color.White, CircleShape), contentAlignment = Alignment.Center) {
                        Icon(Icons.Outlined.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }
                Spacer(Modifier.height(22.dp))
                Text(profile.name, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                Text(profile.email, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text("ACCOUNT", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(horizontal = 16.dp))
                Column(
                    modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(24.dp)).padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) { profile.actions.forEach { action -> ProfileActionRow(action) } }
            }
        }
        item {
            Box(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(999.dp)).border(2.dp, Crimson.copy(alpha = 0.2f), RoundedCornerShape(999.dp)).clickable { onLoginClick() }.padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) { Text("Login / Logout", color = Crimson, fontWeight = FontWeight.Bold) }
        }
        item {
            Text("CinePass v2.4.1 (Build 890)", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
        }
    }
}

@Composable
private fun AuthScreen(isSignup: Boolean, onBack: () -> Unit, onSwitch: () -> Unit) {
    var checked by remember { mutableStateOf(false) }
    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        if (isSignup) {
            Icon(Icons.Outlined.ArrowBack, contentDescription = null, tint = Crimson, modifier = Modifier.padding(24.dp).clickable { onBack() })
        }
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Outlined.Movie, contentDescription = null, tint = Crimson, modifier = Modifier.size(64.dp))
                Text(if (isSignup) "Welcome to CinePass" else "Welcome Back", style = MaterialTheme.typography.displayLarge, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center)
                Text(if (isSignup) "Create account" else "Please enter your details to continue", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
            }
            Spacer(Modifier.height(36.dp))
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                if (isSignup) AuthField("Username")
                AuthField("Email Address")
                AuthField("Password", true)
                if (isSignup) AuthField("Confirm Password", true)
            }
            if (isSignup) {
                Row(modifier = Modifier.fillMaxWidth().padding(top = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = checked, onCheckedChange = { checked = it })
                    Text("I agree to the Terms & Conditions", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                Text("Forgot Password?", modifier = Modifier.padding(top = 16.dp), color = Crimson, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(16.dp))
            Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(999.dp)).background(Crimson).padding(vertical = 18.dp), contentAlignment = Alignment.Center) {
                Text(if (isSignup) "Sign Up" else "Login", color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(24.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                HorizontalDivider(modifier = Modifier.weight(1f))
                Text("OR CONTINUE WITH", modifier = Modifier.padding(horizontal = 12.dp), style = MaterialTheme.typography.labelSmall, color = Color(0xFF94A3B8))
                HorizontalDivider(modifier = Modifier.weight(1f))
            }
            Spacer(Modifier.height(18.dp))
            Row(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(999.dp)).border(1.dp, MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(999.dp)).padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = Color(0xFF4285F4), modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(10.dp))
                Text("Google", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(18.dp))
            Row {
                Text(if (isSignup) "Already have an account?" else "Don't have an account?", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.width(6.dp))
                Text(if (isSignup) "Login" else "Sign Up", color = Crimson, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { onSwitch() })
            }
        }
    }
}

@Composable
private fun MovieDetailScreen(data: MovieDetailData, onBack: () -> Unit) {
    var selectedTab by remember { mutableStateOf(MovieTab.Tickets) }
    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 120.dp)) {
        item {
            Box(modifier = Modifier.fillMaxWidth().background(Color.White.copy(alpha = 0.85f)).padding(horizontal = 24.dp, vertical = 16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Icon(Icons.Outlined.ArrowBack, contentDescription = null, modifier = Modifier.clickable { onBack() })
                    Text("Cinema", style = MaterialTheme.typography.headlineMedium, color = Crimson, fontWeight = FontWeight.Black)
                }
            }
        }
        item { HeroMediaBlock() }
        item {
            Column(
                modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background, RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)).padding(24.dp)
            ) {
                Text(data.title, style = MaterialTheme.typography.displayLarge, fontWeight = FontWeight.Black)
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    data.genres.forEach {
                        Text(it, modifier = Modifier.clip(RoundedCornerShape(999.dp)).background(MaterialTheme.colorScheme.surfaceVariant).padding(horizontal = 14.dp, vertical = 8.dp), color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Schedule, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(data.duration, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Spacer(Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("CineScore", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.width(8.dp))
                    Icon(Icons.Outlined.Star, contentDescription = null, tint = Crimson, modifier = Modifier.size(18.dp))
                    Text(data.rating, color = Crimson, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
                }
            }
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).background(Color.White).padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                data.tabs.forEach { tab ->
                    val selected = selectedTab == tab
                    Text(
                        text = when (tab) {
                            MovieTab.Tickets -> "TICKETS"
                            MovieTab.About -> "ABOUT MOVIE"
                            MovieTab.Comments -> "COMMENTS"
                        },
                        modifier = Modifier.clickable { selectedTab = tab }.padding(vertical = 18.dp),
                        color = if (selected) Crimson else MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
        when (selectedTab) {
            MovieTab.Tickets -> {
                item { MovieTicketsTab(data) }
            }
            MovieTab.About -> {
                item { MovieAboutTab(data) }
            }
            MovieTab.Comments -> {
                item { MovieCommentsTab(data) }
            }
        }
    }
}

@Composable
private fun EventDetailScreen(data: EventDetailData, onBack: () -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 120.dp)) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .border(BorderStroke(1.dp, Color(0xFFF1F1F1)))
                    .padding(horizontal = 24.dp, vertical = 18.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.ArrowBack, contentDescription = null, modifier = Modifier.clickable { onBack() })
                    Spacer(Modifier.weight(1f))
                    Text(data.screenTitle, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.weight(1f))
                }
            }
        }
        item {
            Box(modifier = Modifier.fillMaxWidth().aspectRatio(4f / 5f)) {
                PosterBox(modifier = Modifier.fillMaxSize(), theme = data.theme)
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                            )
                        )
                )
            }
        }
        item {
            Card(
                modifier = Modifier.padding(horizontal = 24.dp).offset(y = (-32).dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(40.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            data.badge,
                            modifier = Modifier.clip(RoundedCornerShape(999.dp)).background(Crimson).padding(horizontal = 14.dp, vertical = 8.dp),
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall
                        )
                        Text(
                            data.ageLabel,
                            modifier = Modifier.clip(RoundedCornerShape(999.dp)).background(MaterialTheme.colorScheme.surfaceVariant).padding(horizontal = 14.dp, vertical = 8.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                    Text(data.title, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.ExtraBold)
                    Text(data.accentTitle, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.ExtraBold, color = Crimson)
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Outlined.Place, contentDescription = null, tint = Crimson)
                        Text(data.venue, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Text("Tickets", color = Crimson, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                Text("About event", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
            }
        }
        item { HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) }
        item { EventTicketsTab(data) }
    }
}

@Composable
private fun SeriesDetailScreen(data: SeriesDetailData, onBack: () -> Unit, onEpisodesClick: () -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 90.dp)) {
        item {
            Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.ArrowBack, contentDescription = null, modifier = Modifier.clickable { onBack() })
                    Spacer(Modifier.weight(1f))
                    Text("Series", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.weight(1f))
                }
            }
        }
        item {
            Box(modifier = Modifier.fillMaxWidth().aspectRatio(16f / 9f)) {
                PosterBox(modifier = Modifier.fillMaxSize(), theme = PosterTheme.CrimsonNight)
                Box(modifier = Modifier.align(Alignment.Center).size(78.dp).clip(CircleShape).background(Crimson.copy(alpha = 0.92f)), contentAlignment = Alignment.Center) {
                    Icon(Icons.Outlined.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(40.dp))
                }
                Text("Trailer • 2:14", modifier = Modifier.align(Alignment.BottomEnd).padding(18.dp).clip(RoundedCornerShape(999.dp)).background(Color.Black.copy(alpha = 0.4f)).padding(horizontal = 12.dp, vertical = 6.dp), color = Color.White, style = MaterialTheme.typography.labelSmall)
            }
        }
        item {
            Card(modifier = Modifier.padding(horizontal = 24.dp).offset(y = (-24).dp), colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(24.dp)) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(data.title, style = MaterialTheme.typography.displayLarge, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        data.genres.forEach {
                            Text(it, modifier = Modifier.clip(RoundedCornerShape(999.dp)).background(MaterialTheme.colorScheme.surfaceVariant).padding(horizontal = 12.dp, vertical = 8.dp), color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    Spacer(Modifier.height(20.dp))
                    Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(999.dp)).background(Brush.linearGradient(listOf(Color(0xFFB8000B), Color(0xFFE50914)))).clickable { onEpisodesClick() }.padding(vertical = 18.dp), contentAlignment = Alignment.Center) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.PlayCircle, contentDescription = null, tint = Color.White)
                            Spacer(Modifier.width(10.dp))
                            Text("Watch Now", color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
        item {
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Text("Storyline", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(10.dp))
                Text(data.storyline, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("All details", color = Crimson, fontWeight = FontWeight.Bold)
                    Icon(Icons.Outlined.KeyboardArrowDown, contentDescription = null, tint = Crimson)
                }
            }
        }
        item {
            Card(modifier = Modifier.padding(24.dp), colors = CardDefaults.cardColors(containerColor = Color(0x1A9CC4FF)), shape = RoundedCornerShape(20.dp)) {
                Row(modifier = Modifier.fillMaxWidth().padding(18.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(Color.White), contentAlignment = Alignment.Center) {
                            Icon(Icons.Outlined.VideoLibrary, contentDescription = null, tint = Crimson)
                        }
                        Column {
                            Text("Series and episodes", fontWeight = FontWeight.Bold)
                            Text("2 Seasons, 24 Episodes", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Text("All", modifier = Modifier.clip(RoundedCornerShape(999.dp)).background(Color.White).clickable { onEpisodesClick() }.padding(horizontal = 18.dp, vertical = 10.dp), fontWeight = FontWeight.Bold)
                }
            }
        }
        item { RatingSection(data) }
        item { CastSection(data.cast) }
        item { ReviewsSection(data.reviews) }
    }
}

@Composable
private fun WatchSeriesScreen(data: SeriesDetailData, onBack: () -> Unit) {
    var selectedSeason by remember { mutableStateOf(data.seasons.first()) }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 24.dp, top = 20.dp, end = 24.dp, bottom = 110.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Icon(Icons.Outlined.ArrowBack, contentDescription = null, tint = Crimson, modifier = Modifier.align(Alignment.CenterStart).clickable { onBack() })
                Text("Episodes", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
        }
        item {
            Column {
                Text("NOW PLAYING", style = MaterialTheme.typography.labelSmall, color = Crimson)
                Spacer(Modifier.height(4.dp))
                Text(data.title, style = MaterialTheme.typography.displayLarge, fontWeight = FontWeight.ExtraBold)
            }
        }
        item {
            Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                data.seasons.forEach { season ->
                    val selected = selectedSeason == season
                    Text(
                        season,
                        modifier = Modifier.clip(RoundedCornerShape(999.dp)).background(if (selected) Crimson else MaterialTheme.colorScheme.surfaceVariant).clickable { selectedSeason = season }.padding(horizontal = 18.dp, vertical = 12.dp),
                        color = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }
        items(data.episodes) { episode ->
            EpisodeCard(episode)
        }
    }
}

@Composable
private fun MovieTicketsTab(data: MovieDetailData) {
    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp), verticalArrangement = Arrangement.spacedBy(22.dp)) {
        Column {
            Text("Select Date", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
            Spacer(Modifier.height(14.dp))
            Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                data.dates.forEach { chip ->
                    Column(
                        modifier = Modifier.clip(RoundedCornerShape(999.dp)).background(if (chip.selected) Crimson else MaterialTheme.colorScheme.surfaceVariant).padding(horizontal = 18.dp, vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(chip.day.uppercase(), style = MaterialTheme.typography.labelSmall, color = if (chip.selected) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(4.dp))
                        Text(chip.date, color = if (chip.selected) Color.White else MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterPill("Daily", true)
            FilterPill("Evening", false)
            FilterPill("All", false)
        }
        data.sessions.chunked(2).forEach { row ->
            row.forEach { session ->
                SessionCard(session)
                Spacer(Modifier.height(12.dp))
            }
        }
        Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(999.dp)).background(Crimson).padding(vertical = 20.dp), contentAlignment = Alignment.Center) {
            Text("CONFIRM BOOKING", color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
private fun EventTicketsTab(data: EventDetailData) {
    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp), verticalArrangement = Arrangement.spacedBy(22.dp)) {
        Column {
            Text("Select Date", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
            Spacer(Modifier.height(14.dp))
            Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                data.dates.forEach { chip ->
                    Column(
                        modifier = Modifier.clip(RoundedCornerShape(999.dp)).background(if (chip.selected) Crimson else MaterialTheme.colorScheme.surfaceVariant).padding(horizontal = 18.dp, vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(chip.day.uppercase(), style = MaterialTheme.typography.labelSmall, color = if (chip.selected) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(4.dp))
                        Text(chip.date, color = if (chip.selected) Color.White else MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterPill("Daily", true)
            FilterPill("Evening", false)
            FilterPill("All", false)
        }
        data.sessions.forEach { session ->
            SessionCard(session)
            Spacer(Modifier.height(12.dp))
        }
        Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(999.dp)).background(Crimson).padding(vertical = 20.dp), contentAlignment = Alignment.Center) {
            Text(data.confirmLabel.uppercase(), color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
private fun MovieAboutTab(data: MovieDetailData) {
    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
        Column {
            Text("Description", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
            Spacer(Modifier.height(10.dp))
            Text(data.description, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Column {
            Text("Starring", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
            Spacer(Modifier.height(12.dp))
            Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                data.cast.forEach { name ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(modifier = Modifier.size(64.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant))
                        Spacer(Modifier.height(8.dp))
                        Text(name, modifier = Modifier.width(78.dp), style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center)
                    }
                }
            }
        }
        Column {
            Text("Details", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
            Spacer(Modifier.height(10.dp))
            data.details.forEachIndexed { index, item ->
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(item.first, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(item.second, fontWeight = FontWeight.Bold)
                }
                if (index != data.details.lastIndex) HorizontalDivider()
            }
        }
    }
}

@Composable
private fun MovieCommentsTab(data: MovieDetailData) {
    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9)), shape = RoundedCornerShape(28.dp)) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(data.rating, style = MaterialTheme.typography.displayLarge, fontWeight = FontWeight.Black)
                Row {
                    repeat(4) { Icon(Icons.Outlined.Star, contentDescription = null, tint = Color(0xFFEAB308)) }
                    Icon(Icons.Outlined.Star, contentDescription = null, tint = Color(0xFFEAB308).copy(alpha = 0.5f))
                }
                Spacer(Modifier.height(6.dp))
                Text("2.4k Reviews", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(16.dp))
                data.reviews.forEach { review ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(review.label, modifier = Modifier.width(16.dp), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.width(10.dp))
                        Box(modifier = Modifier.weight(1f).height(8.dp).clip(RoundedCornerShape(999.dp)).background(Color(0xFFE5E7EB))) {
                            Box(modifier = Modifier.fillMaxWidth(review.progress).height(8.dp).clip(RoundedCornerShape(999.dp)).background(Crimson))
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                }
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("User Reviews", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
            Text("Newest", color = Crimson, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable private fun RatingSection(data: SeriesDetailData) {
    Row(
        modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant).padding(horizontal = 24.dp, vertical = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Row { repeat(4) { Icon(Icons.Outlined.Star, contentDescription = null, tint = Color(0xFFEAB308)) }; Icon(Icons.Outlined.Star, contentDescription = null, tint = Color(0xFFEAB308).copy(alpha = 0.4f)) }
            Spacer(Modifier.height(6.dp))
            Text("${data.rating} / 5.0", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(data.reviewCount, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text("Rate This Series", modifier = Modifier.clip(RoundedCornerShape(999.dp)).background(Crimson).padding(horizontal = 18.dp, vertical = 14.dp), color = Color.White, fontWeight = FontWeight.Bold)
    }
}

@Composable private fun CastSection(cast: List<String>) {
    Column(modifier = Modifier.padding(vertical = 20.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Main Cast", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text("See All", color = Crimson, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(16.dp))
        Row(modifier = Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 24.dp), horizontalArrangement = Arrangement.spacedBy(18.dp)) {
            cast.forEach {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(modifier = Modifier.size(96.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant))
                    Spacer(Modifier.height(10.dp))
                    Text(it, modifier = Modifier.width(80.dp), textAlign = TextAlign.Center, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable private fun ReviewsSection(reviews: List<String>) {
    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("User Reviews", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text("All", modifier = Modifier.clip(RoundedCornerShape(999.dp)).background(MaterialTheme.colorScheme.surfaceVariant).padding(horizontal = 14.dp, vertical = 8.dp), fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(14.dp))
        Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            reviews.forEach { review ->
                Card(modifier = Modifier.width(300.dp), colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(20.dp)) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant))
                            Column { Text("Viewer", fontWeight = FontWeight.Bold); Row { repeat(5) { Icon(Icons.Outlined.Star, contentDescription = null, tint = Color(0xFFEAB308), modifier = Modifier.size(14.dp)) } } }
                        }
                        Spacer(Modifier.height(12.dp))
                        Text("\"$review\"", color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 4, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
        }
    }
}

@Composable private fun EpisodeCard(episode: EpisodeItem) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Color.White).padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.width(128.dp).height(80.dp).clip(RoundedCornerShape(10.dp))) {
            PosterBox(modifier = Modifier.fillMaxSize(), theme = episode.theme)
            Box(modifier = Modifier.matchParentSize().background(Color.Black.copy(alpha = 0.14f)), contentAlignment = Alignment.Center) {
                Icon(Icons.Outlined.PlayCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(30.dp))
            }
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(episode.badge, style = MaterialTheme.typography.labelSmall, color = Crimson)
            Text(episode.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Schedule, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text(episode.duration, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Icon(Icons.Outlined.MoreVert, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable private fun AuthField(label: String, password: Boolean = false) {
    OutlinedTextField(
        value = "",
        onValueChange = {},
        placeholder = { Text(label, color = Color(0xFF94A3B8)) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(999.dp),
        visualTransformation = if (password) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}

@Composable private fun HomeTopBar() {
    Row(
        modifier = Modifier.fillMaxWidth().background(Color.White.copy(alpha = 0.85f)).padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.Menu, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("CinePass", style = MaterialTheme.typography.headlineMedium, color = Crimson, fontWeight = FontWeight.Black)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant).border(2.dp, Crimson.copy(alpha = 0.1f), CircleShape), contentAlignment = Alignment.Center) {
                Text("AJ", color = Crimson, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable private fun SeriesTopBar() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 28.dp, vertical = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.Menu, contentDescription = null)
            Text("CinePass", style = MaterialTheme.typography.displayLarge, color = Crimson, fontWeight = FontWeight.Bold)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(Icons.Filled.Search, contentDescription = null)
            Icon(Icons.Outlined.AccountCircle, contentDescription = null)
        }
    }
}

@Composable private fun SearchRow(placeholder: String, withFilter: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f).clip(RoundedCornerShape(999.dp)).background(Color.White).border(1.dp, Color(0xFFF1F1F1), RoundedCornerShape(999.dp)).padding(horizontal = 18.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f), modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(12.dp))
            Text(placeholder, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
        }
        if (withFilter) {
            Box(modifier = Modifier.size(52.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant), contentAlignment = Alignment.Center) {
                Icon(Icons.Outlined.Tune, contentDescription = null)
            }
        }
    }
}

@Composable private fun CategoryGrid(categories: List<HomeCategory>, onSeriesOpen: () -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 14.dp)) {
        categories.chunked(3).forEach { row ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                row.forEach { category ->
                    Column(
                        modifier = Modifier.weight(1f).clip(RoundedCornerShape(24.dp)).background(Color.White).border(1.dp, Color(0xFFF8F8F8), RoundedCornerShape(24.dp)).clickable {
                            if (category.label == "Series") onSeriesOpen()
                        }.padding(vertical = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(category.icon.toImageVector(), contentDescription = null, tint = if (category.selected) Crimson else MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(28.dp))
                        Text(category.label, style = MaterialTheme.typography.labelSmall, color = if (category.selected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun HomeSectionBlock(
    section: HomeSection,
    onMovieClick: () -> Unit,
    onConcertClick: () -> Unit,
    onStandupClick: () -> Unit
) {
    Column(modifier = Modifier.padding(bottom = 26.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(section.title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
            Text("See All", modifier = Modifier.clip(RoundedCornerShape(999.dp)).background(Crimson.copy(alpha = 0.06f)).padding(horizontal = 14.dp, vertical = 8.dp), color = Crimson, style = MaterialTheme.typography.labelSmall)
        }
        Spacer(Modifier.height(16.dp))
        LazyRow(contentPadding = PaddingValues(horizontal = 24.dp), horizontalArrangement = Arrangement.spacedBy(18.dp)) {
            items(section.items) { poster ->
                if (section.title == "Concerts") ConcertCard(poster, onConcertClick)
                else if (section.title == "Stand-Up") StandupCard(poster, onStandupClick)
                else MovieCard(poster, onMovieClick)
            }
        }
    }
}

@Composable private fun SeriesSectionBlock(section: SeriesSection, onSeriesClick: () -> Unit) {
    Column(modifier = Modifier.padding(top = 6.dp, bottom = 18.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(section.title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text("See All", color = Crimson, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(14.dp))
        LazyRow(contentPadding = PaddingValues(horizontal = 24.dp), horizontalArrangement = Arrangement.spacedBy(20.dp)) {
            items(section.items) { poster -> SeriesPosterCard(poster, onSeriesClick) }
        }
    }
}

@Composable private fun MovieCard(poster: MediaPoster, onMovieClick: () -> Unit) {
    Column(modifier = Modifier.width(180.dp).clickable { onMovieClick() }) {
        PosterBox(modifier = Modifier.fillMaxWidth().aspectRatio(2f / 3f), theme = poster.theme, topBadge = poster.meta, compactBadge = true, ratingMode = true)
        Spacer(Modifier.height(14.dp))
        Text(poster.title, style = MaterialTheme.typography.titleLarge, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Spacer(Modifier.height(4.dp))
        Text(poster.subtitle, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable private fun ConcertCard(poster: MediaPoster, onClick: () -> Unit) {
    Column(modifier = Modifier.width(300.dp).clickable { onClick() }) {
        PosterBox(modifier = Modifier.fillMaxWidth().aspectRatio(16f / 9f), theme = poster.theme)
        Spacer(Modifier.height(16.dp))
        Text(poster.title, style = MaterialTheme.typography.headlineMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(poster.subtitle, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(6.dp))
        Text(poster.meta, style = MaterialTheme.typography.labelSmall, color = Crimson)
    }
}

@Composable private fun StandupCard(poster: MediaPoster, onClick: () -> Unit) {
    Column(modifier = Modifier.width(260.dp).clickable { onClick() }) {
        PosterBox(modifier = Modifier.fillMaxWidth().aspectRatio(4f / 3f), theme = poster.theme)
        Spacer(Modifier.height(14.dp))
        Text(poster.title, style = MaterialTheme.typography.titleLarge)
        Text(poster.subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(6.dp))
        Text(poster.meta, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
    }
}

@Composable private fun SeriesPosterCard(poster: SeriesPoster, onSeriesClick: () -> Unit) {
    Column(modifier = Modifier.width(192.dp).clickable { onSeriesClick() }) {
        PosterBox(modifier = Modifier.fillMaxWidth().aspectRatio(2f / 3f), theme = poster.theme, topBadge = poster.rating, compactBadge = true, ratingMode = true)
        Spacer(Modifier.height(10.dp))
        Text(poster.title, style = MaterialTheme.typography.titleLarge, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(poster.genre, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable private fun TicketCard(ticket: TicketSummary) {
    Card(shape = RoundedCornerShape(28.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(20.dp), horizontalArrangement = Arrangement.spacedBy(18.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(width = 112.dp, height = 160.dp)) {
                PosterBox(modifier = Modifier.fillMaxSize(), theme = ticket.posterTheme)
                Text(ticket.category, modifier = Modifier.align(Alignment.TopEnd).offset(x = 8.dp, y = (-8).dp).clip(RoundedCornerShape(999.dp)).background(ticket.accent).padding(horizontal = 12.dp, vertical = 6.dp), color = Color.White, style = MaterialTheme.typography.labelSmall)
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(ticket.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    MetaRow(Icons.Outlined.CalendarToday, ticket.dateTime)
                    MetaRow(Icons.Outlined.Place, ticket.venue)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("View Ticket", modifier = Modifier.clip(RoundedCornerShape(999.dp)).background(Crimson).padding(horizontal = 16.dp, vertical = 10.dp), color = Color.White, style = MaterialTheme.typography.labelSmall)
                    Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFF9F9F9)).border(1.dp, Color(0xFFF0F0F0), RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Outlined.ConfirmationNumber, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f), modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

@Composable private fun ProfileActionRow(action: ProfileAction) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Color.White).clickable { }.padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(Crimson.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
                Icon(action.icon.toImageVector(), contentDescription = null, tint = Crimson, modifier = Modifier.size(20.dp))
            }
            Text(action.title, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        }
        Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable private fun PosterBox(modifier: Modifier, theme: PosterTheme, topBadge: String? = null, compactBadge: Boolean = false, ratingMode: Boolean = false) {
    Box(modifier = modifier.clip(RoundedCornerShape(28.dp)).background(Brush.linearGradient(listOf(theme.start, theme.end)))) {
        Box(modifier = Modifier.matchParentSize().background(Brush.radialGradient(listOf(Color.White.copy(alpha = 0.18f), Color.Transparent))))
        Text("CinePass", modifier = Modifier.align(Alignment.BottomStart).padding(16.dp), color = Color.White.copy(alpha = 0.92f), style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.ExtraBold)
        if (topBadge != null) {
            Row(
                modifier = Modifier.align(Alignment.TopStart).padding(14.dp).clip(RoundedCornerShape(999.dp)).background(Color.White.copy(alpha = 0.93f)).padding(horizontal = if (compactBadge) 10.dp else 14.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (ratingMode) Icon(Icons.Outlined.Star, contentDescription = null, tint = Color(0xFFEAB308), modifier = Modifier.size(14.dp))
                Text(topBadge, style = MaterialTheme.typography.labelSmall, color = Color(0xFF141414))
            }
        }
    }
}

@Composable private fun MetaRow(icon: ImageVector, text: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable private fun FilterPill(text: String, selected: Boolean) {
    Text(
        text,
        modifier = Modifier.clip(RoundedCornerShape(999.dp)).background(if (selected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.surfaceVariant).padding(horizontal = 18.dp, vertical = 10.dp),
        color = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
        style = MaterialTheme.typography.labelSmall
    )
}

@Composable private fun SessionCard(session: MovieSession) {
    val border = if (session.selected) BorderStroke(2.dp, Crimson) else BorderStroke(0.dp, Color.Transparent)
    Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F8F8)), border = border) {
        Row(modifier = Modifier.fillMaxWidth().padding(20.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text(session.time, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black, color = if (session.selected) Crimson else MaterialTheme.colorScheme.onSurface)
                Spacer(Modifier.height(4.dp))
                Text("${session.hall} • ${session.status}", style = MaterialTheme.typography.labelLarge, color = if (session.soldOut) Color.Red.copy(alpha = 0.7f) else if (session.selected) Crimson else MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(session.price, fontWeight = if (session.selected) FontWeight.Bold else FontWeight.SemiBold, color = if (session.selected) Crimson else MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable private fun HeroMediaBlock() {
    Box(modifier = Modifier.fillMaxWidth().aspectRatio(16f / 9f)) {
        PosterBox(modifier = Modifier.fillMaxSize(), theme = PosterTheme.CrimsonNight)
        Box(modifier = Modifier.align(Alignment.Center).size(72.dp).clip(CircleShape).background(Crimson), contentAlignment = Alignment.Center) {
            Icon(Icons.Outlined.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(36.dp))
        }
        Box(modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(4.dp).background(Color.White.copy(alpha = 0.2f))) {
            Box(modifier = Modifier.fillMaxWidth(0.33f).height(4.dp).background(Crimson))
        }
    }
}

private fun CategoryIcon.toImageVector(): ImageVector = when (this) {
    CategoryIcon.Movie -> Icons.Outlined.Movie
    CategoryIcon.Series -> Icons.Outlined.Tv
    CategoryIcon.Music -> Icons.Outlined.MusicNote
    CategoryIcon.Mic -> Icons.Outlined.Mic
    CategoryIcon.Kids -> Icons.Outlined.Person
    CategoryIcon.Stadium -> Icons.Outlined.Stadium
    CategoryIcon.Person -> Icons.Outlined.Person
    CategoryIcon.Payments -> Icons.Outlined.Payments
    CategoryIcon.History -> Icons.Outlined.History
}
