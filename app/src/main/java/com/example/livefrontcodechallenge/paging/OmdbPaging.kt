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

class OmdbPaging<K, T>(
    dispatcher: CoroutineDispatcher,
    private val loadMore: suspend (key: K?) -> OmdbPagingResult<K, T>
) {

    private var nextKey: K? = null
    private var job: Job? = null
    private val coroutineScope = CoroutineScope(dispatcher)

    private val _state = MutableStateFlow(OmdbPagingState<T>())
    val state: StateFlow<OmdbPagingState<T>> = _state

    fun reset() {
        Timber.d("alex: reset!")
        // cancel all coroutine jobs
        _state.update {
            it.copy(
                items = persistentListOf()
            )
        }
        nextKey = null
        Timber.d("alex: coroutineScope cancel!")
        job?.cancel(CancellationException("Resetting state"))
    }

    fun refresh() {
        Timber.d("alex: refresh!")
        nextKey = null
        job = loadData(true)
    }

    private fun loadData(isRefresh: Boolean = false): Job {
        return coroutineScope.launch {
            Timber.d("alex: paging load more")
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
                // this needs a timeout
                val pagingState = when (val result = loadMore.invoke(nextKey)) {
                    is OmdbPagingResult.Error -> {

                        Timber.d("alex: loadData got back OmdbPagingResult.Error exception: ${result.exception}")

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

                        Timber.d("alex: loadData got back success")

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

                        Timber.d("alex: loadData got back done")

                        state.value.copy(
                            pagingStatus = PagingStatus.Done
                        )
                    }
                }
                _state.update { pagingState }

//                val result = loadMore.invoke(nextKey)
//                nextKey = result.nextKey
//
//                // TODO: check if done
//                _state.update {
//                    it.copy(
//                        pagingStatus = PagingStatus.IDLE,
//                        items = if (isRefresh) {
//                            result.items.toImmutableList()
//                        } else {
//                            (it.items + result.items).toImmutableList()
//                        }
//                    )
//                }
            } catch (e: CancellationException) {
                // when loading job is cancelled reset everything
                _state.update {
                    OmdbPagingState()
                }
            } catch (e: Exception) {
                Timber.e(e, "alex: Error occurred trying to paginate data")
                _state.update {
                    it.copy(
                        pagingStatus = PagingStatus.Error(e),
                    )
                }
            }
        }
    }

    fun loadMore() {
        Timber.d("alex: load more!")
        job = loadData(false)
    }
}

sealed interface OmdbPagingResult<out K, out T> {

    data class Success<K, T>(
        val items: List<T>,
        val nextKey: K
    ) : OmdbPagingResult<K, T>

    data object Done : OmdbPagingResult<Nothing, Nothing>

    data class Error(
        val exception: Exception
    ) : OmdbPagingResult<Nothing, Nothing>
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

    // LoadMore / MoreLoading might be a better name
    data object Loading : PagingStatus

    data object RefreshLoading : PagingStatus

    data class Error(
        val exception: Exception // maybe this can just be an OmdbError instead of exception?
    ) : PagingStatus

    data object Done : PagingStatus
}
