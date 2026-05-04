package com.example.cinescope.data.tickets

import com.example.cinescope.data.booking.BookingRepository
import com.example.cinescope.presentation.models.TicketSummary
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TicketsRepository @Inject constructor(
    private val bookingRepository: BookingRepository
) {
    fun getTicketTabs(): List<String> = listOf("ALL", "CINEMA", "CONCERTS", "STAND-UP")

    suspend fun getTickets(): List<TicketSummary> = bookingRepository.getMyTicketSummaries()

    suspend fun cancelTicket(bookingId: String) {
        bookingRepository.cancelBooking(bookingId)
    }
}
