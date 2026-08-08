package com.joshgm3z.netplayer.ui.screen

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.tv.material3.Text
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Replay
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.Button
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme.colorScheme
import androidx.tv.material3.MaterialTheme.typography
import com.joshgm3z.netplayer.repository.VideoLink
import com.joshgm3z.netplayer.ui.NavDest
import com.joshgm3z.netplayer.ui.theme.subTextColor
import com.joshgm3z.netplayer.ui.util.DarkPreview
import com.joshgm3z.netplayer.ui.util.DarkSurface
import com.joshgm3z.netplayer.viewmodel.HomeUiState
import com.joshgm3z.netplayer.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    navigate: (NavDest) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    HomeScreenContent(
        uiState = uiState,
        onVideoLinkClick = {
            navigate(NavDest.Player(it.url))
        },
        onAppUpdateClick = {
            navigate(NavDest.AppUpdate)
        })
}

@Composable
private fun HomeScreenContent(
    uiState: HomeUiState,
    onVideoLinkClick: (VideoLink) -> Unit = {},
    onAppUpdateClick: () -> Unit = {},
) {
    Box(
        contentAlignment = Alignment.BottomEnd,
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            VideoLinks(uiState.videoLinks, onVideoLinkClick)
            QrCode(uiState.qrCode)
        }
        Button(onClick = onAppUpdateClick) {
            Icon(
                imageVector = Icons.Default.Replay,
                contentDescription = null
            )
            Spacer(Modifier.size(5.dp))
            Text("Update App")
        }
    }
}

@Composable
fun QrCode(qrCode: Bitmap?) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(200.dp)
                .background(color = colorScheme.primary)
        ) {
            qrCode?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        Text(
            text = "Scan the QR code to add new urls here",
            color = subTextColor(),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(20.dp)
                .width(200.dp)
        )
    }
}

@Composable
fun VideoLinks(
    videoLinks: List<VideoLink>?,
    onVideoLinkClick: (VideoLink) -> Unit = {}
) {
    val firstItemRequester = remember { FocusRequester() }
    LaunchedEffect(videoLinks) {
        if (!videoLinks.isNullOrEmpty()) firstItemRequester.requestFocus()
    }

    Column(modifier = Modifier.width(450.dp)) {
        if (videoLinks.isNullOrEmpty()) Text(
            text = when {
                videoLinks == null -> "Loading links"
                videoLinks.isEmpty() -> "No links yet"
                else -> return@Column
            },
            style = typography.headlineMedium,
            color = subTextColor(),
        )
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            itemsIndexed(videoLinks ?: emptyList()) { index, item ->
                VideoLinkItem(
                    videoLink = item,
                    onClick = { onVideoLinkClick(item) },
                    modifier = Modifier.then(
                        if (index != 0) Modifier
                        else Modifier.focusRequester(firstItemRequester)
                    )
                )
            }
        }
    }
}

@DarkPreview
@Composable
private fun PreviewHomeScreenContent() {
    DarkSurface {
        HomeScreenContent(
            HomeUiState(
                videoLinks = listOf(
                    VideoLink("https://example.com/video1", "Video 1", 1202012L, 12121L, 32323L),
                    VideoLink("https://example.com/video2", "Video 2", 1202012L, 12121L, 32323L),
                    VideoLink("https://example.com/video3", "Video 3", 1202012L, 12121L, 32323L),
                ),
                qrCode = null
            )
        )
    }
}
