package com.example.cinescope.presentation.catalog

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.Tune
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
import com.example.cinescope.presentation.models.MediaPoster
import com.example.cinescope.ui.components.PosterBox
import com.example.cinescope.ui.theme.Crimson

@Composable
fun MoviesCatalogScreen(
    items: List<MediaPoster>,
    onMovieClick: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        CatalogSearchSection(
            title = "Browse Movies",
            placeholder = "Search movies or cinemas",
            chips = listOf("Now Showing", "Coming Soon", "IMAX", "Premiere")
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 120.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalArrangement = Arrangement.spacedBy(28.dp)
        ) {
            items(items) { item ->
                MovieGridCard(item = item, onClick = { onMovieClick(item.id) })
            }
        }
    }
}

@Composable
fun ConcertsCatalogScreen(
    items: List<MediaPoster>,
    onConcertClick: (String) -> Unit
) {
    VerticalCatalogScreen(
        title = "Browse Concerts",
        placeholder = "Search concerts or artists",
        chips = listOf("All Concerts", "Now Showing", "Rock", "Electronic"),
        items = items,
        onClick = onConcertClick
    )
}

@Composable
fun StandupCatalogScreen(
    items: List<MediaPoster>,
    onStandupClick: (String) -> Unit
) {
    VerticalCatalogScreen(
        title = "Browse Stand-Up",
        placeholder = "Search comedians or specials",
        chips = listOf("All Specials", "Recently Added", "Observational", "Political"),
        items = items,
        onClick = onStandupClick
    )
}

@Composable
private fun VerticalCatalogScreen(
    title: String,
    placeholder: String,
    chips: List<String>,
    items: List<MediaPoster>,
    onClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        item {
            CatalogSearchSection(
                title = title,
                placeholder = placeholder,
                chips = chips
            )
        }
        items(items.size) { index ->
            val item = items[index]
            VerticalPosterCard(item = item, onClick = { onClick(item.id) })
        }
    }
}

@Composable
private fun CatalogSearchSection(
    title: String,
    placeholder: String,
    chips: List<String>
) {
    Column(
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        Text(title, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color(0xFFF3F4F6))
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Outlined.Search, contentDescription = null, tint = Color(0xFFA1A1AA))
                Spacer(Modifier.width(10.dp))
                Text(placeholder, color = Color(0xFFA1A1AA), style = MaterialTheme.typography.bodyMedium)
            }
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFFF3F4F6)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.Tune, contentDescription = null, tint = Crimson)
            }
        }
        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(chips.size) { index ->
                val selected = index == 0
                Text(
                    text = chips[index],
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(if (selected) Crimson else Color(0xFFF3F4F6))
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                    color = if (selected) Color.White else Color(0xFF71717A),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun MovieGridCard(item: MediaPoster, onClick: () -> Unit) {
    Column(modifier = Modifier.clickable(onClick = onClick)) {
        PosterBox(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2f / 3f),
            theme = item.theme,
            topBadge = item.meta,
            compactBadge = true,
            ratingMode = true
        )
        Spacer(Modifier.height(14.dp))
        Text(
            item.title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(Modifier.height(4.dp))
        Text(
            item.subtitle,
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFFA1A1AA),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun VerticalPosterCard(item: MediaPoster, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .clickable(onClick = onClick)
    ) {
        Box {
            PosterBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f),
                theme = item.theme
            )
            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(Color.White.copy(alpha = 0.95f))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Outlined.Star, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(14.dp))
                Text(item.meta, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface)
            }
        }
        Spacer(Modifier.height(16.dp))
        Text(item.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text(
            item.subtitle,
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFFA1A1AA),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
