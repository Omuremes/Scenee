package com.example.cinescope.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val username: String? = null,
    val avatar_url: String? = null,
    val email: String,
    val password: String,
    val confirm_password: String
)

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class AuthResponse(
    val access_token: String,
    val token_type: String = "bearer",
    val expires_in: Int? = null
)

@Serializable
data class UserDto(
    val id: String,
    val username: String? = null,
    val email: String,
    val avatar_url: String? = null,
    val role: String
)
