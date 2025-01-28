package com.example.livefrontcodechallenge.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OmdbEntryDetail(
    @SerialName("Title")
    val title: String,
    @SerialName("Year")
    val year: String,
    @SerialName("Rated")
    val rated: String,
    @SerialName("Released")
    val released: String,
    @SerialName("Runtime")
    val runtime: String,
    @SerialName("Genre")
    val genre: String,
    @SerialName("Director")
    val director: String,
    @SerialName("Writer")
    val writer: String,
    @SerialName("Actors")
    val actors: String,
    @SerialName("Plot")
    val plot: String,
    @SerialName("Country")
    val country: String,
    @SerialName("Awards")
    val awards: String,
    @SerialName("Poster")
    val poster: String,
    @SerialName("Ratings")
    val ratings: List<OmdbRating>,
    @SerialName("Metascore")
    val metascore: String,
    @SerialName("imdbRating")
    val imdbRating: String,
    @SerialName("imdbVotes")
    val imdbVotes: String,
    @SerialName("imdbID")
    val imdbID: String,
    @SerialName("BoxOffice")
    val boxOffice: String,
    @SerialName("Response")
    val result: String
)
