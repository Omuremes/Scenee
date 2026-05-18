package com.example.cinescope.presentation.series

fun normalizeSeriesSearchQuery(query: String): String = query.trim()

fun seriesSearchEmptyMessage(query: String): String {
    val normalizedQuery = normalizeSeriesSearchQuery(query)
    return if (normalizedQuery.isBlank()) {
        "Start typing to search series."
    } else {
        "No series found for \"$normalizedQuery\"."
    }
}