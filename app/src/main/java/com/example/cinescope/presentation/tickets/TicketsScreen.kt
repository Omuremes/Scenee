package com.example.cinescope.presentation.tickets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material.icons.outlined.LocalActivity
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.cinescope.presentation.models.TicketSummary
import com.example.cinescope.ui.components.PosterBox
import com.example.cinescope.ui.theme.Crimson

@Composable
fun TicketsScreen(
    tabs: List<String>,
    tickets: List<TicketSummary>,
    isAuthenticated: Boolean,
    isLoading: Boolean,
    errorMessage: String?,
    cancellingBookingId: String?,
    onLoginClick: () -> Unit,
    onRetry: () -> Unit,
    onCancelTicket: (String) -> Unit
) {
    if (!isAuthenticated) {
        GuestTicketsContent(onLoginClick)
        return
    }

    var selectedTab by remember(tabs) { mutableStateOf(tabs.firstOrNull() ?: "ALL") }
    var selectedTicketTitle by remember { mutableStateOf<String?>(null) }
    val filteredTickets = remember(selectedTab, tickets) {
        if (selectedTab.equals("ALL", ignoreCase = true)) {
            tickets
        } else {
            tickets.filter { it.category.equals(selectedTab, ignoreCase = true) }
        }
    }

    LaunchedEffect(selectedTab) {
        selectedTicketTitle = null
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(Color.White),
        contentPadding = PaddingValues(start = 24.dp, top = 16.dp, end = 24.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text("Your Collection", style = MaterialTheme.typography.displayLarge, fontWeight = FontWeight.ExtraBold)
                Text("Ready for your next experience?", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        when {
            isLoading -> item { TicketsLoadingBlock() }
            errorMessage != null -> item { TicketsErrorBlock(message = errorMessage, onRetry = onRetry) }
            filteredTickets.isEmpty() -> item { EmptyTicketsBlock() }
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(999.dp)).padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                tabs.forEach { tab ->
                    val selected = selectedTab == tab
                    Box(
                        modifier = Modifier.weight(1f).clip(RoundedCornerShape(999.dp)).background(if (selected) Color.White else Color.Transparent).clickable { selectedTab = tab }.padding(vertical = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(tab, style = MaterialTheme.typography.labelLarge, color = if (selected) Crimson else MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        items(filteredTickets, key = { it.id.ifBlank { it.title } }) { ticket ->
            val selected = ticket.title == selectedTicketTitle
            TicketCard(
                ticket = ticket,
                selected = selected,
                isCancelling = cancellingBookingId == ticket.id,
                onSelect = { selectedTicketTitle = ticket.title },
                onViewTicket = { selectedTicketTitle = ticket.title },
                onQrClick = { selectedTicketTitle = ticket.title },
                onCancel = { onCancelTicket(ticket.id) }
            )
        }
    }
}

@Composable
private fun TicketsLoadingBlock() {
    Box(modifier = Modifier.fillMaxWidth().height(180.dp), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = Crimson)
    }
}

@Composable
private fun TicketsErrorBlock(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Could not load tickets", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text(message, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}

@Composable
private fun EmptyTicketsBlock() {
    Box(modifier = Modifier.fillMaxWidth().height(160.dp), contentAlignment = Alignment.Center) {
        Text("No bookings yet", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun GuestTicketsContent(onLoginClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().background(Color.White).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Outlined.LocalActivity,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = Crimson.copy(alpha = 0.2f)
        )
        Spacer(Modifier.height(24.dp))
        Text(
            "Access Your Tickets",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(12.dp))
        Text(
            "Log in to view your booked tickets, upcoming events, and reservation history.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(40.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Crimson)
                .clickable { onLoginClick() }
                .padding(vertical = 18.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("Sign In", color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
private fun TicketCard(
    ticket: TicketSummary,
    selected: Boolean,
    isCancelling: Boolean,
    onSelect: () -> Unit,
    onViewTicket: () -> Unit,
    onQrClick: () -> Unit,
    onCancel: () -> Unit
) {
    val borderColor = if (selected) Crimson else Color(0xFFE5E7EB)
    val borderWidth = if (selected) 2.dp else 1.dp
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .border(borderWidth, borderColor, RoundedCornerShape(28.dp))
            .clickable { onSelect() },
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(20.dp), horizontalArrangement = Arrangement.spacedBy(18.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(width = 112.dp, height = 160.dp)) {
                PosterBox(modifier = Modifier.fillMaxSize(), theme = ticket.posterTheme)
                Text(ticket.category, modifier = Modifier.align(Alignment.TopEnd).offset(x = 8.dp, y = (-8).dp).clip(RoundedCornerShape(999.dp)).background(ticket.accent).padding(horizontal = 12.dp, vertical = 6.dp), color = Color.White, style = MaterialTheme.typography.labelSmall)
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(ticket.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    MetaRow(Icons.Outlined.CalendarToday, ticket.dateTime)
                    MetaRow(Icons.Outlined.Place, ticket.venue)
                    if (ticket.bookingReference.isNotBlank()) {
                        MetaRow(Icons.Outlined.ConfirmationNumber, ticket.bookingReference)
                    }
                    if (ticket.seatLabel.isNotBlank()) {
                        MetaRow(Icons.Outlined.LocalActivity, "Seat ${ticket.seatLabel}")
                    }
                    Text(
                        "${ticket.seatsCount} seat(s) • ${ticket.priceRange.ifBlank { ticket.totalPrice }} • ${ticket.status.uppercase()}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "View Ticket",
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(Crimson)
                            .clickable { onViewTicket() }
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall
                    )
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF9F9F9))
                            .border(1.dp, Color(0xFFF0F0F0), RoundedCornerShape(12.dp))
                            .clickable { onQrClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.ConfirmationNumber, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f), modifier = Modifier.size(18.dp))
                    }
                }
                if (!ticket.status.equals("cancelled", ignoreCase = true)) {
                    Text(
                        if (isCancelling) "Cancelling..." else "Cancel booking",
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(Color(0xFFFFF1F2))
                            .clickable(enabled = !isCancelling) { onCancel() }
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        color = Crimson,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun MetaRow(icon: ImageVector, text: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
