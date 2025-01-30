package com.example.livefrontcodechallenge.fakes

import com.example.livefrontcodechallenge.models.LoadableData
import com.example.livefrontcodechallenge.models.OmdbEntryDetail
import com.example.livefrontcodechallenge.models.OmdbSearchResult
import com.example.livefrontcodechallenge.models.OmdbType
import com.example.livefrontcodechallenge.repository.OmdbRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart

class FakeOmdbRepository : OmdbRepository {

    var omdbSearchResult: OmdbSearchResult? = null
    var omdbEntryDetailFlow: Flow<LoadableData<OmdbEntryDetail>> = emptyFlow()
    var throwException: Exception? = null

    override suspend fun search(query: String, page: Int, type: OmdbType?): OmdbSearchResult {
        val exception = throwException
        if (exception != null) {
            throw exception
        }
        return omdbSearchResult!!
    }

    override fun getOmdbEntryDetails(imdbId: String): Flow<LoadableData<OmdbEntryDetail>> {
        return flow {
            val exception = throwException
            if (exception != null) {
                throw exception
            }
            emitAll(omdbEntryDetailFlow)
        }.onStart {
            emit(LoadableData.Loading)
        }.catch { e ->
            emit(LoadableData.Error(e))
        }
    }
}
