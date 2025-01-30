package com.example.livefrontcodechallenge.ui.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.livefrontcodechallenge.R

@Composable
fun OmdbSearchNoResultsUi(modifier: Modifier = Modifier) {

    val noResultsTitle = stringResource(R.string.omdb_search_no_results_title)

    Column(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .semantics(mergeDescendants = true) {
                    contentDescription = noResultsTitle
                }
                .size(130.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = null,
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.BottomEnd),
                tint = MaterialTheme.colorScheme.primary
            )
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .align(Alignment.TopStart),
                tint = MaterialTheme.colorScheme.error
            )
        }

        Text(
            text = stringResource(R.string.omdb_search_no_results_title),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = stringResource(R.string.omdb_search_no_results_body),
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
