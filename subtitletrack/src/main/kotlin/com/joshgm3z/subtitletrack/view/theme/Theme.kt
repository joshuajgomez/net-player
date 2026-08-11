package com.joshgm3z.subtitletrack.view.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.darkColorScheme

private val DarkColorScheme = darkColorScheme(
    primary = Orange80,
    primaryContainer = Gray30,
    tertiary = Pink80,
    background = Gray10
)

@Composable
fun SubtitleTrackTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}