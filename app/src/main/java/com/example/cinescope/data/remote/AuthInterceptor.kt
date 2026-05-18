package com.example.cinescope.data.remote

import com.example.cinescope.data.local.SessionManager
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor @Inject constructor(
    private val sessionManager: SessionManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestPath = originalRequest.url.encodedPath
        val shouldSkipAuthHeader = requestPath.isAuthPublicEndpoint()

        val token = if (shouldSkipAuthHeader) {
            null
        } else {
            runBlocking { sessionManager.authToken.first() }
        }

        val request = if (!token.isNullOrBlank() && originalRequest.header("Authorization").isNullOrBlank() && !shouldSkipAuthHeader) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            originalRequest
        }

        val response = chain.proceed(request)
        if (response.code == 401 && !shouldSkipAuthHeader) {
            runBlocking { sessionManager.clearSession() }
        }
        return response
    }

    private fun String.isAuthPublicEndpoint(): Boolean {
        return this == "/v1/auth/login" ||
            this == "/v1/auth/register" ||
            this == "/v1/auth/refresh" ||
            this == "/v1/auth/sync"
    }
}