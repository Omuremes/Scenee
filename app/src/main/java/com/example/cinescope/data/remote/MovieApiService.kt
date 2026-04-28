package com.example.cinescope.data.remote

import com.example.cinescope.data.remote.dto.EpisodeDto
import com.example.cinescope.data.remote.dto.MovieDetailDto
import com.example.cinescope.data.remote.dto.MovieDto
import retrofit2.http.GET
import retrofit2.http.Path

interface MovieApiService {

    @GET("v1/movies/popular")
    suspend fun getPopularMovies(): List<MovieDto>

    @GET("v1/movies/new")
    suspend fun getNewMovies(): List<MovieDto>

    @GET("v1/movies/{movie_id}")
    suspend fun getMovieDetail(
        @Path("movie_id") movieId: String
    ): MovieDetailDto

    @GET("v1/movies/{movie_id}/seasons/{season_number}/episodes")
    suspend fun getSeasonEpisodes(
        @Path("movie_id") movieId: String,
        @Path("season_number") seasonNumber: Int
    ): List<EpisodeDto>
}
