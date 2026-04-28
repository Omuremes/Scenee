package com.example.cinescope.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForwardIos
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cinescope.presentation.models.ProfileSummary
import com.example.cinescope.ui.components.toImageVector
import com.example.cinescope.ui.theme.Crimson

@Composable
fun ProfileScreen(
    isAuthenticated: Boolean,
    onLoginClick: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    if (!isAuthenticated) {
        ProfileContent(
            profile = null,
            isAuthenticated = false,
            onAction = onLoginClick
        )
        return
    }

    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is ProfileUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Crimson)
            }
        }
        is ProfileUiState.Error -> {
            Column(
                modifier = Modifier.fillMaxSize().background(Color.White).padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Outlined.ErrorOutline,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = Crimson
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Oops! Something went wrong.",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = state.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(24.dp))
                Button(
                    onClick = { viewModel.loadProfile() },
                    colors = ButtonDefaults.buttonColors(containerColor = Crimson)
                ) {
                    Text("Retry", color = Color.White)
                }
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = {
                        viewModel.logout {
                            onLoginClick() // Still navigating to login after sign out
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEF2F2))
                ) {
                    Text("Sign Out", color = Color(0xFFEF4444))
                }
            }
        }
        is ProfileUiState.Success -> {
            ProfileContent(
                profile = state.profile,
                isAuthenticated = true,
                onAction = {
                    viewModel.logout {
                        // Do nothing, appState will update and ProfileScreen will re-compose as not authenticated
                    }
                }
            )
        }
    }
}

@Composable
private fun ProfileContent(profile: ProfileSummary?, isAuthenticated: Boolean, onAction: () -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(Color.White)
    ) {
        item {
            Column(
                modifier = Modifier.fillMaxWidth().padding(top = 40.dp, bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(4.dp, Crimson.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        if (isAuthenticated) profile?.name?.take(2)?.uppercase() ?: "U" else "?",
                        style = MaterialTheme.typography.displaySmall,
                        color = Crimson,
                        fontWeight = FontWeight.Black
                    )
                }
                Spacer(Modifier.height(20.dp))
                Text(
                    if (isAuthenticated) profile?.name ?: "User" else "Guest",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    if (isAuthenticated) profile?.email ?: "" else "Sign in to access your profile",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        item {
            Text(
                "ACCOUNT SETTINGS",
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (isAuthenticated && profile != null) {
            items(profile.actions) { action ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { }
                        .padding(horizontal = 24.dp, vertical = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    Icon(
                        action.icon.toImageVector(),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(action.title, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                    Icon(Icons.AutoMirrored.Outlined.ArrowForwardIos, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
                }
                HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp), thickness = 0.5.dp, color = Color(0xFFF1F1F1))
            }
        } else {
            // Show disabled-looking items or skip them
            item {
                Text(
                    "Sign in to view account settings and history.",
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }
        item {
            Spacer(Modifier.height(24.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isAuthenticated) Color(0xFFFEF2F2) else Crimson)
                    .clickable { onAction() }
                    .padding(vertical = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    if (isAuthenticated) "Sign Out" else "Sign In",
                    color = if (isAuthenticated) Color(0xFFEF4444) else Color.White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}
