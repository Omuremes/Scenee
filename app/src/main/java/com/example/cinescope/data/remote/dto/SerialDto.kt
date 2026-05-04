package com.example.cinescope.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class SerialsPageDto(
    val items: List<SerialDto> = emptyList(),
    val total: Int = 0,
    val offset: Int = 0,
    val limit: Int = 0,
    val has_more: Boolean = false
)

@Serializable
data class SerialDto(
    val id: String,
    val name: String,
    val poster_key: String? = null,
    val poster_url: String? = null,
    val average_rating: Double = 0.0,
    val created_at: String,
    val categories: List<CategoryDto> = emptyList()
)

@Serializable
data class SerialActorDto(
    val full_name: String,
    val photo_url: String? = null,
    val bio: String? = null,
    val id: String
)

@Serializable
data class SerialReviewUserDto(
    val id: String,
    val username: String? = null,
    val avatar_url: String? = null
)

@Serializable
data class SerialReviewDto(
    val id: String,
    val serial_id: String,
    val user_id: String,
    val rating: Double,
    val text: String? = null,
    val created_at: String,
    val user: SerialReviewUserDto
)

@Serializable
data class SerialReviewCreateRequest(
    val serial_id: String,
    val rating: Double,
    val text: String? = null
)

@Serializable
data class SerialReviewUpdateRequest(
    val rating: Double? = null,
    val text: String? = null
)

@Serializable
data class SerialEpisodeFileDto(
    val minio_bucket: String? = null,
    val minio_object_key: String? = null,
    val file_size: Long = 0,
    val mime_type: String? = null,
    val id: String,
    val episode_id: String,
    val video_url: String? = null
)

@Serializable
data class SerialEpisodeDto(
    val episode_number: Int,
    val title: String,
    val description: String? = null,
    val duration: Int = 0,
    val id: String,
    val season_id: String,
    val episode_file: SerialEpisodeFileDto? = null
)

@Serializable
data class SerialSeasonDto(
    val season_number: Int,
    val title: String? = null,
    val release_year: Int? = null,
    val id: String,
    val serial_id: String,
    val episodes: List<SerialEpisodeDto> = emptyList()
)

@Serializable
data class SerialDetailDto(
    val name: String,
    val description: String,
    val id: String,
    val poster_key: String? = null,
    val poster_url: String? = null,
    val trailer_video_key: String? = null,
    val trailer_url: String? = null,
    val average_rating: Double = 0.0,
    val created_at: String,
    val updated_at: String,
    val categories: List<CategoryDto> = emptyList(),
    val actors: List<SerialActorDto> = emptyList(),
    val seasons: List<SerialSeasonDto> = emptyList(),
    val reviews: List<SerialReviewDto> = emptyList()
)
