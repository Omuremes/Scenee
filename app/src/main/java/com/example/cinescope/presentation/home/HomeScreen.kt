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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.cinescope.presentation.models.*
import com.example.cinescope.ui.components.CategoryGrid
import com.example.cinescope.ui.components.PosterBox
import com.example.cinescope.ui.components.SearchRow
import com.example.cinescope.ui.theme.Crimson

@Composable
fun HomeScreen(
    categories: List<HomeCategory>,
    sections: List<HomeSection>,
    onMovieClick: () -> Unit,
    onConcertClick: () -> Unit,
    onStandupClick: () -> Unit,
    onSeriesOpen: () -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 24.dp)) {
        item { SearchRow("Search movies, concerts, shows...") }
        item { CategoryGrid(categories, onSeriesOpen) }
        items(sections) { section ->
            HomeSectionBlock(section, onMovieClick, onConcertClick, onStandupClick)
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
                    else -> MovieCard(poster, onMovieClick)
                }
            }
        }
    }
}

@Composable
private fun MovieCard(poster: MediaPoster, onMovieClick: () -> Unit) {
    Column(modifier = Modifier
        .width(180.dp)
        .clickable { onMovieClick() }) {
        PosterBox(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2f / 3f),
            theme = poster.theme,
            topBadge = poster.meta,
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
private fun ConcertCard(poster: MediaPoster, onClick: () -> Unit) {
    Column(modifier = Modifier
        .width(300.dp)
        .clickable { onClick() }) {
        PosterBox(modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f), theme = poster.theme)
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
private fun StandupCard(poster: MediaPoster, onClick: () -> Unit) {
    Column(modifier = Modifier
        .width(260.dp)
        .clickable { onClick() }) {
        PosterBox(modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(4f / 3f), theme = poster.theme)
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
