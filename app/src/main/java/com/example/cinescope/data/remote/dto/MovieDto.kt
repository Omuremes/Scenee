package com.example.cinescope.data.remote.dto

import java.util.UUID

import kotlinx.serialization.Serializable

@Serializable
data class CategoryDto(
    val name: String,
    val slug: String,
    val id: String
)

@Serializable
data class PosterDto(
    val url: String,
    val storage_path: String,
    val is_primary: Boolean,
    val id: String,
    val movie_id: String
)

@Serializable
data class ActorDto(
    val full_name: String,
    val photo_url: String,
    val bio: String,
    val id: String
)

@Serializable
data class EpisodeDto(
    val season_number: Int,
    val episode_number: Int,
    val title: String,
    val description: String,
    val video_url: String,
    val duration: Int,
    val id: String,
    val movie_id: String
)

@Serializable
data class MovieDto(
    val id: String,
    val name: String,
    val is_series: Boolean,
    val duration: Int,
    val seasons_count: Int,
    val average_rating: Double,
    val category: CategoryDto? = null,
    val categories: List<CategoryDto> = emptyList(),
    val primary_poster: PosterDto? = null,
    val created_at: String
)

@Serializable
data class MovieDetailDto(
    val name: String,
    val description: String,
    val is_series: Boolean,
    val duration: Int,
    val seasons_count: Int,
    val id: String,
    val average_rating: Double,
    val created_at: String,
    val updated_at: String,
    val category: CategoryDto? = null,
    val categories: List<CategoryDto> = emptyList(),
    val actors: List<ActorDto> = emptyList(),
    val posters: List<PosterDto> = emptyList(),
    val episodes: List<EpisodeDto> = emptyList(),
    val primary_poster: PosterDto? = null
)
