package com.joshgm3z.netplayer.ui.screen

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme.colorScheme
import androidx.tv.material3.MaterialTheme.typography
import androidx.tv.material3.Text
import com.joshgm3z.netplayer.repository.VideoLink
import com.joshgm3z.netplayer.ui.NavDest
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
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(230.dp)
    ) {
        QrCode(qrCode)
        Spacer(Modifier.size(24.dp)) // Slightly increased for M3 spacing
        CustomButton(
            text = "Delete all",
            imageVector = Icons.Default.Delete,
            onClick = onDeleteAllClick
        )
        Spacer(Modifier.size(12.dp))
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
    imageVector: ImageVector? = null,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.colors(
            containerColor = colorScheme.surface,
        ),
        shape = ButtonDefaults.shape(shape = RoundedCornerShape(8.dp))
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            imageVector?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.size(8.dp))
            }
            Text(text = text, style = typography.labelLarge)
        }
    }
}

@Composable
fun QrCode(qrCode: Bitmap?) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(
                // WCAG: Using surfaceVariant ensures contrast against background
                color = colorScheme.surface,
                shape = RoundedCornerShape(10.dp)
            )
            .padding(20.dp)
    ) {
        Box(
            modifier = Modifier
                .aspectRatio(1f)
                .clip(shape = RoundedCornerShape(8.dp))
                .background(
                    // Container for the QR code to keep it legible
                    color = colorScheme.surfaceVariant,
                ),
            contentAlignment = Alignment.Center
        ) {
            if (qrCode == null) {
                CircularProgressIndicator(
                    modifier = Modifier.size(40.dp),
                    color = colorScheme.onSurfaceVariant,
                    strokeWidth = 3.dp
                )
            } else {
                Image(
                    bitmap = qrCode.asImageBitmap(),
                    contentDescription = "QR Code to add URLs",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        Text(
            text = "Scan the QR code to add new urls",
            // WCAG: onSurfaceVariant provides accessible contrast for secondary text
            color = colorScheme.onSurfaceVariant,
            style = typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(top = 20.dp)
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
        if (videoLinks.isNullOrEmpty()) {
            Text(
                text = when {
                    videoLinks == null -> "Loading..."
                    else -> "Welcome to NetPlayer!"
                },
                style = typography.headlineSmall,
                color = colorScheme.onSurfaceVariant,
            )
        }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
                    VideoLink(
                        "https://example.com/video1",
                        "Video 1",
                        1202012L,
                        6212100L,
                        3232300L
                    ),
                    VideoLink(
                        "https://example.com/video2",
                        "Video 2",
                        1202012L,
                        4212100L,
                        3232300L
                    ),
                    VideoLink("https://example.com/video3", "Video 3", 1202012L, 3212100L, 120000L),
                ),
                qrCode = null
            )
        )
    }
}
