package com.example.cinescope.data.local

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.cinescope.presentation.models.HomeSection
import com.example.cinescope.presentation.models.MediaPoster
import com.example.cinescope.presentation.models.PosterTheme
import com.example.cinescope.presentation.models.TicketSummary
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

private val Context.contentCacheDataStore: DataStore<Preferences> by preferencesDataStore(name = "content_cache")

@Serializable
private data class HomeSectionCache(
    val title: String,
    val items: List<MediaPosterCache>
)

@Serializable
private data class MediaPosterCache(
    val id: String,
    val title: String,
    val subtitle: String,
    val meta: String,
    val posterUrl: String? = null,
    val theme: String
)

@Serializable
private data class TicketSummaryCache(
    val title: String,
    val category: String,
    val dateTime: String,
    val venue: String,
    val accentArgb: Int,
    val posterTheme: String,
    val posterUrl: String? = null,
    val id: String = "",
    val bookingReference: String = "",
    val status: String = "",
    val seatsCount: Int = 1,
    val totalPrice: String = "",
    val priceRange: String = "",
    val seatLabel: String = ""
)

class ContentCacheStore @Inject constructor(
    @ApplicationContext context: Context
) {
    private companion object {
        const val CACHE_VERSION = 2
    }

    private val dataStore = context.contentCacheDataStore
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    private val homeSectionsKey = stringPreferencesKey("home_sections")
    private val ticketsKey = stringPreferencesKey("tickets")
    private val cacheVersionKey = stringPreferencesKey("cache_version")

    private suspend fun ensureCacheVersion() {
        dataStore.edit { preferences ->
            val storedVersion = preferences[cacheVersionKey]?.toIntOrNull()
            if (storedVersion != CACHE_VERSION) {
                preferences.remove(homeSectionsKey)
                preferences.remove(ticketsKey)
                preferences[cacheVersionKey] = CACHE_VERSION.toString()
            }
        }
    }

    suspend fun saveHomeSections(sections: List<HomeSection>) {
        ensureCacheVersion()
        val cacheValue = json.encodeToString(
            ListSerializer(HomeSectionCache.serializer()),
            sections.map { it.toCache() }
        )
        dataStore.edit { preferences ->
            preferences[homeSectionsKey] = cacheValue
        }
    }

    suspend fun getHomeSections(): List<HomeSection>? {
        ensureCacheVersion()
        val cacheValue = dataStore.data.first()[homeSectionsKey] ?: return null
        return runCatching {
            json.decodeFromString(
                ListSerializer(HomeSectionCache.serializer()),
                cacheValue
            ).map { it.toDomain() }
        }.getOrNull()
    }

    suspend fun saveTickets(tickets: List<TicketSummary>) {
        ensureCacheVersion()
        val cacheValue = json.encodeToString(
            ListSerializer(TicketSummaryCache.serializer()),
            tickets.map { it.toCache() }
        )
        dataStore.edit { preferences ->
            preferences[ticketsKey] = cacheValue
        }
    }

    suspend fun getTickets(): List<TicketSummary>? {
        ensureCacheVersion()
        val cacheValue = dataStore.data.first()[ticketsKey] ?: return null
        return runCatching {
            json.decodeFromString(
                ListSerializer(TicketSummaryCache.serializer()),
                cacheValue
            ).map { it.toDomain() }
        }.getOrNull()
    }

    private fun HomeSection.toCache(): HomeSectionCache {
        return HomeSectionCache(
            title = title,
            items = items.map { poster ->
                MediaPosterCache(
                    id = poster.id,
                    title = poster.title,
                    subtitle = poster.subtitle,
                    meta = poster.meta,
                    posterUrl = poster.posterUrl,
                    theme = poster.theme.name
                )
            }
        )
    }

    private fun HomeSectionCache.toDomain(): HomeSection {
        return HomeSection(
            title = title,
            items = items.map { poster ->
                MediaPoster(
                    id = poster.id,
                    title = poster.title,
                    subtitle = poster.subtitle,
                    meta = poster.meta,
                    posterUrl = poster.posterUrl,
                    theme = PosterTheme.valueOf(poster.theme)
                )
            }
        )
    }

    private fun TicketSummary.toCache(): TicketSummaryCache {
        return TicketSummaryCache(
            title = title,
            category = category,
            dateTime = dateTime,
            venue = venue,
            accentArgb = accent.toArgb(),
            posterTheme = posterTheme.name,
            posterUrl = posterUrl,
            id = id,
            bookingReference = bookingReference,
            status = status,
            seatsCount = seatsCount,
            totalPrice = totalPrice,
            priceRange = priceRange,
            seatLabel = seatLabel
        )
    }

    private fun TicketSummaryCache.toDomain(): TicketSummary {
        return TicketSummary(
            title = title,
            category = category,
            dateTime = dateTime,
            venue = venue,
            accent = Color(accentArgb),
            posterTheme = PosterTheme.valueOf(posterTheme),
            posterUrl = posterUrl,
            id = id,
            bookingReference = bookingReference,
            status = status,
            seatsCount = seatsCount,
            totalPrice = totalPrice,
            priceRange = priceRange,
            seatLabel = seatLabel
        )
    }
}