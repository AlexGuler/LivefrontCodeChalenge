package com.example.livefrontcodechallenge.viewmodeltests

import com.example.livefrontcodechallenge.fakes.FakeOmdbRepository
import com.example.livefrontcodechallenge.models.OmdbError
import com.example.livefrontcodechallenge.models.OmdbSearchResult
import com.example.livefrontcodechallenge.omdbEntries
import com.example.livefrontcodechallenge.paging.PagingStatus
import com.example.livefrontcodechallenge.ui.search.OmdbMainScreenState
import com.example.livefrontcodechallenge.ui.search.OmdbSearchViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OmdbSearchViewModelTests {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var omdbRepository: FakeOmdbRepository

    @Test
    fun onQueryChanged_returns_RefreshLoading_status() = runTest {
        omdbRepository = FakeOmdbRepository()

        val subject = OmdbSearchViewModel(
            dispatcher = UnconfinedTestDispatcher(),
            omdbRepository = omdbRepository
        )

        val actual = mutableListOf<OmdbMainScreenState>()
        val testScope = TestScope(UnconfinedTestDispatcher())
        testScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            subject.state.toList(actual)
        }

        subject.onQueryChanged("spider")

        assert(actual.size == 2)
        assert(actual[0].query == "" && actual[0].omdbPagingState.pagingStatus is PagingStatus.Idle)
        assert(actual[1].query == "spider" && actual[1].omdbPagingState.pagingStatus is PagingStatus.RefreshLoading)
    }

    @Test
    fun onBottomOfListReached_returns_omdbEntries() = runTest {
        val result = OmdbSearchResult.Success(
            totalResults = omdbEntries.size,
            search = omdbEntries
        )
        omdbRepository = FakeOmdbRepository()
        omdbRepository.omdbSearchResult = result

        val subject = OmdbSearchViewModel(
            dispatcher = UnconfinedTestDispatcher(),
            omdbRepository = omdbRepository
        )

        val actual = mutableListOf<OmdbMainScreenState>()
        val testScope = TestScope(UnconfinedTestDispatcher())
        testScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            subject.state.toList(actual)
        }

        subject.onBottomOfListReached()

        assert(actual.size == 2)
        assert(actual[0].omdbPagingState.pagingStatus is PagingStatus.Idle && actual[0].omdbPagingState.items.isEmpty())
        assert(actual[1].omdbPagingState.pagingStatus is PagingStatus.Idle && actual[1].omdbPagingState.items == omdbEntries)
    }

    @Test
    fun onBottomOfListReached_returns_Error() = runTest {
        val result = OmdbSearchResult.Error(
            OmdbError.RetryableError(Exception("Test").message)
        )
        omdbRepository = FakeOmdbRepository()
        omdbRepository.omdbSearchResult = result

        val subject = OmdbSearchViewModel(
            dispatcher = UnconfinedTestDispatcher(),
            omdbRepository = omdbRepository
        )

        val actual = mutableListOf<OmdbMainScreenState>()
        val testScope = TestScope(UnconfinedTestDispatcher())
        testScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            subject.state.toList(actual)
        }

        subject.onBottomOfListReached()

        assert(actual.size == 2)
        assert(actual[0].omdbPagingState.pagingStatus is PagingStatus.Idle && actual[0].omdbPagingState.items.isEmpty())
        assert(actual[1].omdbPagingState.pagingStatus is PagingStatus.Error)
    }
}
