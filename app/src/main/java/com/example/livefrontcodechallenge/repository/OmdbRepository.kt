package com.example.livefrontcodechallenge.repository

import com.example.livefrontcodechallenge.models.OmdbError
import com.example.livefrontcodechallenge.models.OmdbSearchNetworkResponse
import com.example.livefrontcodechallenge.models.OmdbSearchResult
import com.example.livefrontcodechallenge.models.OmdbType
import com.example.livefrontcodechallenge.network.OmdbApiConstants
import com.example.livefrontcodechallenge.network.OmdbNetworkService
import javax.inject.Inject
import timber.log.Timber

interface OmdbRepository {

    suspend fun search(
        query: String,
        page: Int = 1,
        type: OmdbType? = null
    ): OmdbSearchResult
}

class OmdbRepositoryImpl @Inject constructor(
    private val omdbNetworkService: OmdbNetworkService
) : OmdbRepository {

    override suspend fun search(
        query: String,
        page: Int,
        type: OmdbType?
    ): OmdbSearchResult {
//        val entries = OmdbTestEntriesGenerator.generateEntries(query, page, 10)
//        delay(2000L)
//        return OmdbSearchResponse(
//            search = entries,
//            totalResults = entries.size.toString(),
//            response = true.toString(),
//        )

        Timber.d("alex: search query: $query , page: $page , type: $type")


        // TODO: this should all be wrapped in its own mapper or something

        return try {
            val searchResponse = omdbNetworkService.search(
                query = query,
                page = page,
                type = type?.toOmdbApiStringType()
            )
            searchResponse.toOmdbSearchResult()
        } catch (e: Exception) {
            OmdbSearchResult.Error(OmdbError.RetryableError(e.message))
        }
    }
}

private fun OmdbSearchNetworkResponse.toOmdbSearchResult(): OmdbSearchResult {
    return if (response.lowercase().toBooleanStrict() && search != null) {
        OmdbSearchResult.Success(
            totalResults = totalResults?.toInt(),
            search = search
        )
    } else {

        Timber.d("alex: error: $error")

        val omdbError = when {
            error?.lowercase()?.contains(OmdbError.NO_RESULTS_ERROR_MESSAGE) == true -> OmdbError.NoResultsError()
            error?.lowercase()?.contains(OmdbError.TOO_MANY_RESULTS_ERROR_MESSAGE) == true -> OmdbError.TooManyResultsError()
            else -> OmdbError.RetryableError("Unknown error occurred with searchResponse.error: $error")
        }
        OmdbSearchResult.Error(omdbError)
    }
}

private fun OmdbType.toOmdbApiStringType(): String {
    return when (this) {
        OmdbType.MOVIE -> OmdbApiConstants.TYPE_MOVIE
        OmdbType.SERIES -> OmdbApiConstants.TYPE_SERIES
        OmdbType.EPISODE -> OmdbApiConstants.TYPE_EPISODE
        OmdbType.GAME -> OmdbApiConstants.TYPE_GAME
    }
}
