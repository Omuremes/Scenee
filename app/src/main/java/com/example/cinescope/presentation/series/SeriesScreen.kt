package com.example.cinescope.presentation.series

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.cinescope.presentation.models.SeriesPoster
import com.example.cinescope.presentation.models.SeriesSection
import com.example.cinescope.ui.components.PosterBox
import com.example.cinescope.ui.components.SearchRow
import com.example.cinescope.ui.theme.Crimson

@Composable
fun SeriesScreen(
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
private fun SeriesTopBar() {
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

@Composable
private fun SeriesSectionBlock(section: SeriesSection, onSeriesClick: () -> Unit) {
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

@Composable
private fun SeriesPosterCard(poster: SeriesPoster, onSeriesClick: () -> Unit) {
    Column(modifier = Modifier.width(192.dp).clickable { onSeriesClick() }) {
        PosterBox(modifier = Modifier.fillMaxWidth().aspectRatio(2f / 3f), theme = poster.theme, topBadge = poster.rating, compactBadge = true, ratingMode = true)
        Spacer(Modifier.height(10.dp))
        Text(poster.title, style = MaterialTheme.typography.titleLarge, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(poster.genre, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
