package com.example.livefrontcodechallenge.ui

import android.content.res.Configuration
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.livefrontcodechallenge.R
import com.example.livefrontcodechallenge.models.OmdbEntry
import com.example.livefrontcodechallenge.models.OmdbType
import com.example.livefrontcodechallenge.paging.OmdbPagingState
import com.example.livefrontcodechallenge.paging.PagingStatus
import com.example.livefrontcodechallenge.ui.theme.LivefrontCodeChallengeTheme
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

@Composable
fun OmdbMainScreenList(
    modifier: Modifier = Modifier,
    pagingState: OmdbPagingState<OmdbEntry>,
    onEntryClicked: (String) -> Unit,
    onBottomOfListReached: () -> Unit,
    onRetry: () -> Unit
) {
    // TODO: need to figure out when we're at the bottom of the list
    val lazyListState = rememberLazyListState()
    LaunchedEffect(lazyListState) {
        /*

        val shouldStartPaginate = remember {
    derivedStateOf {
        viewModel.canPaginate && (lazyColumnListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -9) >= (lazyColumnListState.layoutInfo.totalItemsCount - 6)
    }
}

         */
        // TODO: use derivedStateOf here
        snapshotFlow { lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .filter { it != null }
            .distinctUntilChanged()
            .collect { index ->
                val lastIndex = lazyListState.layoutInfo.totalItemsCount - 3
                index ?: return@collect
                if (index >= lastIndex) {
                    onBottomOfListReached.invoke()
                }
            }
    }

//    LaunchedEffect(pagingStatus, lazyListState) {
    LaunchedEffect(pagingState.pagingStatus) {
        /**
         * Scroll to top if we are at the top of the list when [PagingStatus.LOADING_REFRESH] state
         * comes in, otherwise user will not see the loading spinner.
         */
        if (lazyListState.firstVisibleItemIndex in 0..1 && pagingState.pagingStatus == PagingStatus.RefreshLoading) {
            lazyListState.animateScrollToItem(0)
        }
    }

    LazyColumn(
        modifier = modifier,
        state = lazyListState
    ) {
        if (pagingState.pagingStatus == PagingStatus.RefreshLoading) {
            item {
                OmdbLoadingListItem(Modifier.animateContentSize())
            }
        }

        items(
            items = pagingState.items,
            key = { it.imdbID }
        ) { omdbEntry ->
            OmdbEntry(
                modifier = Modifier
                    .clickable {
                        onEntryClicked.invoke(omdbEntry.imdbID)
                    }
                    .padding(
                        horizontal = 12.dp,
                        vertical = 8.dp
                    )
                    .fillMaxWidth(),
                omdbEntry = omdbEntry
            )
        }

        when (pagingState.pagingStatus) {
            PagingStatus.Loading -> {
                item {
                    OmdbLoadingListItem()
                }
            }
            is PagingStatus.Error -> {
                item {
                    OmdbErrorListItem(
                        onRetry = onRetry
                    )
                }
            }
            else -> Unit
        }
    }
}

@Composable
private fun OmdbErrorListItem(
    modifier: Modifier = Modifier,
    onRetry: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = LOAD_ERROR_BOX_MIN_HEIGHT)
    ) {
        Button(
            modifier = Modifier.align(Alignment.Center),
            onClick = onRetry,
        ) {
            Text(
                text = stringResource(R.string.retry),
                fontSize = 17.sp
            )
        }
    }
}

@Composable
private fun OmdbLoadingListItem(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = LOAD_ERROR_BOX_MIN_HEIGHT)
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .size(40.dp)
                .align(Alignment.Center)
        )
    }
}

private val LOAD_ERROR_BOX_MIN_HEIGHT = 55.dp

@Composable
private fun OmdbEntry(
    modifier: Modifier = Modifier,
    omdbEntry: OmdbEntry
) {
    Row(
        modifier = modifier
    ) {
        // TODO: install proxy man and see if images are being cached properly
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(omdbEntry.poster)
                .crossfade(true)
                .build(),
            onLoading = {
//                Timber.d("alex: onLoading for ${omdbEntry.title}")
            },
            onSuccess = {
//                Timber.d("alex: on success for ${omdbEntry.title}")
            },
            onError = {
//                Timber.e("alex: on error for ${omdbEntry.title}", it.result.throwable)
            },
            error = null,
            contentDescription = "Description",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .size(
                    width = 60.dp,
                    height = 100.dp
                )
        )

        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold,
                        )
                    ) {
                        append(omdbEntry.title)
                    }
                    append(", ${omdbEntry.year}")
                },
                fontSize = 19.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
            )
            Text(
                text = when (omdbEntry.type) {
                    OmdbType.MOVIE -> "Movie"
                    OmdbType.SERIES -> "TV Series"
                    OmdbType.EPISODE -> "Episode"
                    OmdbType.GAME -> "Game"
                    null -> ""
                },
                fontSize = 17.sp
            )
        }
    }
}

// TODO: this is not working
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewOmdbMainScreenList() {
    LivefrontCodeChallengeTheme {
        OmdbMainScreenList(
            modifier = Modifier.fillMaxSize(),
            pagingState = OmdbPagingState(
                pagingStatus = PagingStatus.Idle,
                items = persistentListOf(
                    OmdbEntry(
                        imdbID = "tt10872600",
                        title = "Spider-Man: No Way Home",
                        year = "2021",
                        type = OmdbType.MOVIE,
                        poster = "https://m.media-amazon.com/images/M/MV5BMmFiZGZjMmEtMTA0Ni00MzA2LTljMTYtZGI2MGJmZWYzZTQ2XkEyXkFqcGc@._V1_SX300.jpg"
                    )
                )
            ),
            onEntryClicked = {},
            onBottomOfListReached = {},
            onRetry = {}
        )
    }
}
