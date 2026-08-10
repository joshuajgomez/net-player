package com.joshgm3z.subtitletrack.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Border
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme.colorScheme
import androidx.tv.material3.RadioButton
import androidx.tv.material3.RadioButtonDefaults
import androidx.tv.material3.Text
import com.joshgm3z.subtitletrack.util.languageName
import com.joshgm3z.subtitletrack.view.theme.SubtitleTrackTheme

@Composable
fun AudioTracks(
    listState: ListState.AudioTracks,
    onClick: (TrackInfo) -> Unit = {},
) {
    TracksContent(list = listState.list, onClick = onClick)
}

@Composable
fun SubtitleTracks(
    listState: ListState.SubtitleTracks,
    onClick: (TrackInfo) -> Unit = {},
) {
    TracksContent(list = listState.list, onClick = onClick)
}

@Composable
private fun TracksContent(
    list: List<TrackInfo>,
    onClick: (TrackInfo) -> Unit = {},
) {
    var selectedTrackInfo by remember {
        mutableStateOf(
            list.firstOrNull { it.isSelected }
        )
    }
    if (list.isEmpty()) Text(
        text = "No tracks loaded",
        style = typography.bodyLarge,
        color = colorScheme.onBackground,
        modifier = Modifier.padding(top = 30.dp)
    ) else LazyColumn(
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        items(list) {
            TrackItem(
                trackInfo = it,
                selected = selectedTrackInfo?.id == it.id,
                onClick = {
                    selectedTrackInfo = it
                    onClick(it)
                }
            )
        }
    }
}

@Composable
private fun TrackItem(
    trackInfo: TrackInfo,
    selected: Boolean = false,
    onClick: () -> Unit = {}
) {
    CustomCard(onClick = onClick) {
        RadioButton(
            selected = selected,
            onClick = {},
            colors = RadioButtonDefaults.colors(
                selectedColor = colorScheme.primary,
                unselectedColor = colorScheme.onBackground.copy(alpha = 0.4f)
            ),
        )
        Spacer(Modifier.size(10.dp))
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
        ) {
            Text(
                text = if (trackInfo.disableTrack) "Disabled"
                else trackInfo.language.languageName(),
                overflow = TextOverflow.Ellipsis,
                color = colorScheme.onBackground.copy(alpha = 0.9f),
            )
            if (!trackInfo.disableTrack) Text(
                text = trackInfo.label ?: "Unknown",
                color = colorScheme.onBackground.copy(alpha = 0.5f),
                overflow = TextOverflow.Ellipsis,
                style = typography.bodyMedium,
                maxLines = 1
            )
        }
    }
}

@DarkPreview
@Composable
private fun PreviewTracksContent() {
    DarkSurface {
        SubtitleTracks(
            listState = ListState.SubtitleTracks(
                listOf(
                    TrackInfo(
                        trackType = TrackType.Subtitle,
                        label = "Wonder.Women.1994.2004 HDRip",
                        language = "de"
                    ),
                    TrackInfo(
                        trackType = TrackType.Subtitle,
                        label = "Wonder.Women.1994.2004 HDRip",
                        language = "en",
                        id = "online"
                    ),
                    TrackInfo(trackType = TrackType.Subtitle).apply {
                        disableTrack = true
                    },
                )
            )
        )
    }
}