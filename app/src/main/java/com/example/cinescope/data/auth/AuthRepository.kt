package com.example.cinescope.data.auth

import com.example.cinescope.data.local.SessionManager
import com.example.cinescope.data.remote.AuthApiService
import com.example.cinescope.data.remote.dto.LoginRequest
import com.example.cinescope.data.remote.dto.RegisterRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val authApiService: AuthApiService,
    private val sessionManager: SessionManager
) {
    suspend fun register(request: RegisterRequest) = authApiService.register(request)
    suspend fun login(request: LoginRequest) = authApiService.login(request)
    suspend fun saveToken(token: String) = sessionManager.saveAuthToken(token)
    fun getAuthToken() = sessionManager.authToken
    suspend fun logout() = sessionManager.clearSession()
}
