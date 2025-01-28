package com.example.livefrontcodechallenge.models

object OmdbTestEntriesGenerator {

    // TODO: remove this later
    fun generateEntries(
        query: String,
        lastNum: Int,
        size: Int
    ): List<OmdbEntry> {
        val last = lastNum + size
        return ((lastNum + 1)..last).map { num ->
            OmdbEntry(
                imdbID = "$num",
                title = "Movie Title $num , query: $query",
                year = (1900 + num).toString(),
                type = OmdbType.MOVIE,
                poster = null,
            )
        }
    }
}
