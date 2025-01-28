package com.example.livefrontcodechallenge.models

sealed class OmdbError : Exception() {

    /**
     * Any exception that comes from the API that can be retried
     * Example: Internet is down, internal error
     */
    data class RetryableError(
        override val message: String?
    ) : OmdbError()

    /**
     * Exception for if the query that is being sent isn't specific enough and returns
     * too many results.
     *
     * This will also be thrown if we are at the end of a paginated list.
     */
    data class TooManyResultsError(
        override val message: String = "Too many results."
    ) : OmdbError()

    /**
     * Exception for if whatever the user is typing isn't found.
     */
    data class NoResultsError(
        override val message: String = "Movie not found!"
    ) : OmdbError()


    companion object {
        const val NO_RESULTS_ERROR_MESSAGE = "not found"
        const val TOO_MANY_RESULTS_ERROR_MESSAGE = "too many results"
    }
}
