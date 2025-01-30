package com.example.livefrontcodechallenge.ui.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.livefrontcodechallenge.R
import com.example.livefrontcodechallenge.models.OmdbError
import com.example.livefrontcodechallenge.paging.PagingStatus

@Composable
fun OmdbSearchScreen(
    viewModel: OmdbSearchViewModel = hiltViewModel(),
    onEntryClicked: (String) -> Unit
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        modifier = Modifier
            .imePadding()
            .fillMaxSize(),
        topBar = {
            Box(
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.omdb_search_title),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                OmdbSearchBar(
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .fillMaxWidth(),
                    text = state.query,
                    onValueChanged = viewModel::onQueryChanged,
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
                                            onClick = viewModel::onErrorRetry
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
                            OmdbSearchScreenList(
                                modifier = Modifier.fillMaxSize(),
                                pagingState = pagingState,
                                onEntryClicked = onEntryClicked,
                                onBottomOfListReached = viewModel::onBottomOfListReached,
                                onRetry = viewModel::onErrorRetry
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
