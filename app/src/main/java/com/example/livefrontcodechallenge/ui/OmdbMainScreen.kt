package com.example.livefrontcodechallenge.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.livefrontcodechallenge.OmdbMainScreenState
import com.example.livefrontcodechallenge.models.OmdbError
import com.example.livefrontcodechallenge.paging.PagingStatus

@Composable
fun OmdbMainScreen(
    state: OmdbMainScreenState,
    onQueryChanged: (String) -> Unit,
    onErrorRetry: () -> Unit,
    onEntryClicked: (String) -> Unit,
    onBottomOfListReached: () -> Unit
) {
    Scaffold(
        modifier = Modifier
            .imePadding()
            .fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = "Omdb Movie Search",
                    fontSize = 26.sp,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                OmdbSearchBar(
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .fillMaxWidth(),
                    text = state.query,
                    onValueChanged = onQueryChanged,
                )

                Spacer(Modifier.size(8.dp))

                val pagingState = state.omdbPagingState
                when {
                    pagingState.isInitialLoading  -> {
                        Box(modifier = Modifier.fillMaxSize()) {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }

                    pagingState.pagingStatus is PagingStatus.Error && pagingState.items.isEmpty() -> {
                        Box(modifier = Modifier.fillMaxSize()) {
                            Column(
                                modifier = Modifier.align(Alignment.Center),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                when (pagingState.pagingStatus.exception) {
                                    is OmdbError.NoResultsError -> {
                                        // TODO: update UI
                                        Text(
                                            text = "No Results Found!",
                                            fontSize = 16.sp
                                        )
                                    }
                                    is OmdbError.TooManyResultsError -> {
                                        // TODO: update UI
                                        Text(
                                            text = "Too Many Results try specifying more",
                                            fontSize = 16.sp
                                        )
                                    }
                                    else -> {
                                        // TODO: update UI
                                        Text(
                                            text = "Error Occurred",
                                            fontSize = 16.sp
                                        )
                                        Button(
                                            onClick = onErrorRetry
                                        ) {
                                            Text(
                                                text = "Retry",
                                                fontSize = 16.sp
                                            )
                                        }
                                    }
                                }

                            }
                        }
                    }

                    else -> {
                        if (pagingState.items.isNotEmpty()) {
                            OmdbMainScreenList(
                                modifier = Modifier.fillMaxSize(),
                                pagingState = pagingState,
                                onEntryClicked = onEntryClicked,
                                onBottomOfListReached = onBottomOfListReached,
                                onRetry = onErrorRetry
                            )
                        } else {
                            /**
                             * TODO: show empty UI
                             *  show something like nothing here try searching or whatever
                             */
                            Box(modifier = Modifier.fillMaxSize()) {
                                Text(
                                    text = "Tap on the search bar to search for something!",
                                    fontSize = 16.sp,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OmdbSearchBar(
    modifier: Modifier = Modifier,
    text: String,
    onValueChanged: (String) -> Unit
) {
    var value by remember { mutableStateOf(text) }
    TextField(
        value = value,
        onValueChange = {
            value = it
            onValueChanged.invoke(it)
        },
        label = { Text("Search") },
        maxLines = 1,
        modifier = modifier
    )
}
