package com.example.cinescope.data.remote

import com.example.cinescope.data.remote.dto.SerialDetailDto
import com.example.cinescope.data.remote.dto.SerialReviewCreateRequest
import com.example.cinescope.data.remote.dto.SerialReviewDto
import com.example.cinescope.data.remote.dto.SerialReviewUpdateRequest
import com.example.cinescope.data.remote.dto.SerialDto
import com.example.cinescope.data.remote.dto.SerialEpisodeDto
import com.example.cinescope.data.remote.dto.SerialsPageDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface SerialApiService {
    @GET("v1/serials/")
    suspend fun getSerials(
        @Query("query") query: String? = null,
        @Query("category_id") categoryId: String? = null,
        @Query("skip") skip: Int = 0,
        @Query("limit") limit: Int = 20
    ): SerialsPageDto

    @GET("v1/serials/popular")
    suspend fun getPopularSerials(): List<SerialDto>

    @GET("v1/serials/new")
    suspend fun getNewSerials(): List<SerialDto>

    @GET("v1/serials/{serial_id}")
    suspend fun getSerialDetail(
        @Path("serial_id") serialId: String
    ): SerialDetailDto

    @GET("v1/serials/{serial_id}/seasons/{season_number}/episodes")
    suspend fun getSeasonEpisodes(
        @Path("serial_id") serialId: String,
        @Path("season_number") seasonNumber: Int
    ): List<SerialEpisodeDto>

    @POST("v1/reviews/serials")
    suspend fun createSerialReview(
        @Header("Authorization") token: String,
        @Body request: SerialReviewCreateRequest
    ): SerialReviewDto

    @PUT("v1/reviews/serials/{review_id}")
    suspend fun updateSerialReview(
        @Header("Authorization") token: String,
        @Path("review_id") reviewId: String,
        @Body request: SerialReviewUpdateRequest
    ): SerialReviewDto

    @DELETE("v1/reviews/serials/{review_id}")
    suspend fun deleteSerialReview(
        @Header("Authorization") token: String,
        @Path("review_id") reviewId: String
    )
}
