package com.example.cinescope.presentation.booking

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Chair
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import com.example.cinescope.presentation.models.BookingSeatOption
import com.example.cinescope.presentation.models.BookingSelectionData
import com.example.cinescope.presentation.models.BookingSessionOption
import com.example.cinescope.ui.components.PosterBox
import com.example.cinescope.ui.theme.Crimson

@Composable
fun BookingScreen(
    state: BookingUiState,
    onSessionSelect: (String) -> Unit,
    onSeatSelect: (String) -> Unit,
    onIncreaseSeats: () -> Unit,
    onDecreaseSeats: () -> Unit,
    onConfirm: () -> Unit,
    onRetry: () -> Unit,
    onViewTickets: () -> Unit
) {
    when {
        state.isLoading -> BookingLoading()
        state.bookingReference != null -> BookingSuccess(state.bookingReference, onViewTickets)
        state.errorMessage != null && state.data == null -> BookingError(state.errorMessage, onRetry)
        state.data != null -> BookingContent(
            data = state.data,
            selectedSeatId = state.selectedSeatId,
            seatsCount = state.seatsCount,
            isSubmitting = state.isSubmitting,
            errorMessage = state.errorMessage,
            onSessionSelect = onSessionSelect,
            onSeatSelect = onSeatSelect,
            onIncreaseSeats = onIncreaseSeats,
            onDecreaseSeats = onDecreaseSeats,
            onConfirm = onConfirm
        )
    }
}

@Composable
private fun BookingLoading() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = Crimson)
    }
}

@Composable
private fun BookingError(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Could not prepare booking", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        Text(message, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(20.dp))
        Button(onClick = onRetry) { Text("Retry") }
    }
}

@Composable
private fun BookingSuccess(reference: String, onViewTickets: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().background(Color.White).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Outlined.Chair, contentDescription = null, tint = Crimson, modifier = Modifier.size(72.dp))
        Spacer(Modifier.height(18.dp))
        Text("Booking created", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(8.dp))
        Text(reference, style = MaterialTheme.typography.titleLarge, color = Crimson, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(28.dp))
        Button(onClick = onViewTickets) { Text("View tickets") }
    }
}

@Composable
private fun BookingContent(
    data: BookingSelectionData,
    selectedSeatId: String?,
    seatsCount: Int,
    isSubmitting: Boolean,
    errorMessage: String?,
    onSessionSelect: (String) -> Unit,
    onSeatSelect: (String) -> Unit,
    onIncreaseSeats: () -> Unit,
    onDecreaseSeats: () -> Unit,
    onConfirm: () -> Unit
) {
    val usesSeatPrice = data.eventTypeCode == "concerts" || data.eventTypeCode == "stand-up"
    val requiresSeat = data.eventTypeCode == "cinema" || usesSeatPrice
    val selectedSeat = data.seats.firstOrNull { it.id == selectedSeatId }
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(Color.White),
        contentPadding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(18.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(width = 104.dp, height = 144.dp)) {
                    PosterBox(modifier = Modifier.fillMaxSize(), theme = data.theme)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(data.eventType, style = MaterialTheme.typography.labelLarge, color = Crimson, fontWeight = FontWeight.Bold)
                    Text(data.title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    Spacer(Modifier.height(8.dp))
                    Text(data.venue, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        item {
            Text("Session", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(data.sessions, key = { it.id }) { session ->
                    val dynamicPrice = if (session.selected && usesSeatPrice && selectedSeat != null) {
                        selectedSeat.price
                    } else {
                        session.price
                    }
                    SessionOptionCard(
                        session = session,
                        price = dynamicPrice,
                        onClick = { onSessionSelect(session.id) }
                    )
                }
            }
        }
        if (requiresSeat) {
            item {
                Text("Seats", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    modifier = Modifier.fillMaxWidth().height(260.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(data.seats, key = { it.id }) { seat ->
                        SeatCell(
                            seat = seat,
                            selected = seat.id == selectedSeatId,
                            showPrice = usesSeatPrice,
                            onClick = { onSeatSelect(seat.id) }
                        )
                    }
                }
            }
        } else {
            item {
                Text("Tickets", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFFF8F8F8))
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("General admission", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        val left = data.availableSeats
                        Text(
                            if (left == null) "Tickets available" else "$left tickets left",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        StepperButton(icon = Icons.Outlined.Remove, onClick = onDecreaseSeats)
                        Text(seatsCount.toString(), style = MaterialTheme.typography.headlineMedium, color = Crimson, fontWeight = FontWeight.ExtraBold)
                        StepperButton(icon = Icons.Outlined.Add, onClick = onIncreaseSeats)
                    }
                }
            }
        }
        if (errorMessage != null) {
            item {
                Text(errorMessage, color = Crimson, style = MaterialTheme.typography.bodyMedium)
            }
        }
        item {
            val hasTickets = data.availableSeats == null || data.availableSeats > 0
            val enabled = !isSubmitting && hasTickets && (!requiresSeat || selectedSeatId != null)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(999.dp))
                    .background(if (enabled) Crimson else Crimson.copy(alpha = 0.45f))
                    .clickable(enabled = enabled) { onConfirm() }
                    .padding(vertical = 18.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    if (isSubmitting) "BOOKING..." else confirmLabel(data, selectedSeat, seatsCount),
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}

@Composable
private fun SessionOptionCard(session: BookingSessionOption, price: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.size(width = 220.dp, height = 116.dp).clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = if (session.selected) Crimson else Color(0xFFF8F8F8)),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(session.startsAt, color = if (session.selected) Color.White else MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
            Text(session.hall, color = if (session.selected) Color.White.copy(alpha = 0.78f) else MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(price, color = if (session.selected) Color.White else Crimson, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun SeatCell(seat: BookingSeatOption, selected: Boolean, showPrice: Boolean, onClick: () -> Unit) {
    val background = when {
        selected -> Crimson
        seat.available -> Color(0xFFF8F8F8)
        else -> Color(0xFFFFE4E6)
    }
    Column(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(14.dp))
            .background(background)
            .border(1.dp, if (selected) Crimson else Color(0xFFE5E7EB), RoundedCornerShape(14.dp))
            .clickable(enabled = seat.available) { onClick() }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(seat.label, color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
        if (seat.zone.isNotBlank()) {
            Text(seat.zone, color = if (selected) Color.White.copy(alpha = 0.76f) else MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.labelSmall)
        }
        if (showPrice && seat.price.isNotBlank()) {
            Text(
                seat.price,
                color = if (selected) Color.White else Crimson,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
            )
        }
        if (!seat.available) {
            Text("Busy", color = Crimson, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
        }
    }
}

private fun confirmLabel(data: BookingSelectionData, selectedSeat: BookingSeatOption?, seatsCount: Int): String {
    return when {
        data.eventTypeCode == "concerts" || data.eventTypeCode == "stand-up" -> {
            val price = selectedSeat?.price?.takeIf { it.isNotBlank() }
            if (price == null) "CONFIRM BOOKING" else "CONFIRM BOOKING - $price"
        }
        data.eventTypeCode == "kids" || data.eventTypeCode == "events" -> {
            val session = data.sessions.firstOrNull { it.selected }
            val total = session?.basePrice?.let { it * seatsCount }
            if (total == null) "CONFIRM BOOKING" else "CONFIRM BOOKING - ${total.formatPrice()}"
        }
        else -> {
            val session = data.sessions.firstOrNull { it.selected }
            val price = session?.price?.takeIf { it.isNotBlank() }
            if (price == null) "CONFIRM BOOKING" else "CONFIRM BOOKING - $price"
        }
    }
}

private fun Double.formatPrice(): String {
    val whole = toLong()
    val value = if (this == whole.toDouble()) whole.toString() else String.format(java.util.Locale.ENGLISH, "%.2f", this)
    return "$value KGS"
}

@Composable
private fun StepperButton(icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(42.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = Crimson)
    }
}
