package com.example.livefrontcodechallenge.repositorytests

import com.example.livefrontcodechallenge.fakes.FakeOmdbNetworkService
import com.example.livefrontcodechallenge.models.LoadableData
import com.example.livefrontcodechallenge.models.OmdbEntryDetail
import com.example.livefrontcodechallenge.models.OmdbError
import com.example.livefrontcodechallenge.models.OmdbSearchNetworkResponse
import com.example.livefrontcodechallenge.models.OmdbSearchResult
import com.example.livefrontcodechallenge.models.OmdbType
import com.example.livefrontcodechallenge.omdbEntries
import com.example.livefrontcodechallenge.repository.OmdbRepository
import com.example.livefrontcodechallenge.repository.OmdbRepositoryImpl
import com.example.livefrontcodechallenge.testOmdbSearchResult
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class OmdbRepositoryTests {

    private lateinit var subject: OmdbRepository
    private lateinit var fakeOmdbNetworkService: FakeOmdbNetworkService

    @Before
    fun setup() {
        fakeOmdbNetworkService = FakeOmdbNetworkService()
        subject = OmdbRepositoryImpl(fakeOmdbNetworkService)
    }

    @Test
    fun search_returns_omdbSearchResult_success() = runTest {
        fakeOmdbNetworkService.omdbSearchNetworkResponse = testOmdbSearchResult
        val expected = OmdbSearchResult.Success(
            totalResults = omdbEntries.size,
            search = omdbEntries
        )

        val actual = subject.search(
            query = "spider"
        )

        assert(actual == expected)
    }

    @Test
    fun search_returns_omdbSearchResult_error_with_no_results_error() = runTest {
        fakeOmdbNetworkService.omdbSearchNetworkResponse = OmdbSearchNetworkResponse(
            response = "false",
            error = "not found"
        )
        val expected = OmdbSearchResult.Error(
            OmdbError.NoResultsError()
        )

        val actual = subject.search(
            query = "spider"
        )

        assert(actual == expected)
    }

    @Test
    fun search_returns_omdbSearchResult_error_with_too_many_results_error() = runTest {
        fakeOmdbNetworkService.omdbSearchNetworkResponse = OmdbSearchNetworkResponse(
            response = "false",
            error = "too many results"
        )
        val expected = OmdbSearchResult.Error(
            OmdbError.TooManyResultsError()
        )

        val actual = subject.search(
            query = "spider"
        )

        assert(actual == expected)
    }

    @Test
    fun search_returns_omdbSearchResult_error_with_retryable_error() = runTest {
        val exception = Exception("Test Exception")
        fakeOmdbNetworkService.throwException = exception
        val expected = OmdbSearchResult.Error(
            OmdbError.RetryableError(exception.message)
        )

        val actual = subject.search(
            query = "spider"
        )

        assert(actual == expected)
    }

    @Test
    fun getOmdbEntryDetails_returns_loading_and_data() = runTest {
        val omdbEntryDetail = OmdbEntryDetail(
            title = "title",
            rated = "rated",
            released = "released",
            runtime = "runtime",
            genre = "genre",
            plot = "plot",
            poster = "poster",
            ratings = emptyList(),
            metascore = "metascore",
            imdbRating = "imdbRating",
            imdbID = "imdbID",
            type = OmdbType.MOVIE,
            result = "result",
        )
        fakeOmdbNetworkService.omdbEntryDetail = omdbEntryDetail

        val actual = subject.getOmdbEntryDetails("id").toList()

        assert(actual.size == 2)
        assert(actual[0] == LoadableData.Loading)
        assert(actual[1] == LoadableData.Data(omdbEntryDetail))
    }
}
