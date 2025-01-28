package com.example.livefrontcodechallenge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.livefrontcodechallenge.ui.OmdbMainScreen
import com.example.livefrontcodechallenge.ui.theme.LivefrontCodeChallengeTheme
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LivefrontCodeChallengeTheme {

                val state by viewModel.state.collectAsState()

                OmdbMainScreen(
                    state = state,
                    onQueryChanged = viewModel::onQueryChanged,
                    onErrorRetry = viewModel::onErrorRetry,
                    onEntryClicked = { imdbId ->
                        // TODO: implement this
                        Timber.d("alex: imdbId: $imdbId clicked ")
                    },
                    onBottomOfListReached = viewModel::onBottomOfListReached
                )
            }
        }
    }
}
