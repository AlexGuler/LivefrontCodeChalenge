package com.example.livefrontcodechallenge.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OmdbRating(
    @SerialName("Source")
    val source: String,
    @SerialName("Value")
    val value: String
)
