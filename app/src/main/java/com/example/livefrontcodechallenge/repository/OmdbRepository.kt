package com.example.livefrontcodechallenge.repository

import com.example.livefrontcodechallenge.models.LoadableData
import com.example.livefrontcodechallenge.models.OmdbEntryDetail
import com.example.livefrontcodechallenge.models.OmdbError
import com.example.livefrontcodechallenge.models.OmdbSearchNetworkResponse
import com.example.livefrontcodechallenge.models.OmdbSearchResult
import com.example.livefrontcodechallenge.models.OmdbType
import com.example.livefrontcodechallenge.network.OmdbApiConstants
import com.example.livefrontcodechallenge.network.OmdbNetworkService
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import timber.log.Timber

interface OmdbRepository {

    suspend fun search(
        query: String,
        page: Int = 1,
        type: OmdbType? = null
    ): OmdbSearchResult

    fun getOmdbEntryDetails(imdbId: String): Flow<LoadableData<OmdbEntryDetail>>
}

class OmdbRepositoryImpl @Inject constructor(
    private val omdbNetworkService: OmdbNetworkService
) : OmdbRepository {

    override suspend fun search(
        query: String,
        page: Int,
        type: OmdbType?
    ): OmdbSearchResult {
        Timber.d("alex: search query: $query , page: $page , type: $type")
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

    override fun getOmdbEntryDetails(imdbId: String): Flow<LoadableData<OmdbEntryDetail>> {
        return flow<LoadableData<OmdbEntryDetail>> {
            emit(LoadableData.Data(omdbNetworkService.getOmdbEntryDetail(imdbId)))
        }.onStart {
            emit(LoadableData.Loading)
        }.catch { throwable ->
            Timber.e(throwable, "alex: error occurred trying to get omdb entry details")
            emit(LoadableData.Error(throwable))
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
