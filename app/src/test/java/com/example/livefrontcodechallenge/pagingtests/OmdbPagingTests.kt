package com.example.livefrontcodechallenge.pagingtests

import com.example.livefrontcodechallenge.models.OmdbEntry
import com.example.livefrontcodechallenge.models.OmdbError
import com.example.livefrontcodechallenge.omdbEntries
import com.example.livefrontcodechallenge.paging.OmdbPaging
import com.example.livefrontcodechallenge.paging.OmdbPagingResult
import com.example.livefrontcodechallenge.paging.OmdbPagingState
import com.example.livefrontcodechallenge.paging.PagingStatus
import com.example.livefrontcodechallenge.viewmodeltests.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OmdbPagingTests {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var subject: OmdbPaging<Int, OmdbEntry>
    private lateinit var fakeLoadMore: suspend (key: Int?) -> OmdbPagingResult<Int, OmdbEntry>

    @Before
    fun setup() {
        subject = OmdbPaging(
            dispatcher = UnconfinedTestDispatcher(),
            loadMore = {
                fakeLoadMore.invoke(it)
            }
        )
    }

    @Test
    fun refresh_returns_omdbentries_successfully() = runTest {
        fakeLoadMore = { key ->
            OmdbPagingResult.Success(
                items = omdbEntries,
                nextKey = key ?: 1
            )
        }
        val actual = mutableListOf<OmdbPagingState<OmdbEntry>>()
        val testScope = TestScope(UnconfinedTestDispatcher())
        testScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            subject.state.toList(actual)
        }

        subject.refresh()

        assert(actual.size == 2)
        assert(actual[0].pagingStatus == PagingStatus.Idle && actual[0].items.isEmpty())
        assert(actual[1].pagingStatus == PagingStatus.Idle && actual[1].items == omdbEntries)
    }

    @Test
    fun refresh_returns_RetryableError() = runTest {
        val retryAbleError =  OmdbError.RetryableError("test")
        fakeLoadMore = {
            OmdbPagingResult.Error(retryAbleError)
        }
        val actual = mutableListOf<OmdbPagingState<OmdbEntry>>()
        val testScope = TestScope(UnconfinedTestDispatcher())
        testScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            subject.state.toList(actual)
        }

        subject.refresh()

        assert(actual.size == 2)
        assert(actual[0].pagingStatus == PagingStatus.Idle && actual[0].items.isEmpty())
        assert(actual[1].pagingStatus == PagingStatus.Error(retryAbleError) && actual[1].items.isEmpty())
    }

    @Test
    fun refresh_returns_TooManyResultsError() = runTest {
        val tooManyResultsError =  OmdbError.TooManyResultsError()
        fakeLoadMore = {
            OmdbPagingResult.Error(tooManyResultsError)
        }
        val actual = mutableListOf<OmdbPagingState<OmdbEntry>>()
        val testScope = TestScope(UnconfinedTestDispatcher())
        testScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            subject.state.toList(actual)
        }

        subject.refresh()

        assert(actual.size == 2)
        assert(actual[0].pagingStatus == PagingStatus.Idle && actual[0].items.isEmpty())
        assert(actual[1].pagingStatus == PagingStatus.Error(tooManyResultsError) && actual[1].items.isEmpty())
    }

    @Test
    fun refresh_returns_NoResultsError() = runTest {
        val noResultsError =  OmdbError.NoResultsError()
        fakeLoadMore = { key ->
            OmdbPagingResult.Error(noResultsError)
        }
        val actual = mutableListOf<OmdbPagingState<OmdbEntry>>()
        val testScope = TestScope(UnconfinedTestDispatcher())
        testScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            subject.state.toList(actual)
        }

        subject.refresh()

        assert(actual.size == 2)
        assert(actual[0].pagingStatus == PagingStatus.Idle && actual[0].items.isEmpty())
        assert(actual[1].pagingStatus == PagingStatus.Error(noResultsError) && actual[1].items.isEmpty())
    }

    @Test
    fun reset_after_refresh_returns_empty_state() = runTest {
        fakeLoadMore = { key ->
            OmdbPagingResult.Success(
                items = omdbEntries,
                nextKey = key ?: 1
            )
        }
        val actual = mutableListOf<OmdbPagingState<OmdbEntry>>()
        val testScope = TestScope(UnconfinedTestDispatcher())
        testScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            subject.state.toList(actual)
        }

        subject.refresh()
        subject.reset()

        assert(actual.size == 3)
        assert(actual[0].pagingStatus == PagingStatus.Idle && actual[0].items.isEmpty())
        assert(actual[1].pagingStatus == PagingStatus.Idle && actual[1].items == omdbEntries)
        assert(actual[2].pagingStatus == PagingStatus.Idle && actual[2].items.isEmpty())
    }

    @Test
    fun loadMore_after_refresh_returns_more_omdbentries_successfully() = runTest {
        fakeLoadMore = { key ->
            val page = key ?: 1
            OmdbPagingResult.Success(
                items = omdbEntries,
                nextKey = page + 1
            )
        }
        val actual = mutableListOf<OmdbPagingState<OmdbEntry>>()
        val testScope = TestScope(UnconfinedTestDispatcher())
        testScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            subject.state.toList(actual)
        }

        subject.refresh()
        subject.loadMore()

        assert(actual.size == 3)
        assert(actual[0].pagingStatus == PagingStatus.Idle && actual[0].items.isEmpty())
        assert(actual[1].pagingStatus == PagingStatus.Idle && actual[1].items == omdbEntries)
        assert(actual[2].pagingStatus == PagingStatus.Idle && actual[2].items == (omdbEntries + omdbEntries))
    }
}
