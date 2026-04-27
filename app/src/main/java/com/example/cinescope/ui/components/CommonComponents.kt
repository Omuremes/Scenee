package com.example.cinescope.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ListAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Stadium
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.outlined.Tv
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.cinescope.presentation.models.*
import com.example.cinescope.ui.theme.Crimson

@Composable
fun PosterBox(
    modifier: Modifier,
    theme: PosterTheme,
    topBadge: String? = null,
    compactBadge: Boolean = false,
    ratingMode: Boolean = false
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(28.dp))
            .background(Brush.linearGradient(listOf(theme.start, theme.end)))
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Brush.radialGradient(listOf(Color.White.copy(alpha = 0.18f), Color.Transparent)))
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
        if (topBadge != null) {
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
                Text(topBadge, style = MaterialTheme.typography.labelSmall, color = Color(0xFF141414))
            }
        }
    }
}

@Composable
fun SearchRow(placeholder: String, withFilter: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(999.dp))
                .background(Color.White)
                .border(1.dp, Color(0xFFF1F1F1), RoundedCornerShape(999.dp))
                .padding(horizontal = 18.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Filled.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(12.dp))
            Text(
                placeholder,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
        if (withFilter) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.Tune, contentDescription = null)
            }
        }
    }
}

@Composable
fun CategoryGrid(categories: List<HomeCategory>, onSeriesOpen: () -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 14.dp)) {
        categories.chunked(3).forEach { row ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                row.forEach { category ->
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(24.dp))
                            .background(Color.White)
                            .border(1.dp, Color(0xFFF8F8F8), RoundedCornerShape(24.dp))
                            .clickable {
                                if (category.label == "Series") onSeriesOpen()
                            }
                            .padding(vertical = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            category.icon.toImageVector(),
                            contentDescription = null,
                            tint = if (category.selected) Crimson else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(28.dp)
                        )
                        Text(
                            category.label,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (category.selected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
fun CategoryIcon.toImageVector(): ImageVector = when (this) {
    CategoryIcon.Movie -> Icons.Outlined.Movie
    CategoryIcon.Series -> Icons.Outlined.Tv
    CategoryIcon.Music -> Icons.Outlined.MusicNote
    CategoryIcon.Mic -> Icons.Outlined.Mic
    CategoryIcon.Kids -> Icons.Outlined.Person
    CategoryIcon.Stadium -> Icons.Outlined.Stadium
    CategoryIcon.Person -> Icons.Outlined.Person
    CategoryIcon.Payments -> Icons.Outlined.Payments
    CategoryIcon.History -> Icons.AutoMirrored.Outlined.ListAlt
}
