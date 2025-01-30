package com.example.livefrontcodechallenge.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OmdbEntryDetail(
    @SerialName("Title")
    val title: String,
    @SerialName("Rated")
    val rated: String,
    @SerialName("Released")
    val released: String,
    @SerialName("Runtime")
    val runtime: String,
    @SerialName("Genre")
    val genre: String,
    @SerialName("Plot")
    val plot: String,
    @SerialName("Poster")
    val poster: String,
    @SerialName("Ratings")
    val ratings: List<OmdbRating>,
    @SerialName("Metascore")
    val metascore: String,
    @SerialName("imdbRating")
    val imdbRating: String,
    @SerialName("imdbID")
    val imdbID: String,
    @SerialName("Type")
    val type: OmdbType,
    @SerialName("Response")
    val result: String
)
