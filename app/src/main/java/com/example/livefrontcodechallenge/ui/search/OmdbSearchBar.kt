package com.example.livefrontcodechallenge.ui.search

import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

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
