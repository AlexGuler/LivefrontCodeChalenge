package com.example.livefrontcodechallenge

import com.example.livefrontcodechallenge.models.OmdbEntry
import com.example.livefrontcodechallenge.models.OmdbSearchNetworkResponse
import com.example.livefrontcodechallenge.models.OmdbType

val omdbEntries = (1..5).map {
    OmdbEntry(
        imdbID = it.toString(),
        title = it.toString(),
        year = "2001",
        type = OmdbType.SERIES,
        poster = "",
    )
}

val testOmdbSearchResult = OmdbSearchNetworkResponse(
    search = omdbEntries,
    totalResults = omdbEntries.size.toString(),
    response = "true",
)
