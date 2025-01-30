package com.example.livefrontcodechallenge.ui.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade

@Composable
fun OmdbAsyncImage(
    image: String?,
    modifier: Modifier = Modifier,
    errorComposable: @Composable () -> Unit
) {

    var imageHasError by remember { mutableStateOf(false) }

    Box(modifier = modifier.clip(RoundedCornerShape(12.dp))) {
        if (imageHasError) {
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surfaceDim)
                    .matchParentSize(),
                contentAlignment = Alignment.Center
            ) {
                errorComposable.invoke()
            }
        } else {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(image)
                    .crossfade(true)
                    .build(),
                onError = {
                    imageHasError = true
                    it.result.throwable.printStackTrace()
                },
                contentDescription = "Description",
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )
        }
    }
}
