package com.example.cinescope.data.profile

import com.example.cinescope.data.local.SessionManager
import com.example.cinescope.data.remote.AuthApiService
import com.example.cinescope.data.remote.dto.UserDto
import com.example.cinescope.presentation.models.CategoryIcon
import com.example.cinescope.presentation.models.ProfileAction
import com.example.cinescope.presentation.models.ProfileSummary
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepository @Inject constructor(
    private val authApiService: AuthApiService,
    private val sessionManager: SessionManager
) {
    suspend fun getMe(): UserDto {
        val token = sessionManager.authToken.first() ?: throw Exception("Not authenticated")
        return authApiService.getMe("Bearer $token")
    }

    suspend fun getProfileSummary(): ProfileSummary {
        val user = getMe()
        return ProfileSummary(
            name = user.username ?: user.email,
            email = user.email,
            initials = buildInitials(user.username, user.email),
            actions = listOf(
                ProfileAction("Personal Info", CategoryIcon.Person),
                ProfileAction("Payment Methods", CategoryIcon.Payments),
                ProfileAction("Order History", CategoryIcon.History)
            )
        )
    }

    suspend fun logout() = sessionManager.clearSession()

    private fun buildInitials(username: String?, email: String): String {
        if (!username.isNullOrBlank()) {
            return username.split(" ")
                .filter { it.isNotEmpty() }
                .take(2)
                .joinToString("") { it.take(1).uppercase() }
        }
        return email.take(1).uppercase()
    }
}
