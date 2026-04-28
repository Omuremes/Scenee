package com.example.cinescope.data.remote

import com.example.cinescope.data.remote.dto.AuthResponse
import com.example.cinescope.data.remote.dto.LoginRequest
import com.example.cinescope.data.remote.dto.RegisterRequest
import com.example.cinescope.data.remote.dto.UserDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApiService {

    @POST("v1/auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): AuthResponse

    @POST("v1/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): AuthResponse

    @GET("v1/auth/me")
    suspend fun getMe(
        @Header("Authorization") token: String
    ): UserDto
}
