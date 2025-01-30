package com.example.livefrontcodechallenge.models

/**
 * Omdb Search Result model
 */
sealed class OmdbSearchResult {

    data class Success(
        val totalResults: Int?,
        val search: List<OmdbEntry>
    ) : OmdbSearchResult()

    data class Error(
        val omdbError: OmdbError
    ) : OmdbSearchResult()
}
