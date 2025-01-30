package com.example.livefrontcodechallenge.ui.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.livefrontcodechallenge.R
import com.example.livefrontcodechallenge.models.OmdbEntryDetail
import com.example.livefrontcodechallenge.models.OmdbRating
import com.example.livefrontcodechallenge.models.OmdbType
import com.example.livefrontcodechallenge.ui.shared.OmdbAsyncImage
import com.example.livefrontcodechallenge.ui.shared.OmdbAsyncImageError
import kotlinx.collections.immutable.persistentListOf

@Composable
fun OmdbEntryDetailUi(
    omdbEntryDetail: OmdbEntryDetail,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = omdbEntryDetail.title,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.size(16.dp))

        OmdbAsyncImage(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .aspectRatio(0.6f)
                .align(Alignment.CenterHorizontally),
            image = omdbEntryDetail.poster,
            errorComposable = {
                OmdbAsyncImageError()
            }
        )

        Spacer(Modifier.size(16.dp))

        Row(
            modifier = Modifier
                .align(Alignment.CenterHorizontally),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val omdbTypeString = when (omdbEntryDetail.type) {
                OmdbType.MOVIE -> stringResource(R.string.movie)
                OmdbType.SERIES -> stringResource(R.string.series)
                OmdbType.EPISODE -> stringResource(R.string.episode)
                OmdbType.GAME -> stringResource(R.string.game)
            }
            persistentListOf(
                omdbTypeString,
                stringResource(R.string.bullet_point),
                omdbEntryDetail.rated,
                stringResource(R.string.bullet_point),
                omdbEntryDetail.runtime
            ).forEach {
                Text(
                    text = it,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }

        persistentListOf(
            stringResource(R.string.genre) to omdbEntryDetail.genre,
            stringResource(R.string.released) to omdbEntryDetail.released,
            stringResource(R.string.metascore) to omdbEntryDetail.metascore,
            stringResource(R.string.imdb_rating) to omdbEntryDetail.imdbRating,
        ).forEachIndexed { index, (title, body) ->
            Spacer(Modifier.size(if (index == 0) 8.dp else 4.dp))
            DescriptionText(
                modifier = Modifier.fillMaxWidth(),
                title = title,
                body = body
            )
        }

        Spacer(Modifier.size(16.dp))

        Text(
            text = omdbEntryDetail.plot,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.size(16.dp))

        Ratings(
            ratings = omdbEntryDetail.ratings,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.size(32.dp))
    }
}

@Composable
private fun Ratings(
    ratings: List<OmdbRating>,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(
        pageCount = { ratings.size }
    )
    HorizontalPager(
        modifier = modifier,
        pageSize = PageSize.Fixed(150.dp),
        state = pagerState,
        contentPadding = PaddingValues(horizontal = 16.dp),
        pageSpacing = 16.dp,
    ) { page ->
        Card(
            modifier = Modifier.size(150.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surfaceDim
            ),
            border = BorderStroke(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primary
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val rating = ratings[page]
                    Text(
                        text = rating.source,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.size(8.dp))
                    Text(
                        text = rating.value,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }
}


@Composable
private fun DescriptionText(
    modifier: Modifier = Modifier,
    title: String,
    body: String
) {
    Text(
        text = buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    fontWeight = FontWeight.Bold,
                )
            ) {
                append(title)
            }
            append(" ")
            append(body)
        },
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier
    )
}
