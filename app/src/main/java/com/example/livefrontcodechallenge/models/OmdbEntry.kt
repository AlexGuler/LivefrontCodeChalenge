package com.example.livefrontcodechallenge.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OmdbEntry(
    @SerialName("imdbID") val imdbID: String,
    @SerialName("Title") val title: String?,
    @SerialName("Year") val year: String?,
    @SerialName("Type") val type: OmdbType?,
    @SerialName("Poster") val poster: String?,
)
