package com.example.cinescope.presentation.series

import org.junit.Assert.assertEquals
import org.junit.Test

class SeriesSearchUiTest {
    @Test
    fun normalizeSeriesSearchQuery_trims_whitespace() {
        assertEquals("Dark", normalizeSeriesSearchQuery("  Dark  "))
    }

    @Test
    fun seriesSearchEmptyMessage_prompts_when_query_is_blank() {
        assertEquals("Start typing to search series.", seriesSearchEmptyMessage("   "))
    }

    @Test
    fun seriesSearchEmptyMessage_mentions_query_when_no_results_exist() {
        assertEquals("No series found for \"Dark\".", seriesSearchEmptyMessage("  Dark  "))
    }
}