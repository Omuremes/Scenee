package com.example.cinescope.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cinescope.ui.theme.Crimson

@Composable
fun CineScopeTopBar(
    title: String? = null,
    showLogo: Boolean = false,
    showBack: Boolean = false,
    onBackClick: () -> Unit = {},
    showMenu: Boolean = false,
    showProfile: Boolean = false,
    showSearch: Boolean = false,
    centeredTitle: Boolean = false,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFFF8F9FA).copy(alpha = 0.95f))
            .statusBarsPadding()
            .padding(horizontal = 28.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left Side
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = if (centeredTitle) Modifier.width(48.dp) else Modifier.weight(1f, fill = false)
        ) {
            if (showBack) {
                Icon(
                    Icons.Outlined.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier.clickable { onBackClick() },
                    tint = MaterialTheme.colorScheme.onSurface
                )
            } else if (showMenu) {
                Icon(
                    Icons.Outlined.Menu,
                    contentDescription = "Menu",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(28.dp)
                )
            }

            if (showLogo) {
                Text(
                    "CinePass",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Crimson,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (1).sp
                )
            } else if (title != null && !centeredTitle) {
                Text(
                    title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = if (showBack) Crimson else MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Black
                )
            }
        }

        // Center (if centered)
        if (centeredTitle && title != null) {
            Text(
                title,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

    }
}
