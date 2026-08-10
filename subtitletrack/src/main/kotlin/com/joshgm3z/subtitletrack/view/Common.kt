package com.joshgm3z.subtitletrack.view

import android.content.res.Configuration
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme.colorScheme
import com.joshgm3z.subtitletrack.view.theme.SubtitleTrackTheme

fun LazyListScope.listSpacing(size: Dp = 50.dp) = item {
    Spacer(Modifier.size(size))
}

@Composable
fun DarkSurface(content: @Composable () -> Unit) {
    SubtitleTrackTheme {
        Surface(color = colorScheme.background) {
            content()
        }
    }
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    device = Devices.TV_720p
)
annotation class DarkPreview
