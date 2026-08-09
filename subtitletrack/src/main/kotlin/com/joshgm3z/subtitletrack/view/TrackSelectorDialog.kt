package com.joshgm3z.subtitletrack.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.MaterialTheme.typography
import androidx.tv.material3.Text
import com.joshgm3z.subtitletrack.repository.SubtitleData
import com.joshgm3z.subtitletrack.view.theme.SubtitleTrackTheme

@Composable
fun TrackSelectorDialog(
    viewModel: TrackSelectorViewModel = hiltViewModel(),
    goBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    SubtitleTrackTheme {
        if (uiState == null) goBack()
        else Dialog(
            onDismissRequest = goBack,
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            TrackSelectorDialogContent(
                uiState = uiState!!,
                onBackPress = goBack,
                onDownloadSubtitleClicked = { viewModel.onDownloadedSubtitleClick(it) },
                onFindMoreClicked = { viewModel.onFindMoreClicked() },
                onTrackClicked = { viewModel.onTrackClicked(it) },
                onLanguageClick = { viewModel.onLanguageClick(it) }
            )
        }
    }
}

@Composable
private fun TrackSelectorDialogContent(
    uiState: TrackSelectorUiState,
    onDownloadSubtitleClicked: (SubtitleData) -> Unit = {},
    onFindMoreClicked: () -> Unit = {},
    onTrackClicked: (TrackInfo) -> Unit = {},
    onBackPress: () -> Unit = {},
    onLanguageClick: (String) -> Unit = {},
) {
    Box(
        modifier = Modifier
            .width(600.dp)
            .height(300.dp)
            .background(color = colorScheme.onBackground),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopRow(
                title = when (uiState.listState) {
                    is ListState.SubtitleTracks -> "Subtitle tracks"
                    is ListState.OnlineSubtitleTracks -> "Subtitles from OpenSubtitles.com"
                    is ListState.AudioTracks -> "Audio tracks"
                    else -> "Unknown"
                },
                onBackPress = onBackPress,
                onFindMoreClicked = onFindMoreClicked,
                showFindMoreButton = uiState.listState is ListState.SubtitleTracks
            )
            when (uiState.listState) {
                is ListState.OnlineSubtitleTracks -> SubtitleDownloaderContent(
                    listState = uiState.listState,
                    onClick = onDownloadSubtitleClicked,
                    onLanguageClick = onLanguageClick
                )

                is ListState.SubtitleTracks -> SubtitleTracks(
                    listState = uiState.listState,
                    onClick = onTrackClicked,
                )

                is ListState.AudioTracks -> AudioTracks(
                    listState = uiState.listState,
                    onClick = onTrackClicked,
                )

                else -> {}
            }
        }
        if (uiState.isLoading) {
            Surface(
                color = Color.Black.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxSize()
            ) {}
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun TopRow(
    title: String,
    onBackPress: () -> Unit,
    onFindMoreClicked: () -> Unit,
    showFindMoreButton: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CloseButton { onBackPress() }
        Text(
            text = title,
            style = typography.titleMedium,
            color = colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )
        Spacer(
            Modifier
                .fillMaxWidth()
                .weight(1f)
        )
        if (showFindMoreButton) SearchButton {
            onFindMoreClicked()
        }
    }
}

@Composable
private fun CloseButton(onClick: () -> Unit) {
    Icon(
        imageVector = Icons.Default.ArrowBack,
        contentDescription = "Close",
        tint = colorScheme.onBackground
    )
}

@Composable
private fun SearchButton(onClick: () -> Unit) {
    val color = colorScheme.primary
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(end = 10.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable(true) { onClick() }
            .padding(horizontal = 10.dp, vertical = 5.dp),
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            modifier = Modifier.size(17.dp),
            tint = color
        )
        Spacer(Modifier.size(1.dp))
        Text(
            text = "OpenSubtitles.com",
            style = MaterialTheme.typography.labelLarge,
            color = color
        )
    }
}

@Preview
@Composable
private fun PreviewTrackSelectorDialogContent() {
    SubtitleTrackTheme {
        TrackSelectorDialogContent(
            uiState = TrackSelectorUiState(
                isLoading = false,
                listState = ListState.SubtitleTracks(
                    listOf(
                        TrackInfo(
                            trackType = TrackType.Subtitle,
                            label = "Wonder.Women.1994.2004 HDRip",
                            language = "en",
                            id = "online"
                        ),
                        TrackInfo(
                            trackType = TrackType.Subtitle,
                            label = "Wonder.Women.1994.2004 HDRip",
                            language = "en",
                            id = "online"
                        ),
                        TrackInfo(
                            trackType = TrackType.Subtitle,
                            label = "Wonder.Women.1994.2004 HDRip",
                            language = "en",
                            id = "online"
                        ),
                    )
                )
            )
        )
    }
}

@Preview
@Composable
private fun PreviewSubtitleDownloaderDialog() {
    SubtitleTrackTheme {
        TrackSelectorDialogContent(
            uiState = TrackSelectorUiState(
                isLoading = false,
                listState = ListState.OnlineSubtitleTracks(
                    listOf(
                        SubtitleData(
                            title = "Wonder.Women.2024.HDRip.Xeno200",
                            language = "English",
                            fileId = 1234,
                            downloadCount = 300,
                        ),
                        SubtitleData(
                            title = "Wonder.Women.2024.HDRip.Xeno200",
                            language = "English",
                            fileId = 1234,
                            downloadCount = 300,
                        ),
                        SubtitleData(
                            title = "Wonder.Women.2024.HDRip.Xeno200",
                            language = "English",
                            fileId = 1234,
                            downloadCount = 300,
                        ),
                        SubtitleData(
                            title = "Wonder.Women.2024.HDRip.Xeno200",
                            language = "English",
                            fileId = 1234,
                            downloadCount = 300,
                        ),
                        SubtitleData(
                            title = "Wonder.Women.2024.HDRip.Xeno200",
                            language = "English",
                            fileId = 1234,
                            downloadCount = 300,
                        ),
                    ),
                    languages = listOf(
                        "en", "es", "fr"
                    ),
                    selectedLanguage = "en",
                ),
            )
        )
    }
}