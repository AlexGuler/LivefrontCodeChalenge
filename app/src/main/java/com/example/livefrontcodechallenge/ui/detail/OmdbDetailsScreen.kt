package com.example.livefrontcodechallenge.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.livefrontcodechallenge.R
import com.example.livefrontcodechallenge.models.LoadableData
import com.example.livefrontcodechallenge.models.OmdbEntryDetail

@Composable
fun OmdbDetailsScreen(
    viewModel: OmdbDetailsViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Row(
                modifier = Modifier
                    .statusBarsPadding()
                    .fillMaxWidth()
            ) {
                IconButton(
                    onClick = onBack
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            OmdbDetailsScreenUi(
                state = state,
                onRetry = viewModel::onErrorRetry
            )
        }
    }
}

@Composable
fun OmdbDetailsScreenUi(
    state: LoadableData<OmdbEntryDetail>,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        when (state) {
            is LoadableData.Data -> {
                OmdbEntryDetailUi(
                    omdbEntryDetail = state.data
                )
            }


            is LoadableData.Error -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "Error Occurred",
                        fontSize = 16.sp
                    )
                    Button(
                        onClick = onRetry
                    ) {
                        Text(
                            text = "Retry",
                            fontSize = 16.sp
                        )
                    }
                }
            }

            LoadableData.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            LoadableData.Empty -> Unit
        }
    }
}
