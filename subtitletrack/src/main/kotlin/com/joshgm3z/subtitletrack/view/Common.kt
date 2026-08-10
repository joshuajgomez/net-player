package com.joshgm3z.subtitletrack.view

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Border
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
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

@Composable
fun CustomCard(
    onClick: () -> Unit,
    content: @Composable RowScope. () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .border(
                width = 2.dp,
                color = if (isFocused) colorScheme.primary else Color.Transparent,
                shape = RoundedCornerShape(10.dp)
            )
            .background(
                color = if (!isFocused) colorScheme.onBackground.copy(alpha = 0.05f)
                else colorScheme.primaryContainer.copy(alpha = 0.3f)
            )
            .fillMaxWidth()
            .padding(vertical = 10.dp, horizontal = 15.dp),
    ) {
        content()
    }
}