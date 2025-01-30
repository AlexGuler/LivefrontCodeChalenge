package com.example.livefrontcodechallenge.ui

import kotlinx.serialization.Serializable

@Serializable
object OmdbSearchScreenRoute

@Serializable
data class OmdbDetailsScreenRoute(val imdbId: String)
