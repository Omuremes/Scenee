package com.example.cinescope.data.remote

import com.example.cinescope.data.remote.dto.SerialDetailDto
import com.example.cinescope.data.remote.dto.SerialDto
import com.example.cinescope.data.remote.dto.SerialEpisodeDto
import com.example.cinescope.data.remote.dto.SerialsPageDto
import retrofit2.http.GET
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
}
