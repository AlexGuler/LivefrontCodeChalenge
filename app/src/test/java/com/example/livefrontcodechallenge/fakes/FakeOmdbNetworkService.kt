package com.example.livefrontcodechallenge.fakes

import com.example.livefrontcodechallenge.models.OmdbEntryDetail
import com.example.livefrontcodechallenge.models.OmdbSearchNetworkResponse
import com.example.livefrontcodechallenge.network.OmdbNetworkService

class FakeOmdbNetworkService : OmdbNetworkService {

    var throwException: Exception? = null
    var omdbSearchNetworkResponse: OmdbSearchNetworkResponse? = null
    var omdbEntryDetail: OmdbEntryDetail? = null

    override suspend fun search(
        query: String,
        page: Int,
        type: String?
    ): OmdbSearchNetworkResponse {
        val exception = throwException
        if (exception != null) {
            throw exception
        }
        return omdbSearchNetworkResponse!!
    }

    override suspend fun getOmdbEntryDetail(imdbID: String): OmdbEntryDetail {
        val exception = throwException
        if (exception != null) {
            throw exception
        }
        return omdbEntryDetail!!
    }
}
