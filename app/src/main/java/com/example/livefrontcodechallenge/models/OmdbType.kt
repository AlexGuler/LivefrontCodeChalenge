package com.example.livefrontcodechallenge.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class OmdbType {
    @SerialName("movie") MOVIE,
    @SerialName("series") SERIES,
    @SerialName("episode") EPISODE,
    @SerialName("game") GAME;
}
