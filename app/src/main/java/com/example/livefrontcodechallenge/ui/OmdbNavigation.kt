package com.example.livefrontcodechallenge.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.livefrontcodechallenge.ui.detail.OmdbDetailsScreen
import com.example.livefrontcodechallenge.ui.search.OmdbSearchScreen

@Composable
fun OmdbNavigation(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = OmdbSearchScreenRoute,
        modifier = modifier
    ) {
        composable<OmdbSearchScreenRoute> {
            OmdbSearchScreen(
                onEntryClicked = { imdbId ->
                    navController.navigate(OmdbDetailsScreenRoute(imdbId = imdbId))
                }
            )
        }

        composable<OmdbDetailsScreenRoute> {
            OmdbDetailsScreen(
                onBack = {
                    navController.navigateUp()
                }
            )
        }
    }
}
