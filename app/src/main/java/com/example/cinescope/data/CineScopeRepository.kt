package com.example.cinescope.data

import javax.inject.Inject

@Deprecated(
    message = "Use page-specific repositories from data/auth, data/home, data/movie, data/profile, data/series, and data/tickets."
)
class CineScopeRepository @Inject constructor()
