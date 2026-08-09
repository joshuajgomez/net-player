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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.tv.material3.Button
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme.colorScheme
import androidx.tv.material3.MaterialTheme.typography
import androidx.tv.material3.Surface
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
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.onViewResumed()
    }
    LifecycleEventEffect(Lifecycle.Event.ON_PAUSE) {
        viewModel.onViewPaused()
    }
    val uiState by viewModel.uiState.collectAsState()
    HomeScreenContent(
        uiState = uiState,
        onVideoLinkClick = {
            navigate(NavDest.Player(it.url))
        },
        onAppUpdateClick = {
            navigate(NavDest.AppUpdate)
        },
        onDeleteAllClick = {
            viewModel.deleteAll()
        })
}

@Composable
private fun HomeScreenContent(
    uiState: HomeUiState,
    onVideoLinkClick: (VideoLink) -> Unit = {},
    onAppUpdateClick: () -> Unit = {},
    onDeleteAllClick: () -> Unit = {}
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
            Settings(
                qrCode = uiState.qrCode,
                onAppUpdateClick = onAppUpdateClick,
                onDeleteAllClick = onDeleteAllClick
            )
        }
    }
}

@Composable
fun Settings(
    qrCode: Bitmap?,
    onAppUpdateClick: () -> Unit,
    onDeleteAllClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        QrCode(qrCode)
        Spacer(Modifier.size(20.dp))
        CustomButton(
            text = "Delete all",
            imageVector = Icons.Default.Delete,
            onClick = onDeleteAllClick
        )
        Spacer(Modifier.size(10.dp))
        CustomButton(
            text = "Update App",
            imageVector = Icons.Default.Replay,
            onClick = onAppUpdateClick
        )
    }
}

@Composable
fun CustomButton(
    text: String,
    imageVector: ImageVector,
    onClick: () -> Unit
) {
    Button(onClick = onClick, modifier = Modifier.width(200.dp)) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = imageVector,
                contentDescription = null
            )
            Spacer(Modifier.size(5.dp))
            Text(text)
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
            text = "Scan the QR code to add new urls",
            color = subTextColor(),
            style = typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(20.dp)
                .width(180.dp)
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
                videoLinks == null -> "Loading..."
                videoLinks.isEmpty() -> "Welcome to NetPlayer!"
                else -> return@Column
            },
            style = typography.titleLarge,
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
