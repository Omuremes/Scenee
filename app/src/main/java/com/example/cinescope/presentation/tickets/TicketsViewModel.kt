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
            val cachedTickets = ticketsRepository.getCachedTickets().orEmpty()
                .filterNot { it.status.equals("cancelled", ignoreCase = true) }

            _uiState.value = _uiState.value.copy(
                isLoading = cachedTickets.isEmpty(),
                errorMessage = null,
                tabs = buildTicketTabs(cachedTickets),
                tickets = cachedTickets,
                cancellingBookingId = null
            )

            try {
                val tickets = ticketsRepository.refreshTickets()
                    .filterNot { it.status.equals("cancelled", ignoreCase = true) }
                _uiState.value = _uiState.value.copy(
                    tabs = buildTicketTabs(tickets),
                    tickets = tickets,
                    isLoading = false,
                    cancellingBookingId = null,
                    errorMessage = null
                )
            } catch (e: Exception) {
                if (cachedTickets.isEmpty()) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        cancellingBookingId = null,
                        errorMessage = e.message ?: "Could not load tickets"
                    )
                }
            }
        }
    }

    fun cancelTicket(bookingId: String) {
        if (bookingId.isBlank()) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(cancellingBookingId = bookingId, errorMessage = null)
            try {
                ticketsRepository.cancelTicket(bookingId)
                _uiState.value = _uiState.value.copy(cancellingBookingId = null)
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
