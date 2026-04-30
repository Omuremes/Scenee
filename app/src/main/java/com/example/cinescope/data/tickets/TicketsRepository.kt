package com.example.cinescope.data.tickets

import androidx.compose.ui.graphics.Color
import com.example.cinescope.presentation.models.PosterTheme
import com.example.cinescope.presentation.models.TicketSummary
import com.example.cinescope.ui.theme.Crimson
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TicketsRepository @Inject constructor() {
    fun getTicketTabs(): List<String> = listOf("ALL", "CINEMA", "CONCERTS")

    fun getTickets(): List<TicketSummary> = listOf(
        TicketSummary(
            "Acoustic Sessions",
            "CONCERTS",
            "Nov 05, 2026 - 20:00",
            "Symphony Hall",
            Crimson,
            PosterTheme.CrimsonNight
        ),
        TicketSummary(
            "Midnight Echoes",
            "CINEMA",
            "Oct 15, 2026 - 21:30",
            "Grand Cinema",
            Color(0xFF9333EA),
            PosterTheme.VioletPop
        )
    )
}
