package com.example.cinescope.presentation.details

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import android.content.res.Configuration
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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.VideoLibrary
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.cinescope.presentation.models.*
import com.example.cinescope.ui.components.PosterBox
import com.example.cinescope.ui.theme.Crimson

@Composable
fun MovieDetailScreen(data: MovieDetailData, onBack: () -> Unit, onBook: (String?) -> Unit) {
    var selectedTab by remember { mutableStateOf(MovieTab.Tickets) }
    var selectedSessionIndex by remember(data.sessions) { mutableStateOf<Int?>(null) }
    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 120.dp)) {
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
                item {
                    MovieTicketsTab(
                        data = data,
                        selectedSessionIndex = selectedSessionIndex,
                        onSessionSelect = { selectedSessionIndex = it },
                        onBook = { onBook(selectedSessionIndex?.let { data.sessions[it].id }) }
                    )
                }
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
fun EventDetailScreen(data: EventDetailData, onBack: () -> Unit, onBook: (String?) -> Unit) {
    var selectedTab by remember { mutableStateOf(EventTab.Tickets) }
    val isGeneralAdmission = data.eventTypeCode == "kids" || data.eventTypeCode == "events"
    val initialDateIndex = remember(data.dates) {
        val selected = data.dates.indexOfFirst { it.selected }
        if (selected >= 0) selected else 0
    }
    var selectedDateIndex by remember(data.dates) { mutableStateOf(initialDateIndex) }
    var selectedFilter by remember { mutableStateOf(EventSessionFilter.All) }
    var selectedSessionIndex by remember(data.sessions, isGeneralAdmission) {
        mutableStateOf(if (isGeneralAdmission && data.sessions.isNotEmpty()) 0 else null)
    }

    val dates = data.dates.mapIndexed { index, chip ->
        chip.copy(selected = index == selectedDateIndex)
    }
    val sessions = data.sessions.mapIndexed { index, session ->
        SessionDisplay(index, session.copy(selected = index == selectedSessionIndex))
    }.filter { isGeneralAdmission || sessionMatchesFilter(it.session, selectedFilter) }
    val isConfirmEnabled = selectedSessionIndex != null

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 190.dp)) {
            item {
                Box(modifier = Modifier.fillMaxWidth().aspectRatio(4f / 5f)) {
                    PosterBox(modifier = Modifier.fillMaxSize(), theme = data.theme)
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color.Transparent, Color.Black.copy(alpha = 0.82f))
                                )
                            )
                    )
                }
            }
            item {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Card(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(horizontal = 24.dp)
                            .offset(y = (-32).dp)
                            .widthIn(max = 520.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(40.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
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
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    EventTab.values().forEach { tab ->
                        val isSelected = selectedTab == tab
                        Text(
                            text = if (tab == EventTab.Tickets) "Tickets" else "About event",
                            modifier = Modifier.clickable { selectedTab = tab },
                            color = if (isSelected) Crimson else MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold
                        )
                    }
                }
            }
            item { HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) }
            when (selectedTab) {
                EventTab.Tickets -> item {
                    EventTicketsTab(
                        dates = dates,
                        selectedFilter = selectedFilter,
                        sessions = sessions,
                        showFilters = !isGeneralAdmission,
                        onDateSelect = { selectedDateIndex = it },
                        onFilterSelect = {
                            selectedFilter = it
                            val current = selectedSessionIndex
                            if (current != null && !sessionMatchesFilter(data.sessions[current], it)) {
                                selectedSessionIndex = null
                            }
                        },
                        onSessionSelect = { selectedSessionIndex = it }
                    )
                }
                EventTab.About -> item { EventAboutTab(data) }
            }
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 28.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(if (isConfirmEnabled) Crimson else Crimson.copy(alpha = 0.5f))
                .clickable(enabled = isConfirmEnabled) {
                    onBook(selectedSessionIndex?.let { data.sessions[it].id })
                }
                .padding(vertical = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    data.confirmLabel.uppercase(),
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black
                )
                Icon(Icons.AutoMirrored.Outlined.ArrowForward, contentDescription = null, tint = Color.White)
            }
        }
    }
}

private enum class EventTab { Tickets, About }
private enum class EventSessionFilter { Daily, Evening, All }

private data class SessionDisplay(val index: Int, val session: MovieSession)

private fun sessionMatchesFilter(session: MovieSession, filter: EventSessionFilter): Boolean {
    val hour = session.time.substringBefore(':').toIntOrNull() ?: return true
    return when (filter) {
        EventSessionFilter.All -> true
        EventSessionFilter.Daily -> hour in 6..17
        EventSessionFilter.Evening -> hour !in 6..17
    }
}

@Composable
fun SeriesDetailScreen(data: SeriesDetailData, onBack: () -> Unit, onEpisodesClick: () -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 90.dp)) {
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
fun WatchSeriesScreen(
    data: SeriesDetailData,
    onBack: () -> Unit,
    onEpisodeClick: (EpisodeItem) -> Unit
) {
    var selectedSeason by remember { mutableStateOf(data.seasons.firstOrNull().orEmpty()) }
    val seasonEpisodes = remember(data.episodes, selectedSeason) {
        data.episodes.filter { selectedSeason.isBlank() || it.seasonLabel == selectedSeason }
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 24.dp, top = 20.dp, end = 24.dp, bottom = 110.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
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
        if (seasonEpisodes.isEmpty()) {
            item {
                Text(
                    "No episodes available for this season",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        items(seasonEpisodes, key = { it.id }) { episode ->
            EpisodeCard(episode = episode, onClick = { onEpisodeClick(episode) })
        }
    }
}

@Composable
fun EpisodePlayerScreen(
    title: String,
    videoUrl: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val activity = remember(context) { context.findActivity() }
    var isLandscapeFullscreen by remember(activity) {
        mutableStateOf(
            activity?.resources?.configuration?.orientation == Configuration.ORIENTATION_LANDSCAPE
        )
    }
    val exoPlayer = remember(videoUrl) {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(videoUrl))
            prepare()
            playWhenReady = true
        }
    }

    DisposableEffect(activity, exoPlayer) {
        val window = activity?.window
        val previousOrientation = activity?.requestedOrientation ?: ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        val controller = window?.let { WindowCompat.getInsetsController(it, it.decorView) }

        if (window != null && controller != null) {
            WindowCompat.setDecorFitsSystemWindows(window, false)
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        onDispose {
            exoPlayer.release()
            activity?.requestedOrientation = previousOrientation
            if (window != null && controller != null) {
                controller.show(WindowInsetsCompat.Type.systemBars())
                WindowCompat.setDecorFitsSystemWindows(window, true)
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Black
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AndroidView(
                factory = { viewContext ->
                    PlayerView(viewContext).apply {
                        player = exoPlayer
                        useController = true
                        setShowNextButton(false)
                        setShowPreviousButton(false)
                        setFullscreenButtonClickListener { shouldEnterFullscreen ->
                            isLandscapeFullscreen = shouldEnterFullscreen
                            activity?.requestedOrientation = if (shouldEnterFullscreen) {
                                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                            } else {
                                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxSize(),
                update = { playerView ->
                    playerView.player = exoPlayer
                }
            )

            if (!isLandscapeFullscreen) {
                Row(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Outlined.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                    Text(
                        text = title,
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

private fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

@Composable
private fun MovieTicketsTab(
    data: MovieDetailData,
    selectedSessionIndex: Int?,
    onSessionSelect: (Int) -> Unit,
    onBook: () -> Unit
) {
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
            FilterPill("Daily", true) { }
            FilterPill("Evening", false) { }
            FilterPill("All", false) { }
        }
        data.sessions.forEachIndexed { index, session ->
            SessionCard(session.copy(selected = index == selectedSessionIndex)) { onSessionSelect(index) }
            Spacer(Modifier.height(12.dp))
        }
        val enabled = selectedSessionIndex != null
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(999.dp))
                .background(if (enabled) Crimson else Crimson.copy(alpha = 0.5f))
                .clickable(enabled = enabled) { onBook() }
                .padding(vertical = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("CONFIRM BOOKING", color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
private fun EventTicketsTab(
    dates: List<MovieDateChip>,
    selectedFilter: EventSessionFilter,
    sessions: List<SessionDisplay>,
    showFilters: Boolean,
    onDateSelect: (Int) -> Unit,
    onFilterSelect: (EventSessionFilter) -> Unit,
    onSessionSelect: (Int) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp), verticalArrangement = Arrangement.spacedBy(22.dp)) {
        Column {
            Text("Select Date", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
            Spacer(Modifier.height(14.dp))
            Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                dates.forEachIndexed { index, chip ->
                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(if (chip.selected) Crimson else MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { onDateSelect(index) }
                            .padding(horizontal = 18.dp, vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(chip.day.uppercase(), style = MaterialTheme.typography.labelSmall, color = if (chip.selected) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(4.dp))
                        Text(chip.date, color = if (chip.selected) Color.White else MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        if (showFilters) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterPill("Daily", selectedFilter == EventSessionFilter.Daily) { onFilterSelect(EventSessionFilter.Daily) }
                FilterPill("Evening", selectedFilter == EventSessionFilter.Evening) { onFilterSelect(EventSessionFilter.Evening) }
                FilterPill("All", selectedFilter == EventSessionFilter.All) { onFilterSelect(EventSessionFilter.All) }
            }
        }
        if (sessions.isEmpty()) {
            Text(
                "No sessions available",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            sessions.forEach { session ->
                SessionCard(session.session) { onSessionSelect(session.index) }
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun EventAboutTab(data: EventDetailData) {
    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp)) {
        Text("About event", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(10.dp))
        Text(data.description, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                Text(data.reviewCount, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
        if (data.comments.isNotEmpty()) {
            Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                data.comments.forEach { comment ->
                    Card(
                        modifier = Modifier.width(300.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            comment,
                            modifier = Modifier.padding(18.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 4,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
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

@Composable private fun EpisodeCard(episode: EpisodeItem, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White)
            .clickable(enabled = !episode.videoUrl.isNullOrBlank(), onClick = onClick)
            .padding(12.dp),
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
            if (episode.description.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    episode.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Schedule, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text(episode.duration, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Icon(
            if (episode.videoUrl.isNullOrBlank()) Icons.Outlined.MoreVert else Icons.Outlined.PlayCircle,
            contentDescription = null,
            tint = if (episode.videoUrl.isNullOrBlank()) MaterialTheme.colorScheme.onSurfaceVariant else Crimson
        )
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

@Composable private fun FilterPill(text: String, selected: Boolean, onClick: () -> Unit) {
    Text(
        text,
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(if (selected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.surfaceVariant)
            .clickable { onClick() }
            .padding(horizontal = 18.dp, vertical = 10.dp),
        color = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
        style = MaterialTheme.typography.labelSmall
    )
}

@Composable private fun SessionCard(session: MovieSession, onClick: () -> Unit) {
    val border = if (session.selected) BorderStroke(2.dp, Crimson) else BorderStroke(0.dp, Color.Transparent)
    Card(
        modifier = Modifier.clickable(enabled = !session.soldOut) { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F8F8)),
        border = border
    ) {
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
