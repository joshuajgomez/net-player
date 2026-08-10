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
import androidx.tv.material3.Button
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme.colorScheme
import androidx.tv.material3.MaterialTheme.typography
import androidx.tv.material3.Text
import com.joshgm3z.subtitletrack.repository.SubtitleData
import com.joshgm3z.subtitletrack.view.theme.Gray10
import com.joshgm3z.subtitletrack.view.theme.Purple40
import com.joshgm3z.subtitletrack.view.theme.SubtitleTrackTheme

@Composable
fun TrackSelectorDialog(
    viewModel: TrackSelectorViewModel = hiltViewModel(),
    goBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    if (uiState == null) goBack()
    else Dialog(
        onDismissRequest = goBack,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        TrackSelectorDialogContent(
            uiState = uiState!!,
            onDownloadSubtitleClicked = { viewModel.onDownloadedSubtitleClick(it) },
            onFindMoreClicked = { viewModel.onFindMoreClicked() },
            onTrackClicked = { viewModel.onTrackClicked(it) },
            onLanguageClick = { viewModel.onLanguageClick(it) }
        )
    }
}

@Composable
private fun TrackSelectorDialogContent(
    uiState: TrackSelectorUiState,
    onDownloadSubtitleClicked: (SubtitleData) -> Unit = {},
    onFindMoreClicked: () -> Unit = {},
    onTrackClicked: (TrackInfo) -> Unit = {},
    onLanguageClick: (String) -> Unit = {},
) {
    val sidePadding = 15.dp
    Box(
        modifier = Modifier
            .width(600.dp)
            .height(300.dp)
            .background(color = colorScheme.background)
            .padding(start = sidePadding, end = sidePadding, top = 5.dp, bottom = sidePadding),
        contentAlignment = Alignment.BottomCenter,
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
    onFindMoreClicked: () -> Unit,
    showFindMoreButton: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(55.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
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
private fun SearchButton(onClick: () -> Unit) {
    Button(onClick = onClick) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.size(1.dp))
        Text(
            text = "OpenSubtitles.com",
        )
    }
}

@Preview
@Composable
private fun PreviewTrackSelectorDialogContent() {
    DarkSurface {
        TrackSelectorDialogContent(
            uiState = TrackSelectorUiState(
                isLoading = false,
                listState = ListState.SubtitleTracks(
                    listOf(
                    )
                )
            )
        )
    }
}

@Preview
@Composable
private fun PreviewSubtitleDownloaderDialog() {
    DarkSurface {
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