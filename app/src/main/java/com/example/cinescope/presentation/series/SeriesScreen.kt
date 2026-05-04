package com.example.cinescope.presentation.series

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
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
    var showFilters by remember { mutableStateOf(false) }
    var selectedCategoryId by remember { mutableStateOf<String?>(null) }

    val availableCategories = remember(sections) {
        sections.flatMap { it.items }
            .flatMap { it.categories }
            .distinctBy { it.id }
            .sortedBy { it.name }
    }

    val filteredSections = remember(sections, selectedCategoryId) {
        if (selectedCategoryId == null) {
            sections
        } else {
            sections.mapNotNull { section ->
                val filteredItems = section.items.filter { poster ->
                    poster.categories.any { it.id == selectedCategoryId }
                }
                if (filteredItems.isEmpty()) null else section.copy(items = filteredItems)
            }
        }
    }

    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 24.dp)) {
        item {
            SearchRow(
                placeholder = "Search series, genres, or actors",
                withFilter = true,
                onClick = onSearchClick,
                onFilterClick = { showFilters = !showFilters }
            )
        }
        if (showFilters) {
            item {
                LazyRow(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        FilterChip(
                            label = "All",
                            selected = selectedCategoryId == null,
                            onClick = { selectedCategoryId = null }
                        )
                    }
                    items(availableCategories, key = { it.id }) { category ->
                        FilterChip(
                            label = category.name,
                            selected = selectedCategoryId == category.id,
                            onClick = { selectedCategoryId = category.id }
                        )
                    }
                }
            }
        }
        if (filteredSections.isEmpty()) {
            item {
                EmptySeriesState()
            }
        } else {
            items(filteredSections) { section ->
                SeriesSectionBlock(section, onSeriesClick)
            }
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

@Composable
private fun FilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Text(
        text = label,
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(if (selected) Crimson else Color(0xFFF3F4F6))
            .clickable { onClick() }
            .padding(horizontal = 18.dp, vertical = 10.dp),
        color = if (selected) Color.White else Color(0xFF71717A),
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
private fun EmptySeriesState() {
    Box(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 44.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No series match this filter",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
