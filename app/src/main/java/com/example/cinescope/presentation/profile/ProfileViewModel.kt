package com.example.cinescope.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cinescope.data.CineScopeRepository
import com.example.cinescope.presentation.models.CategoryIcon
import com.example.cinescope.presentation.models.ProfileAction
import com.example.cinescope.presentation.models.ProfileSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

sealed class ProfileUiState {
    object Loading : ProfileUiState()
    data class Success(val profile: ProfileSummary) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: CineScopeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            try {
                val user = repository.getMe()
                val profileSummary = ProfileSummary(
                    name = user.username ?: "User",
                    email = user.email,
                    initials = (user.username ?: "U").take(2).uppercase(),
                    actions = listOf(
                        ProfileAction("Personal Info", CategoryIcon.Person),
                        ProfileAction("Payment Methods", CategoryIcon.Payments),
                        ProfileAction("Order History", CategoryIcon.History)
                    )
                )
                _uiState.value = ProfileUiState.Success(profileSummary)
            } catch (e: HttpException) {
                if (e.code() == 401) {
                    repository.logout()
                } else {
                    _uiState.value = ProfileUiState.Error("Network error: ${e.code()}")
                }
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error(e.message ?: "Failed to load profile")
            }
        }
    }

    fun logout(onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.logout()
            onComplete()
        }
    }
}
