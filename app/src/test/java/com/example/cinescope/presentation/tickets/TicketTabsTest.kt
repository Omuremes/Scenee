package com.example.cinescope.presentation.tickets

import com.example.cinescope.presentation.models.TicketSummary
import org.junit.Assert.assertEquals
import org.junit.Test

class TicketTabsTest {
    @Test
    fun buildTicketTabs_keeps_known_tabs_and_adds_custom_categories() {
        val tabs = buildTicketTabs(
            listOf(
                ticket(category = "Cinema"),
                ticket(category = "concerts"),
                ticket(category = "family-event")
            )
        )

        assertEquals(
            listOf(
                TicketFilterTab("all", "All"),
                TicketFilterTab("cinema", "Cinema"),
                TicketFilterTab("concerts", "Concerts"),
                TicketFilterTab("family-event", "Family Event")
            ),
            tabs
        )
    }

    @Test
    fun buildTicketTabs_falls_back_to_all_when_no_categories_exist() {
        val tabs = buildTicketTabs(emptyList())

        assertEquals(listOf(TicketFilterTab("all", "All")), tabs)
    }

    @Test
    fun toCategoryId_and_toTicketLabel_normalize_values() {
        assertEquals("stand-up", "  Stand-Up  ".toCategoryId())
        assertEquals("Kids Special", "kids-special".toTicketLabel())
    }

    private fun ticket(category: String): TicketSummary {
        return TicketSummary(
            title = "Sample",
            category = category,
            dateTime = "2026-05-17",
            venue = "Venue",
            accent = androidx.compose.ui.graphics.Color.Black,
            posterTheme = com.example.cinescope.presentation.models.PosterTheme.GoldenStage,
            id = "1"
        )
    }
}