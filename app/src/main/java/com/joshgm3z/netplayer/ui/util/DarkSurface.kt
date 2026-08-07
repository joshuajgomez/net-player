package com.joshgm3z.netplayer.ui.util

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.tv.material3.MaterialTheme.colorScheme
import com.joshgm3z.netplayer.ui.theme.NetPlayerTheme

@Composable
fun DarkSurface(content: @Composable () -> Unit) {
    NetPlayerTheme {
        Surface(color = colorScheme.background) {
            content()
        }
    }
}