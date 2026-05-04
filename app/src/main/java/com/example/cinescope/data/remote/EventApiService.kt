package com.example.cinescope.data.remote

import com.example.cinescope.data.remote.dto.EventCardDto
import com.example.cinescope.data.remote.dto.EventDetailDto
import com.example.cinescope.data.remote.dto.EventReviewDto
import com.example.cinescope.data.remote.dto.EventReviewsSummaryDto
import com.example.cinescope.data.remote.dto.EventSeatDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface EventApiService {
    @GET("v1/events/")
    suspend fun getEvents(
        @Query("city") city: String? = null,
        @Query("type") type: String? = null,
        @Query("category_id") categoryId: String? = null,
        @Query("category") category: String? = null,
        @Query("skip") skip: Int = 0,
        @Query("limit") limit: Int = 20
    ): List<EventCardDto>

    @GET("v1/events/{event_id}")
    suspend fun getEventDetail(
        @Path("event_id") eventId: String
    ): EventDetailDto

    @GET("v1/events/sessions/{session_id}/seats")
    suspend fun getSessionSeats(
        @Path("session_id") sessionId: String,
        @Query("only_available") onlyAvailable: Boolean = true
    ): List<EventSeatDto>

    @GET("v1/events/{event_id}/seats")
    suspend fun getEventSeats(
        @Path("event_id") eventId: String,
        @Query("session_id") sessionId: String? = null,
        @Query("only_available") onlyAvailable: Boolean = true
    ): List<EventSeatDto>

    @GET("v1/events/{event_id}/reviews")
    suspend fun getReviews(
        @Path("event_id") eventId: String,
        @Query("skip") skip: Int = 0,
        @Query("limit") limit: Int = 20
    ): List<EventReviewDto>

    @GET("v1/events/{event_id}/reviews/summary")
    suspend fun getReviewsSummary(
        @Path("event_id") eventId: String
    ): EventReviewsSummaryDto
}
