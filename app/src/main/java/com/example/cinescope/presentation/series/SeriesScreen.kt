package com.example.cinescope.presentation.series

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
    onSearchClick: () -> Unit,
    onSeriesClick: (String) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 24.dp)) {
        item {
            SearchRow(
                placeholder = "Search series, genres, or actors",
                withFilter = true,
                onClick = onSearchClick
            )
        }
        items(sections) { section ->
            SeriesSectionBlock(section, onSeriesClick)
        }
    }
}

@Composable
fun SeriesLoadingScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = Crimson)
    }
}

@Composable
fun SeriesErrorScreen(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Outlined.ErrorOutline,
            contentDescription = null,
            modifier = Modifier.width(64.dp).height(64.dp),
            tint = Crimson
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Series are unavailable",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = Crimson)
        ) {
            Text("Retry")
        }
    }
}

@Composable
private fun SeriesSectionBlock(section: SeriesSection, onSeriesClick: (String) -> Unit) {
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
private fun SeriesPosterCard(poster: SeriesPoster, onSeriesClick: (String) -> Unit) {
    Column(modifier = Modifier.width(192.dp).clickable { onSeriesClick(poster.id) }) {
        PosterBox(modifier = Modifier.fillMaxWidth().aspectRatio(2f / 3f), theme = poster.theme, topBadge = poster.rating, compactBadge = true, ratingMode = true)
        Spacer(Modifier.height(10.dp))
        Text(poster.title, style = MaterialTheme.typography.titleLarge, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(poster.genre, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
