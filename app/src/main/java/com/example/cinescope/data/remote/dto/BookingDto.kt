package com.example.cinescope.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class BookingCreateDto(
    val event_id: String,
    val session_id: String? = null,
    val seat_id: String? = null,
    val seats_count: Int
)

@Serializable
data class BookingResponseDto(
    val id: String,
    val user_id: String,
    val event_id: String,
    val session_id: String? = null,
    val seat_id: String? = null,
    val seats_count: Int,
    val total_price: Double = 0.0,
    val status: String,
    val booking_reference: String,
    val created_at: String,
    val updated_at: String? = null
)
