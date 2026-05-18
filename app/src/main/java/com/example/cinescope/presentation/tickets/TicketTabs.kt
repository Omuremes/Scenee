package com.example.cinescope.presentation.tickets

import com.example.cinescope.presentation.models.TicketSummary

data class TicketFilterTab(
    val id: String,
    val label: String
)

fun defaultTicketTabs(): List<TicketFilterTab> = listOf(
    TicketFilterTab(id = "all", label = "All"),
    TicketFilterTab(id = "cinema", label = "Cinema"),
    TicketFilterTab(id = "concerts", label = "Concerts"),
    TicketFilterTab(id = "stand-up", label = "Stand-Up")
)

fun buildTicketTabs(tickets: List<TicketSummary>): List<TicketFilterTab> {
    val existingCategories = tickets
        .map { it.category.toCategoryId() }
        .filter { it.isNotBlank() }
        .toSet()

    val knownTabs = defaultTicketTabs()
        .filter { tab -> tab.id == "all" || tab.id in existingCategories }

    val customTabs = existingCategories
        .filterNot { categoryId -> knownTabs.any { it.id == categoryId } }
        .sorted()
        .map { categoryId -> TicketFilterTab(id = categoryId, label = categoryId.toTicketLabel()) }

    return (knownTabs + customTabs).ifEmpty { defaultTicketTabs().take(1) }
}

fun String.toCategoryId(): String = trim().lowercase()

fun String.toTicketLabel(): String = split('-', ' ')
    .filter { it.isNotBlank() }
    .joinToString(" ") { part ->
        part.replaceFirstChar { char ->
            if (char.isLowerCase()) char.titlecase() else char.toString()
        }
    }