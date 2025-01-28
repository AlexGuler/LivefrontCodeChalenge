package com.example.livefrontcodechallenge.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class OmdbSearchNetworkResponse(
    @SerialName("Search") val search: List<OmdbEntry>? = null,
    @SerialName("totalResults") val totalResults: String? = null,
    @SerialName("Response") val response: String,
    @SerialName("Error") val error: String? = null,
)
