package com.example.livefrontcodechallenge.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color.White,
    onPrimary = Color.White.copy(alpha = 0.5f),
    secondary = PurpleGrey80,
    tertiary = Pink80,
    inversePrimary = Color.Black,
    surfaceDim = Grey2,
    error = Color.Red
)

private val LightColorScheme = lightColorScheme(
    primary = Color.Black,
    onPrimary = Color.Black.copy(alpha = 0.5f),
    secondary = PurpleGrey40,
    tertiary = Pink40,
    inversePrimary = Color.White,
    surfaceDim = Grey1,
    error = Error40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun LivefrontCodeChallengeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
