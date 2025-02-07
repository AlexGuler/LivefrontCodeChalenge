package com.example.livefrontcodechallenge.ui.shared

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.livefrontcodechallenge.R

@Composable
fun OmdbAsyncImageError(
    modifier: Modifier = Modifier,
    errorIconSize: Dp = 60.dp,
    errorMessageTextStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    showErrorMessage: Boolean = true
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            modifier = Modifier.size(errorIconSize),
            imageVector = Icons.Filled.Warning,
            contentDescription = stringResource(R.string.image_loading_error_content_desc),
            tint = MaterialTheme.colorScheme.primary
        )
        if (showErrorMessage) {
            Spacer(Modifier.size(8.dp))
            Text(
                text = stringResource(R.string.image_loading_error),
                style = errorMessageTextStyle,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
