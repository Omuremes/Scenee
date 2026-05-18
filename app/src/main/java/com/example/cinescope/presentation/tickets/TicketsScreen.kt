package com.example.cinescope.presentation.tickets

import android.net.Uri
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material.icons.outlined.LocalActivity
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.cinescope.presentation.models.TicketSummary
import com.example.cinescope.ui.components.PosterBox
import com.example.cinescope.ui.theme.Crimson

@Composable
fun TicketsScreen(
    tabs: List<TicketFilterTab>,
    tickets: List<TicketSummary>,
    isAuthenticated: Boolean,
    isLoading: Boolean,
    errorMessage: String?,
    cancellingBookingId: String?,
    onLoginClick: () -> Unit,
    onRetry: () -> Unit,
    onViewTicket: (TicketSummary) -> Unit,
    onCancelTicket: (String) -> Unit
) {
    if (!isAuthenticated) {
        GuestTicketsContent(onLoginClick)
        return
    }

    var selectedTabId by remember(tabs) { mutableStateOf(tabs.firstOrNull()?.id ?: "all") }
    val filteredTickets = remember(selectedTabId, tickets) {
        if (selectedTabId == "all") {
            tickets
        } else {
            tickets.filter { it.category.toTicketCategoryId() == selectedTabId }
        }
    }

    LaunchedEffect(tabs) {
        if (tabs.none { it.id == selectedTabId }) {
            selectedTabId = tabs.firstOrNull()?.id ?: "all"
        }
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
        item {
            TicketTabs(
                tabs = tabs,
                selectedTabId = selectedTabId,
                tickets = tickets,
                onTabSelected = { selectedTabId = it }
            )
        }
        when {
            isLoading -> item { TicketsLoadingBlock() }
            errorMessage != null -> item { TicketsErrorBlock(message = errorMessage, onRetry = onRetry) }
            filteredTickets.isEmpty() -> item { EmptyTicketsBlock() }
        }
        items(filteredTickets, key = { it.id.ifBlank { it.title } }) { ticket ->
            TicketCard(
                ticket = ticket,
                isCancelling = cancellingBookingId == ticket.id,
                onViewTicket = { onViewTicket(ticket) },
                onCancel = { onCancelTicket(ticket.id) }
            )
        }
    }
}

@Composable
private fun TicketTabs(
    tabs: List<TicketFilterTab>,
    selectedTabId: String,
    tickets: List<TicketSummary>,
    onTabSelected: (String) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(vertical = 2.dp)
    ) {
        items(tabs, key = { it.id }) { tab ->
            val selected = tab.id == selectedTabId
            val count = if (tab.id == "all") {
                tickets.size
            } else {
                tickets.count { it.category.toTicketCategoryId() == tab.id }
            }
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(if (selected) Crimson else Color(0xFFF5F5F5))
                    .border(1.dp, if (selected) Crimson else Color(0xFFE8E8E8), RoundedCornerShape(999.dp))
                    .clickable { onTabSelected(tab.id) }
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    ticketTabIcon(tab.id),
                    contentDescription = null,
                    tint = if (selected) Color.White else Crimson,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    tab.label,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(if (selected) Color.White.copy(alpha = 0.2f) else Color.White)
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        count.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
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
    isCancelling: Boolean,
    onViewTicket: () -> Unit,
    onCancel: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(width = 86.dp, height = 122.dp)) {
                TicketPoster(
                    ticket = ticket,
                    modifier = Modifier.fillMaxSize(),
                    cornerRadius = 24
                )
                Text(
                    ticket.category.toTicketLabel(),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .offset(x = (-6).dp, y = (-6).dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(ticket.accent)
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1
                )
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        ticket.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    MetaRow(Icons.Outlined.CalendarToday, ticket.dateTime)
                    if (ticket.venue.isNotBlank()) {
                        MetaRow(Icons.Outlined.Place, ticket.venue, maxLines = 1)
                    }
                    Text(
                        ticket.priceLabel(),
                        style = MaterialTheme.typography.labelSmall,
                        color = Crimson,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onViewTicket,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Crimson),
                        shape = RoundedCornerShape(999.dp)
                    ) {
                        Text("View Ticket", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                    }
                    if (!ticket.isCancelled()) {
                        Text(
                            if (isCancelling) "Cancelling..." else "Cancel",
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(999.dp))
                                .background(Color(0xFFFFF1F2))
                                .clickable(enabled = !isCancelling) { onCancel() }
                                .padding(horizontal = 14.dp, vertical = 11.dp),
                            color = Crimson,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun TicketPoster(
    ticket: TicketSummary,
    modifier: Modifier,
    cornerRadius: Int
) {
    if (!ticket.posterUrl.isNullOrBlank()) {
        AsyncImage(
            model = ticket.posterUrl,
            contentDescription = ticket.title,
            modifier = modifier.clip(RoundedCornerShape(cornerRadius.dp)),
            contentScale = ContentScale.Crop
        )
    } else {
        PosterBox(modifier = modifier, theme = ticket.posterTheme)
    }
}

@Composable
private fun MetaRow(icon: ImageVector, text: String, maxLines: Int = Int.MAX_VALUE) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
        Text(
            text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun TicketDetailScreen(
    ticketId: String,
    tickets: List<TicketSummary>,
    isLoading: Boolean,
    errorMessage: String?,
    cancellingBookingId: String?,
    onRetry: () -> Unit,
    onCancelTicket: (String) -> Unit
) {
    val decodedTicketId = remember(ticketId) { Uri.decode(ticketId) }
    val ticket = remember(decodedTicketId, tickets) {
        tickets.firstOrNull { item ->
            item.id == decodedTicketId || item.bookingReference == decodedTicketId
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(Color.White),
        contentPadding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        when {
            isLoading -> item { TicketsLoadingBlock() }
            errorMessage != null -> item { TicketsErrorBlock(message = errorMessage, onRetry = onRetry) }
            ticket == null -> item { MissingTicketBlock(onRetry) }
            else -> {
                item { TicketPass(ticket) }
                item {
                    TicketDetailsBlock(
                        ticket = ticket,
                        isCancelling = cancellingBookingId == ticket.id,
                        onCancel = { onCancelTicket(ticket.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun MissingTicketBlock(onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 56.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(Icons.Outlined.ConfirmationNumber, contentDescription = null, tint = Crimson.copy(alpha = 0.35f), modifier = Modifier.size(72.dp))
        Text("Ticket not found", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text(
            "Refresh your bookings and try again.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Button(onClick = onRetry, colors = ButtonDefaults.buttonColors(containerColor = Crimson)) {
            Text("Refresh")
        }
    }
}

@Composable
private fun TicketPass(ticket: TicketSummary) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF18181B)),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(22.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(18.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(width = 112.dp, height = 156.dp)) {
                    TicketPoster(
                        ticket = ticket,
                        modifier = Modifier.fillMaxSize(),
                        cornerRadius = 28
                    )
                }
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(ticket.category.toTicketLabel(), style = MaterialTheme.typography.labelSmall, color = ticket.accent, fontWeight = FontWeight.Bold)
                    Text(ticket.title, style = MaterialTheme.typography.headlineMedium, color = Color.White, fontWeight = FontWeight.ExtraBold)
                    Text(ticket.dateTime, style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.72f))
                }
            }
            Box(
                modifier = Modifier.fillMaxWidth().height(1.dp).background(Color.White.copy(alpha = 0.12f))
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Booking ref", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.5f))
                    Text(ticket.bookingReference.ifBlank { ticket.id }, style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
                }
                PseudoQrCode(ticket.bookingReference.ifBlank { ticket.id })
            }
        }
    }
}

@Composable
private fun TicketDetailsBlock(
    ticket: TicketSummary,
    isCancelling: Boolean,
    onCancel: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Ticket details", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            DetailRow("Date and time", ticket.dateTime)
            DetailRow("Venue", ticket.venue.ifBlank { "Venue will be announced" })
            DetailRow("Seat", ticket.seatLabel.ifBlank { "General admission" })
            DetailRow("Quantity", "${ticket.seatsCount} seat(s)")
            DetailRow("Price", ticket.priceLabel())
            if (!ticket.isCancelled()) {
                Button(
                    onClick = onCancel,
                    enabled = !isCancelling,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFF1F2), contentColor = Crimson),
                    shape = RoundedCornerShape(999.dp)
                ) {
                    Text(if (isCancelling) "Cancelling..." else "Cancel booking", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            value,
            modifier = Modifier.weight(1f).padding(start = 16.dp),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.End,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun PseudoQrCode(seed: String) {
    val hash = remember(seed) { seed.hashCode() }
    Column(
        modifier = Modifier
            .size(86.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        repeat(7) { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                repeat(7) { column ->
                    val filled = row in 0..1 && column in 0..1 ||
                        row in 5..6 && column in 0..1 ||
                        row in 0..1 && column in 5..6 ||
                        ((hash shr ((row + column) % 16)) and 1) == 1
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .clip(RoundedCornerShape(1.dp))
                            .background(if (filled) Color(0xFF18181B) else Color.Transparent)
                    )
                }
            }
        }
    }
}

private fun ticketTabIcon(categoryId: String): ImageVector = when (categoryId) {
    "cinema" -> Icons.Outlined.Movie
    "concerts" -> Icons.Outlined.MusicNote
    "stand-up" -> Icons.Outlined.LocalActivity
    else -> Icons.Outlined.ConfirmationNumber
}

private fun TicketSummary.priceLabel(): String = priceRange.ifBlank { totalPrice }.ifBlank { "Price unavailable" }

private fun TicketSummary.isCancelled(): Boolean = status.equals("cancelled", ignoreCase = true)

private fun String.toTicketCategoryId(): String = trim().lowercase()
