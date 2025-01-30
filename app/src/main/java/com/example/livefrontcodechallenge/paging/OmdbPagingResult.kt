package com.example.livefrontcodechallenge.paging

/**
 * Represents the possible outcomes when paging happens
 * @see [OmdbPagingResult.Success] - Successful page loaded
 * @see [OmdbPagingResult.Done] - No more pages to load
 * @see [OmdbPagingResult.Error] - Any error occurred when paging
 */
sealed interface OmdbPagingResult<out K, out T> {

    data class Success<K, T>(
        val items: List<T>,
        val nextKey: K
    ) : OmdbPagingResult<K, T>

    data object Done : OmdbPagingResult<Nothing, Nothing>

    data class Error(
        val exception: Exception
    ) : OmdbPagingResult<Nothing, Nothing>
}
