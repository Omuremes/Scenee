package com.example.cinescope.data.home

import com.example.cinescope.data.event.EventRepository
import com.example.cinescope.presentation.models.CategoryIcon
import com.example.cinescope.presentation.models.HomeCategory
import com.example.cinescope.presentation.models.HomeSection
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeRepository @Inject constructor(
    private val eventRepository: EventRepository
) {
    suspend fun getHomeSections(): List<HomeSection> = eventRepository.getPosterSections()

    fun getCategories(): List<HomeCategory> = listOf(
        HomeCategory("Cinema", CategoryIcon.Movie, true),
        HomeCategory("Series", CategoryIcon.Series),
        HomeCategory("Concerts", CategoryIcon.Music),
        HomeCategory("Stand-Up", CategoryIcon.Mic),
        HomeCategory("Kids", CategoryIcon.Child),
        HomeCategory("Events", CategoryIcon.Stadium)
    )
}
