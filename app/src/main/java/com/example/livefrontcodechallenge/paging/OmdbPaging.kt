package com.example.livefrontcodechallenge.paging

import com.example.livefrontcodechallenge.models.OmdbEntry
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
                        PagingStatus.LOADING_REFRESH
                    } else {
                        PagingStatus.LOADING
                    }
                )
            }
            try {
                val pagingState = when (val result = loadMore.invoke(nextKey)) {
                    is OmdbPagingResult.Error -> {

                        Timber.d("alex: loadData got back OmdbPagingResult.Error exception: ${result.exception}")

                        state.value.copy(
                            pagingStatus = PagingStatus.ERROR,
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
                            pagingStatus = PagingStatus.IDLE,
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
                            pagingStatus = PagingStatus.DONE
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
                        pagingStatus = PagingStatus.ERROR,
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

// TODO: probably move all of this into its own file?
//data class OmdbPagingResult<K, T>(
//    val items: List<T>,
//    val nextKey: K
//)

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

//sealed interface OmdbPagingResult<K, T> {
//
//   data class Success<K, T>(
//       val items: List<T>,
//       val nextKey: K
//   ) : OmdbPagingResult<K, T>
//
//    data object Done : OmdbPagingResult<Nothing, Nothing>
//
//    data class Error(
//        val exception: Exception
//    ) : OmdbPagingResult<Nothing, Nothing>
//}

/**
 * Success,
 *
 * Done
 *
 * Error
 */

// maybe omdb paging result should have an error to it too

data class OmdbPagingState<T>(
    val pagingStatus: PagingStatus = PagingStatus.IDLE,
    val items: ImmutableList<T> = persistentListOf(),
    // TODO: have an omdb error state here?
) {
    val isInitialLoading: Boolean get() = pagingStatus == PagingStatus.LOADING_REFRESH && items.isEmpty()
    val isInitialError: Boolean get() = pagingStatus == PagingStatus.ERROR && items.isEmpty()
}

// should be a sealed interface / class where the error's should hold their own exceptions
enum class PagingStatus {
    IDLE,
    LOADING,
    LOADING_REFRESH,
    ERROR,
    DONE
}
