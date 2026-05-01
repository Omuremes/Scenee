package com.example.cinescope.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class EventCardDto(
    val id: String,
    val title: String,
    val type: String,
    val event_type: String? = null,
    val poster_url: String? = null,
    val image_url: String? = null,
    val city: String? = null,
    val category: CategoryDto? = null,
    val next_session_at: String? = null,
    val min_price: Double? = null,
    val average_rating: Double = 0.0,
    val is_active: Boolean = true,
    val start_datetime: String? = null,
    val venue: EventVenueDto? = null,
    val price: Double? = null,
    val available_seats: Int? = null
)

@Serializable
data class EventDetailDto(
    val id: String,
    val title: String,
    val description: String? = null,
    val type: String,
    val event_type: String? = null,
    val poster_url: String? = null,
    val image_url: String? = null,
    val city: String? = null,
    val average_rating: Double = 0.0,
    val is_active: Boolean = true,
    val available_seats: Int? = null,
    val price: Double? = null,
    val category: CategoryDto? = null,
    val sessions: List<EventSessionDto> = emptyList(),
    val venue: EventVenueDto? = null,
    val created_at: String? = null,
    val updated_at: String? = null
)

@Serializable
data class EventVenueDto(
    val id: String? = null,
    val name: String? = null,
    val title: String? = null,
    val address: String? = null,
    val city: String? = null
)

@Serializable
data class EventSessionDto(
    val id: String,
    val event_id: String,
    val starts_at: String,
    val ends_at: String? = null,
    val base_price: Double? = null,
    val pricing_type: String? = null,
    val cinema_name: String? = null,
    val hall_name: String? = null,
    val seats: List<EventSeatDto> = emptyList(),
    val created_at: String? = null,
    val updated_at: String? = null
)

@Serializable
data class EventSeatDto(
    val id: String,
    val session_id: String,
    val label: String,
    val zone: String? = null,
    val price: Double? = null,
    val is_available: Boolean = true,
    val created_at: String? = null,
    val updated_at: String? = null
)

@Serializable
data class EventReviewDto(
    val id: String,
    val event_id: String,
    val user_id: String,
    val rating: Int,
    val text: String,
    val created_at: String,
    val user: EventReviewUserDto? = null
)

@Serializable
data class EventReviewUserDto(
    val id: String,
    val username: String,
    val avatar_url: String? = null
)

@Serializable
data class EventReviewsSummaryDto(
    val average_rating: Double = 0.0,
    val reviews_count: Int = 0
)
