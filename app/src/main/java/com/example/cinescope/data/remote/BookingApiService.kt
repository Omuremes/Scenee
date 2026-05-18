package com.example.cinescope.data.remote

import com.example.cinescope.data.remote.dto.BookingCreateDto
import com.example.cinescope.data.remote.dto.BookingResponseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface BookingApiService {
    @POST("v1/bookings/")
    suspend fun createBooking(
        @Body request: BookingCreateDto
    ): BookingResponseDto

    @GET("v1/bookings/me")
    suspend fun getMyBookings(): List<BookingResponseDto>

    @GET("v1/bookings/{booking_reference}")
    suspend fun getBookingByReference(
        @Path("booking_reference") bookingReference: String
    ): BookingResponseDto

    @PUT("v1/bookings/{booking_id}/cancel")
    suspend fun cancelBooking(
        @Path("booking_id") bookingId: String
    ): BookingResponseDto
}
