package com.example.livefrontcodechallenge.network

import com.example.livefrontcodechallenge.BuildConfig
import com.example.livefrontcodechallenge.models.OmdbEntryDetail
import com.example.livefrontcodechallenge.models.OmdbSearchNetworkResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Docs for this API https://www.omdbapi.com/
 */
interface OmdbNetworkService {

    @GET("?apikey=${BuildConfig.OMDB_API_KEY}")
    suspend fun search(
        @Query("s") query: String,
        // TODO: require 1-100
        @Query("page") page: Int, // 1-100
        @Query("type") type: String? = null
    ): OmdbSearchNetworkResponse

    @GET("?apikey=${BuildConfig.OMDB_API_KEY}&plot=full")
    suspend fun getOmdbEntryDetail(
        @Query("i") imdbID: String
    ): OmdbEntryDetail
}
