package com.example.cinescope.data.tickets

import com.example.cinescope.data.booking.BookingRepository
import com.example.cinescope.data.local.ContentCacheStore
import com.example.cinescope.presentation.models.TicketSummary
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TicketsRepository @Inject constructor(
    private val bookingRepository: BookingRepository,
    private val contentCacheStore: ContentCacheStore
) {
    suspend fun getCachedTickets(): List<TicketSummary>? = contentCacheStore.getTickets()

    suspend fun refreshTickets(): List<TicketSummary> {
        val tickets = bookingRepository.getMyTicketSummaries()
        contentCacheStore.saveTickets(tickets)
        return tickets
    }

    suspend fun getTickets(): List<TicketSummary> = refreshTickets()

    suspend fun cancelTicket(bookingId: String) {
        bookingRepository.cancelBooking(bookingId)
    }
}
