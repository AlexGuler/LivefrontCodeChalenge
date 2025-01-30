package com.example.livefrontcodechallenge.paging

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Paging Helper
 */
class OmdbPaging<K, T>(
    dispatcher: CoroutineDispatcher,
    private val loadMore: suspend (key: K?) -> OmdbPagingResult<K, T>
) {

    private var nextKey: K? = null
    private var job: Job? = null
    private val coroutineScope = CoroutineScope(dispatcher)

    private val _state = MutableStateFlow(OmdbPagingState<T>())
    val state: StateFlow<OmdbPagingState<T>> = _state

    /**
     * Used to reset items of the state.
     */
    fun reset() {
        _state.update {
            it.copy(
                items = persistentListOf()
            )
        }
        nextKey = null
        job?.cancel(CancellationException("Resetting state"))
    }

    /**
     * This will completely reload the data and refresh the state.
     */
    fun refresh() {
        nextKey = null
        job = loadData(true)
    }

    /**
     * Loading of the data as well as concatenation of the list.
     */
    private fun loadData(isRefresh: Boolean = false): Job {
        return coroutineScope.launch {
            _state.update {
                it.copy(
                    pagingStatus = if (isRefresh) {
                        PagingStatus.RefreshLoading
                    } else {
                        PagingStatus.Loading
                    }
                )
            }
            try {
                val pagingState = when (val result = loadMore.invoke(nextKey)) {
                    is OmdbPagingResult.Error -> {
                        state.value.copy(
                            pagingStatus = PagingStatus.Error(result.exception),
                            items = if (isRefresh) {
                                persistentListOf()
                            } else {
                                state.value.items
                            }
                        )
                    }

                    is OmdbPagingResult.Success -> {
                        nextKey = result.nextKey
                        state.value.copy(
                            pagingStatus = PagingStatus.Idle,
                            items = if (isRefresh) {
                                result.items.toImmutableList()
                            } else {
                                (state.value.items + result.items).toImmutableList()
                            }
                        )
                    }

                    OmdbPagingResult.Done -> {
                        state.value.copy(
                            pagingStatus = PagingStatus.Done
                        )
                    }
                }
                _state.update { pagingState }
            } catch (e: CancellationException) {
                Timber.e(e, "Error occurred CancellationException")
                _state.update {
                    OmdbPagingState()
                }
            } catch (e: Exception) {
                Timber.e(e, "Error occurred Exception")
                _state.update {
                    it.copy(
                        pagingStatus = PagingStatus.Error(e)
                    )
                }
            }
        }
    }

    /**
     * Loading more data (not refresh).
     */
    fun loadMore() {
        job = loadData(false)
    }
}

data class OmdbPagingState<T>(
    val pagingStatus: PagingStatus = PagingStatus.Idle,
    val items: ImmutableList<T> = persistentListOf(),
) {
    val isInitialLoading: Boolean
        get() =
            (pagingStatus is PagingStatus.RefreshLoading || pagingStatus is PagingStatus.Loading) &&
                items.isEmpty()
}

sealed interface PagingStatus {
    data object Idle : PagingStatus

    data object Loading : PagingStatus

    data object RefreshLoading : PagingStatus

    data class Error(
        val exception: Exception
    ) : PagingStatus

    data object Done : PagingStatus
}
