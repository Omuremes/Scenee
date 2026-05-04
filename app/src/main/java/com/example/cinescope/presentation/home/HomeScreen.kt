package com.example.cinescope.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.cinescope.presentation.models.*
import com.example.cinescope.ui.components.CategoryGrid
import com.example.cinescope.ui.components.PosterBox
import com.example.cinescope.ui.theme.Crimson

@Composable
fun HomeScreen(
    categories: List<HomeCategory>,
    sections: List<HomeSection>,
    isLoading: Boolean,
    errorMessage: String?,
    onRetry: () -> Unit,
    onMovieClick: (String) -> Unit,
    onConcertClick: (String) -> Unit,
    onStandupClick: (String) -> Unit,
    onEventClick: (String) -> Unit,
    onMoviesOpen: () -> Unit,
    onConcertsOpen: () -> Unit,
    onStandupOpen: () -> Unit,
    onKidsOpen: () -> Unit,
    onEventsOpen: () -> Unit,
    onSeriesOpen: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val visibleSections = remember(sections, searchQuery) {
        val query = searchQuery.trim()
        if (query.isBlank()) {
            sections
        } else {
            sections.mapNotNull { section ->
                val filtered = section.items.filter { poster ->
                    poster.title.contains(query, ignoreCase = true) ||
                        poster.subtitle.contains(query, ignoreCase = true) ||
                        poster.meta.contains(query, ignoreCase = true)
                }
                if (filtered.isEmpty()) null else section.copy(items = filtered)
            }
        }
    }

    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 24.dp)) {
        item {
            HomeSearchField(
                query = searchQuery,
                onQueryChange = { searchQuery = it }
            )
        }
        item {
            CategoryGrid(categories) { label ->
                when (label) {
                    "Cinema" -> onMoviesOpen()
                    "Series" -> onSeriesOpen()
                    "Concerts" -> onConcertsOpen()
                    "Stand-Up" -> onStandupOpen()
                    "Kids" -> onKidsOpen()
                    "Events" -> onEventsOpen()
                }
            }
        }
        when {
            isLoading -> item { HomeLoadingBlock() }
            errorMessage != null -> item { HomeErrorBlock(message = errorMessage, onRetry = onRetry) }
            visibleSections.isEmpty() -> item { EmptyPosterBlock() }
            else -> {
                items(visibleSections) { section ->
                    HomeSectionBlock(
                        section = section,
                        onMovieClick = onMovieClick,
                        onConcertClick = onConcertClick,
                        onStandupClick = onStandupClick,
                        onEventClick = onEventClick,
                        onSeeAllClick = {
                            when (section.title) {
                                "Concerts" -> onConcertsOpen()
                                "Stand-Up" -> onStandupOpen()
                                "Kids" -> onKidsOpen()
                                "Events" -> onEventsOpen()
                                else -> onMoviesOpen()
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeSearchField(query: String, onQueryChange: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(999.dp)),
            placeholder = { Text("Search movies, concerts, shows...") },
            leadingIcon = {
                Icon(
                    Icons.Filled.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    modifier = Modifier.size(18.dp)
                )
            },
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
    }
}

@Composable
private fun HomeLoadingBlock() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = Crimson)
    }
}

@Composable
private fun HomeErrorBlock(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("Could not load events", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
        Text(message, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}

@Composable
private fun EmptyPosterBlock() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 48.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            "No upcoming events yet",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun HomeSectionBlock(
    section: HomeSection,
    onMovieClick: (String) -> Unit,
    onConcertClick: (String) -> Unit,
    onStandupClick: (String) -> Unit,
    onEventClick: (String) -> Unit,
    onSeeAllClick: () -> Unit
) {
    Column(modifier = Modifier.padding(bottom = 26.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(section.title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
            Text(
                "See All",
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(Crimson.copy(alpha = 0.06f))
                    .clickable { onSeeAllClick() }
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                color = Crimson,
                style = MaterialTheme.typography.labelSmall
            )
        }
        Spacer(Modifier.height(16.dp))
        LazyRow(contentPadding = PaddingValues(horizontal = 24.dp), horizontalArrangement = Arrangement.spacedBy(18.dp)) {
            items(section.items) { poster ->
                when (section.title) {
                    "Concerts" -> ConcertCard(poster, onConcertClick)
                    "Stand-Up" -> StandupCard(poster, onStandupClick)
                    "Kids", "Events" -> StandupCard(poster, onEventClick)
                    else -> MovieCard(poster, onMovieClick)
                }
            }
        }
    }
}

@Composable
private fun MovieCard(poster: MediaPoster, onMovieClick: (String) -> Unit) {
    Column(modifier = Modifier
        .width(180.dp)
        .clickable { onMovieClick(poster.id) }) {
        PosterCover(
            poster = poster,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2f / 3f),
            badge = poster.meta,
            compactBadge = true,
            ratingMode = true
        )
        Spacer(Modifier.height(14.dp))
        Text(poster.title, style = MaterialTheme.typography.titleLarge, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Spacer(Modifier.height(4.dp))
        Text(poster.subtitle, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun ConcertCard(poster: MediaPoster, onClick: (String) -> Unit) {
    Column(modifier = Modifier
        .width(300.dp)
        .clickable { onClick(poster.id) }) {
        PosterCover(
            poster = poster,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            poster.title,
            style = MaterialTheme.typography.headlineMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(poster.subtitle, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(6.dp))
        Text(poster.meta, style = MaterialTheme.typography.labelSmall, color = Crimson)
    }
}

@Composable
private fun StandupCard(poster: MediaPoster, onClick: (String) -> Unit) {
    Column(modifier = Modifier
        .width(260.dp)
        .clickable { onClick(poster.id) }) {
        PosterCover(
            poster = poster,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(4f / 3f)
        )
        Spacer(Modifier.height(14.dp))
        Text(poster.title, style = MaterialTheme.typography.titleLarge)
        Text(poster.subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(6.dp))
        Text(
            poster.meta,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun PosterCover(
    poster: MediaPoster,
    modifier: Modifier,
    badge: String? = null,
    compactBadge: Boolean = false,
    ratingMode: Boolean = false
) {
    if (!poster.posterUrl.isNullOrBlank()) {
        Box(modifier = modifier.clip(RoundedCornerShape(28.dp))) {
            AsyncImage(
                model = poster.posterUrl,
                contentDescription = poster.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        androidx.compose.ui.graphics.Brush.verticalGradient(
                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.18f))
                        )
                    )
            )
            Text(
                "CinePass",
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp),
                color = Color.White.copy(alpha = 0.92f),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.ExtraBold
            )
            if (badge != null) {
                Row(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(14.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(Color.White.copy(alpha = 0.93f))
                        .padding(horizontal = if (compactBadge) 10.dp else 14.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (ratingMode) Icon(
                        Icons.Outlined.Star,
                        contentDescription = null,
                        tint = Color(0xFFEAB308),
                        modifier = Modifier.size(14.dp)
                    )
                    Text(badge, style = MaterialTheme.typography.labelSmall, color = Color(0xFF141414))
                }
            }
        }
    } else {
        PosterBox(
            modifier = modifier,
            theme = poster.theme,
            topBadge = badge,
            compactBadge = compactBadge,
            ratingMode = ratingMode
        )
    }
}
