package com.example.cinescope.presentation.tickets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cinescope.data.tickets.TicketsRepository
import com.example.cinescope.presentation.models.TicketSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class TicketFilterTab(
    val id: String,
    val label: String
)

data class TicketsUiState(
    val tabs: List<TicketFilterTab> = defaultTicketTabs(),
    val tickets: List<TicketSummary> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val cancellingBookingId: String? = null
)

@HiltViewModel
class TicketsViewModel @Inject constructor(
    private val ticketsRepository: TicketsRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        TicketsUiState(isLoading = true)
    )
    val uiState: StateFlow<TicketsUiState> = _uiState.asStateFlow()

    fun loadTickets() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val tickets = ticketsRepository.getTickets()
                    .filterNot { it.status.equals("cancelled", ignoreCase = true) }
                _uiState.value = _uiState.value.copy(
                    tabs = buildTicketTabs(tickets),
                    tickets = tickets,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Could not load tickets"
                )
            }
        }
    }

    fun cancelTicket(bookingId: String) {
        if (bookingId.isBlank()) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(cancellingBookingId = bookingId, errorMessage = null)
            try {
                ticketsRepository.cancelTicket(bookingId)
                loadTickets()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    cancellingBookingId = null,
                    errorMessage = e.message ?: "Could not cancel booking"
                )
            }
        }
    }
}

private fun defaultTicketTabs(): List<TicketFilterTab> = listOf(
    TicketFilterTab(id = "all", label = "All"),
    TicketFilterTab(id = "cinema", label = "Cinema"),
    TicketFilterTab(id = "concerts", label = "Concerts"),
    TicketFilterTab(id = "stand-up", label = "Stand-Up")
)

private fun buildTicketTabs(tickets: List<TicketSummary>): List<TicketFilterTab> {
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

private fun String.toCategoryId(): String = trim().lowercase()

private fun String.toTicketLabel(): String = split("-", " ")
    .filter { it.isNotBlank() }
    .joinToString(" ") { part ->
        part.replaceFirstChar { char ->
            if (char.isLowerCase()) char.titlecase() else char.toString()
        }
    }
