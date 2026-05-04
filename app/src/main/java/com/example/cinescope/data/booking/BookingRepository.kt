package com.example.cinescope.data.booking

import androidx.compose.ui.graphics.Color
import com.example.cinescope.data.local.SessionManager
import com.example.cinescope.data.remote.BookingApiService
import com.example.cinescope.data.remote.EventApiService
import com.example.cinescope.data.remote.dto.BookingCreateDto
import com.example.cinescope.data.remote.dto.BookingResponseDto
import com.example.cinescope.data.remote.dto.EventDetailDto
import com.example.cinescope.data.remote.dto.EventSeatDto
import com.example.cinescope.data.remote.dto.EventSessionDto
import com.example.cinescope.presentation.models.BookingSeatOption
import com.example.cinescope.presentation.models.BookingSelectionData
import com.example.cinescope.presentation.models.BookingSessionOption
import com.example.cinescope.presentation.models.PosterTheme
import com.example.cinescope.presentation.models.TicketSummary
import com.example.cinescope.ui.theme.Crimson
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first

@Singleton
class BookingRepository @Inject constructor(
    private val bookingApiService: BookingApiService,
    private val eventApiService: EventApiService,
    private val sessionManager: SessionManager
) {
    suspend fun getBookingSelection(eventId: String, selectedSessionId: String?): BookingSelectionData {
        val event = eventApiService.getEventDetail(eventId)
        val selectedSession = selectedSessionId
            ?: event.sessions.firstOrNull()?.id
        val seats = if (event.type.requiresSeat()) {
            selectedSession
                ?.let { runCatching { eventApiService.getSessionSeats(it, onlyAvailable = false) }.getOrDefault(emptyList()) }
                .orEmpty()
        } else {
            emptyList()
        }

        return event.toBookingSelection(selectedSession, seats)
    }

    suspend fun createBooking(
        eventId: String,
        sessionId: String?,
        seatId: String?,
        seatsCount: Int
    ): BookingResponseDto {
        return bookingApiService.createBooking(
            token = bearerToken(),
            request = BookingCreateDto(
                event_id = eventId,
                session_id = sessionId,
                seat_id = seatId,
                seats_count = seatsCount
            )
        )
    }

    suspend fun getMyTicketSummaries(): List<TicketSummary> = coroutineScope {
        val bookings = bookingApiService.getMyBookings(bearerToken())
        val events = bookings.map { booking ->
            booking to async { runCatching { eventApiService.getEventDetail(booking.event_id) }.getOrNull() }
        }
        val tickets = mutableListOf<TicketSummary>()
        for ((booking, eventDeferred) in events) {
            val event = eventDeferred.await() ?: continue
            val seats = booking.session_id
                ?.let { runCatching { eventApiService.getSessionSeats(it, onlyAvailable = false) }.getOrDefault(emptyList()) }
                .orEmpty()
            tickets.add(event.toTicketSummary(booking, seats))
        }
        tickets
    }

    suspend fun cancelBooking(bookingId: String): BookingResponseDto {
        return bookingApiService.cancelBooking(bearerToken(), bookingId)
    }

    suspend fun getBookingByReference(reference: String): BookingResponseDto {
        return bookingApiService.getBookingByReference(bearerToken(), reference)
    }

    private suspend fun bearerToken(): String {
        val token = sessionManager.authToken.first()
        require(!token.isNullOrBlank()) { "Authorization required" }
        return "Bearer $token"
    }

    private fun EventDetailDto.toBookingSelection(
        selectedSessionId: String?,
        seats: List<EventSeatDto>
    ): BookingSelectionData {
        return BookingSelectionData(
            eventId = id,
            title = title,
            eventTypeCode = type,
            eventType = type.toDisplayType(),
            venue = venueLabel(),
            availableSeats = available_seats,
            sessions = sessions.map { it.toBookingSessionOption(type, seats, selectedSessionId) },
            seats = seats.map { it.toBookingSeatOption(type) },
            selectedSessionId = selectedSessionId,
            theme = type.toPosterTheme()
        )
    }

    private fun EventSessionDto.toBookingSessionOption(
        eventType: String,
        seats: List<EventSeatDto>,
        selectedSessionId: String?
    ): BookingSessionOption {
        val startsAt = starts_at.toDateTime()
        val priceLabel = when {
            eventType.usesSeatPrice() && id == selectedSessionId -> seats.priceRangeLabel() ?: "Select a seat"
            else -> base_price?.formatPrice() ?: "By availability"
        }
        return BookingSessionOption(
            id = id,
            startsAt = startsAt?.format(sessionDateFormatter) ?: starts_at,
            hall = hall_name ?: cinema_name ?: "General admission",
            price = priceLabel,
            basePrice = base_price,
            selected = id == selectedSessionId
        )
    }

    private fun EventSeatDto.toBookingSeatOption(eventType: String): BookingSeatOption {
        return BookingSeatOption(
            id = id,
            label = label,
            zone = zone.orEmpty(),
            price = if (eventType.usesSeatPrice()) price?.formatPrice().orEmpty() else "",
            rawPrice = price,
            available = is_available
        )
    }

    private fun EventDetailDto.toTicketSummary(
        booking: BookingResponseDto,
        seats: List<EventSeatDto>
    ): TicketSummary {
        val session = sessions.firstOrNull { it.id == booking.session_id }
        val seat = seats.firstOrNull { it.id == booking.seat_id }
        val dateTime = session?.starts_at?.toDateTime()?.format(ticketDateFormatter)
            ?: booking.created_at.toDateTime()?.format(ticketDateFormatter)
            ?: booking.created_at
        val priceRange = when {
            type.usesSeatPrice() -> seats.priceRangeLabel().orEmpty()
            session?.base_price != null -> session.base_price.formatPrice()
            else -> booking.total_price.formatPrice()
        }

        return TicketSummary(
            title = title,
            category = type.toDisplayType().uppercase(Locale.ENGLISH),
            dateTime = dateTime,
            venue = listOfNotNull(
                session?.hall_name ?: session?.cinema_name,
                venueLabel().takeIf { it.isNotBlank() }
            ).distinct().joinToString(", "),
            accent = type.toAccentColor(),
            posterTheme = type.toPosterTheme(),
            id = booking.id,
            bookingReference = booking.booking_reference,
            status = booking.status,
            seatsCount = booking.seats_count,
            totalPrice = booking.total_price.formatPrice(),
            priceRange = priceRange,
            seatLabel = seat?.label.orEmpty()
        )
    }

    private fun EventDetailDto.venueLabel(): String {
        val venueName = venue?.name ?: venue?.title
        val address = venue?.address
        return listOfNotNull(venueName, address, city).distinct().joinToString(", ").ifBlank { city.orEmpty() }
    }

    private fun String.toDisplayType(): String = when (this) {
        "cinema" -> "Cinema"
        "concerts" -> "Concerts"
        "sports" -> "Sports"
        "stand-up" -> "Stand-Up"
        "kids" -> "Kids"
        "events" -> "Events"
        else -> replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ENGLISH) else it.toString() }
    }

    private fun String.requiresSeat(): Boolean = this == "cinema" || this == "concerts" || this == "stand-up"

    private fun String.usesSeatPrice(): Boolean = this == "concerts" || this == "stand-up"

    private fun List<EventSeatDto>.priceRangeLabel(): String? {
        val prices = mapNotNull { it.price }.filter { it > 0.0 }
        if (prices.isEmpty()) return null
        val min = prices.minOrNull() ?: return null
        val max = prices.maxOrNull() ?: min
        return if (min == max) min.formatPrice() else "${min.formatPrice()} - ${max.formatPrice()}"
    }

    private fun String.toPosterTheme(): PosterTheme = when (this) {
        "cinema" -> PosterTheme.VioletPop
        "concerts" -> PosterTheme.CrimsonNight
        "stand-up" -> PosterTheme.MonoSmoke
        "kids" -> PosterTheme.SoftAmber
        "sports" -> PosterTheme.CobaltRush
        else -> PosterTheme.GoldenStage
    }

    private fun String.toAccentColor(): Color = when (this) {
        "cinema" -> Color(0xFF9333EA)
        "concerts" -> Crimson
        "stand-up" -> Color(0xFF3F3F46)
        else -> Color(0xFF2563EB)
    }

    private fun Double.formatPrice(): String {
        val whole = toLong()
        val value = if (this == whole.toDouble()) whole.toString() else String.format(Locale.ENGLISH, "%.2f", this)
        return "$value KGS"
    }

    private fun String.toDateTime(): LocalDateTime? {
        return runCatching { LocalDateTime.parse(this) }.getOrElse {
            runCatching { OffsetDateTime.parse(this).atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime() }
                .getOrNull()
        }
    }

    private companion object {
        val sessionDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("EEE, dd MMM - HH:mm", Locale.ENGLISH)
        val ticketDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy - HH:mm", Locale.ENGLISH)
    }
}
