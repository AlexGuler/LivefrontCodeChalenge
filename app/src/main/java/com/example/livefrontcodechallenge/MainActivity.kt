package com.example.livefrontcodechallenge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.example.livefrontcodechallenge.ui.OmdbNavigation
import com.example.livefrontcodechallenge.ui.theme.LivefrontCodeChallengeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LivefrontCodeChallengeTheme {
                OmdbNavigation(
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
