package com.joshgm3z.subtitletrack.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme.colorScheme
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
        text = "No subtitles",
        style = typography.bodyLarge,
        color = colorScheme.onBackground,
        modifier = Modifier.padding(top = 30.dp)
    ) else LazyColumn(
        modifier = Modifier
            .padding(10.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(color = colorScheme.onBackground.copy(alpha = 0.1f))
    ) {
        itemsIndexed(list) { index, item ->
            TrackItem(
                trackInfo = item,
                selected = selectedTrackInfo?.id == item.id,
                onClick = {
                    selectedTrackInfo = item
                    onClick(item)
                }
            )
            CustomHorizontalDivider(index, list.size)
        }
    }
}

@Composable
private fun TrackItem(
    trackInfo: TrackInfo,
    selected: Boolean = false,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .clickable(true) {
                onClick()
            }
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
        )
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
        ) {
            Text(
                text = if (trackInfo.disableTrack) "Disabled"
                else trackInfo.language.languageName(),
                color = colorScheme.onBackground,
            )
            if (!trackInfo.disableTrack) Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                if (trackInfo.id.contains("online")) Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = null,
                    tint = colorScheme.onBackground,
                    modifier = Modifier
                        .size(15.dp)
                )
                Text(
                    text = trackInfo.label ?: "Unknown",
                    color = colorScheme.onBackground,
                    style = typography.bodyMedium,
                    maxLines = 1
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewTracksContent() {
    SubtitleTrackTheme {
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