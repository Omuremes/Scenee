package com.example.cinescope.presentation.booking

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cinescope.data.booking.BookingRepository
import com.example.cinescope.presentation.models.BookingSelectionData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class BookingUiState(
    val isLoading: Boolean = true,
    val data: BookingSelectionData? = null,
    val selectedSeatId: String? = null,
    val seatsCount: Int = 1,
    val isSubmitting: Boolean = false,
    val bookingReference: String? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class BookingViewModel @Inject constructor(
    private val bookingRepository: BookingRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val eventId: String = savedStateHandle["eventId"] ?: ""
    private val initialSessionId: String? = savedStateHandle["sessionId"]

    private val _uiState = MutableStateFlow(BookingUiState())
    val uiState: StateFlow<BookingUiState> = _uiState.asStateFlow()

    init {
        load(eventId, initialSessionId)
    }

    fun load(eventId: String = this.eventId, sessionId: String? = _uiState.value.data?.selectedSessionId) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                _uiState.value = BookingUiState(
                    isLoading = false,
                    data = bookingRepository.getBookingSelection(eventId, sessionId)
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Could not load booking"
                )
            }
        }
    }

    fun selectSession(sessionId: String) {
        load(sessionId = sessionId)
    }

    fun selectSeat(seatId: String) {
        _uiState.value = _uiState.value.copy(selectedSeatId = seatId, seatsCount = 1)
    }

    fun increaseSeats() {
        val state = _uiState.value
        val data = state.data ?: return
        if (data.requiresSeat()) return
        val maxSeats = data.availableSeats ?: 10
        if (maxSeats <= 0) return
        _uiState.value = state.copy(seatsCount = (state.seatsCount + 1).coerceAtMost(maxSeats))
    }

    fun decreaseSeats() {
        val state = _uiState.value
        if (state.data?.requiresSeat() == true) return
        _uiState.value = state.copy(seatsCount = (state.seatsCount - 1).coerceAtLeast(1))
    }

    fun createBooking() {
        val state = _uiState.value
        val data = state.data ?: return
        val requiresSeat = data.requiresSeat()
        val seatId = if (requiresSeat) state.selectedSeatId else null
        if (requiresSeat && seatId == null) {
            _uiState.value = state.copy(errorMessage = "Select an available seat")
            return
        }
        if (!requiresSeat && data.availableSeats != null && data.availableSeats < state.seatsCount) {
            _uiState.value = state.copy(errorMessage = "Not enough tickets available")
            return
        }
        viewModelScope.launch {
            _uiState.value = state.copy(isSubmitting = true, errorMessage = null)
            try {
                val booking = bookingRepository.createBooking(
                    eventId = data.eventId,
                    sessionId = data.selectedSessionId,
                    seatId = seatId,
                    seatsCount = if (requiresSeat) 1 else state.seatsCount
                )
                _uiState.value = _uiState.value.copy(
                    isSubmitting = false,
                    bookingReference = booking.booking_reference
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSubmitting = false,
                    errorMessage = e.message ?: "Could not create booking"
                )
            }
        }
    }

    private fun BookingSelectionData.requiresSeat(): Boolean {
        return eventTypeCode == "cinema" || eventTypeCode == "concerts" || eventTypeCode == "stand-up"
    }
}
