package com.example.livefrontcodechallenge.di

import com.example.livefrontcodechallenge.network.OmdbApiConstants
import com.example.livefrontcodechallenge.network.OmdbNetworkService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOmdbNetworkService(): OmdbNetworkService {
        val contentType = "application/json".toMediaType()
        val json = Json {
            ignoreUnknownKeys = true
        }
        return Retrofit.Builder()
            .baseUrl(OmdbApiConstants.BASE_URL)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
            .create(OmdbNetworkService::class.java)
    }

}
