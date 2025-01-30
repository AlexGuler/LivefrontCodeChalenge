package com.example.livefrontcodechallenge.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.livefrontcodechallenge.di.IODispatcher
import com.example.livefrontcodechallenge.models.OmdbEntry
import com.example.livefrontcodechallenge.models.OmdbError
import com.example.livefrontcodechallenge.models.OmdbSearchResult
import com.example.livefrontcodechallenge.paging.OmdbPaging
import com.example.livefrontcodechallenge.paging.OmdbPagingResult
import com.example.livefrontcodechallenge.paging.OmdbPagingState
import com.example.livefrontcodechallenge.paging.PagingStatus
import com.example.livefrontcodechallenge.repository.OmdbRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class OmdbSearchViewModel @Inject constructor(
    @IODispatcher dispatcher: CoroutineDispatcher,
    private val omdbRepository: OmdbRepository
) : ViewModel() {

    private val omdbPaging = OmdbPaging(
        dispatcher = dispatcher,
        loadMore = ::loadMore // TODO make loadMore a usecase and then just do useCase::invoke here
    )

    // TODO: Instead of observing query changes have a query: MutableStateFlow<String> and just
    //  combine it with omdb paging state and transform it to the ui state

    private val _state: MutableStateFlow<OmdbMainScreenState> = MutableStateFlow(
        OmdbMainScreenState(
            omdbPagingState = omdbPaging.state.value
        )
    )
    val state: StateFlow<OmdbMainScreenState> = _state

    init {
        viewModelScope.launch {
            omdbPaging.state
                .map {
                    state.value.copy(omdbPagingState = it)
                }
                .collect(_state)
        }
        listenForQueryChanges()
    }

    // TODO: put this in a use case
    private suspend fun loadMore(key: Int?): OmdbPagingResult<Int, OmdbEntry> {
        val page = key ?: 1
        val response = omdbRepository.search(
            query = state.value.query,
            page = page
        )
        return when (response) {
            is OmdbSearchResult.Error -> {
                response.omdbError.let { omdbError ->
                    /**
                     * If no results are found and we have items then we're at the end of pagination.
                     */
                    if (omdbError is OmdbError.NoResultsError && page > 1) {
                        OmdbPagingResult.Done
                    } else {
                        OmdbPagingResult.Error(response.omdbError)
                    }
                }
            }
            is OmdbSearchResult.Success -> {
                OmdbPagingResult.Success(
                    items = response.search,
                    nextKey = page + 1
                )
            }
        }
    }

    fun onQueryChanged(query: String) {
        _state.update {
            it.copy(
                query = query
            )
        }
    }

    @OptIn(FlowPreview::class)
    private fun listenForQueryChanges() {
        viewModelScope.launch {
            state
                .map { it.query }
                .distinctUntilChanged()
                .onEach { query ->
                    _state.update {
                        it.copy(
                            omdbPagingState = it.omdbPagingState.copy(
                                pagingStatus = if (query.isNotBlank()) {
                                    PagingStatus.RefreshLoading
                                } else {
                                    PagingStatus.Idle
                                }
                            )
                        )
                    }
                }
                .debounce { if (it.isEmpty() || it.isBlank()) 0L else 1000L }
                .collect { text ->
                    if (text.isNotBlank()) {
                        omdbPaging.refresh()
                    } else {
                        omdbPaging.reset()
                    }
                }
        }
    }

    fun onErrorRetry() {
        omdbPaging.loadMore()
    }

    fun onBottomOfListReached() {
        val currentPagingState = state.value.omdbPagingState.pagingStatus
        if (currentPagingState == PagingStatus.Idle) {
            omdbPaging.loadMore()
        }
    }
}

data class OmdbMainScreenState(
    val query: String = "",
    val omdbPagingState: OmdbPagingState<OmdbEntry>
)
