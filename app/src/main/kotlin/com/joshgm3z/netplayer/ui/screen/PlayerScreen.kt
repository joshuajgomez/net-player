package com.joshgm3z.netplayer.ui.screen

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.LayoutInflater
import android.widget.ImageButton
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Tracks
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.joshgm3z.netplayer.R
import com.joshgm3z.netplayer.ui.util.DarkPreview
import com.joshgm3z.netplayer.ui.util.DarkSurface
import com.joshgm3z.netplayer.ui.util.errorListener
import com.joshgm3z.netplayer.util.Logger
import com.joshgm3z.netplayer.viewmodel.PlaybackUiState
import com.joshgm3z.netplayer.viewmodel.PlaybackViewModel
import com.joshgm3z.subtitletrack.util.loadSubtitle
import com.joshgm3z.subtitletrack.util.switchTrack
import com.joshgm3z.subtitletrack.view.LoadTrack
import com.joshgm3z.subtitletrack.view.TrackType

@Composable
fun PlayerScreen(
    viewModel: PlaybackViewModel = hiltViewModel(),
    onCaptionsClicked: (String) -> Unit,
    onError: (String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    uiState?.let { uiState ->
        PlaybackScreenContent(
            uiState = uiState,
            updateLastPlayedPosition = {
                viewModel.updatePlayedPosition(it)
            },
            onError = onError,
            updateTotalDuration = {
                viewModel.updateTotalDuration(it)
            },
            onCaptionsClicked = { onCaptionsClicked(uiState.title) },
            onTracksChanged = {
                viewModel.playerListener.trackChangesFlow.value = it
            },
        )
    }
}

@OptIn(UnstableApi::class)
@Composable
private fun PlaybackScreenContent(
    uiState: PlaybackUiState,
    updateLastPlayedPosition: (Long) -> Unit = {},
    updateTotalDuration: (Long) -> Unit = {},
    onError: (String) -> Unit = {},
    onCaptionsClicked: () -> Unit = {},
    onTracksChanged: (Tracks) -> Unit = {},
) {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            addListener(errorListener(onError = onError))
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == Player.STATE_READY) {
                        updateTotalDuration(duration.coerceAtLeast(0L))
                    }
                }

                override fun onTracksChanged(tracks: Tracks) {
                    super.onTracksChanged(tracks)
                    onTracksChanged(tracks)
                }
            })
        }
    }

    LaunchedEffect(uiState.url) {
        Logger.debug("Loading media ${uiState.url}")
        val mediaItem = MediaItem.Builder()
            .setUri(uiState.url)
            .build()
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        uiState.resumePosition?.let { exoPlayer.seekTo(it) }
        exoPlayer.playWhenReady = true
    }

    LifecycleEventEffect(Lifecycle.Event.ON_PAUSE) {
        updateLastPlayedPosition(exoPlayer.currentPosition)
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    AndroidView(
        factory = { ctx ->
            Logger.debug("factory custom_player_view")
            val playerView = LayoutInflater.from(ctx)
                .inflate(R.layout.custom_player_view, null) as PlayerView
            playerView.apply {
                player = exoPlayer
                keepScreenOn = true
                findViewById<ImageButton>(R.id.custom_exo_subtitle)?.let {
                    it.setOnClickListener { onCaptionsClicked() }
                }
            }
        },
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black)
    )

    LaunchedEffect(Unit) {
        if (exoPlayer.isPlaying)
            updateLastPlayedPosition(exoPlayer.currentPosition)
    }

    LaunchedEffect(uiState.trackToLoad) {
        uiState.trackToLoad?.let {
            when (it) {
                is LoadTrack.OnlineSubtitle -> with(it.subtitleData) {
                    exoPlayer.loadSubtitle(this)
                }

                is LoadTrack.OfflineTrack -> with(it.trackInfo) {
                    exoPlayer.switchTrack(this)
                }
            }
        }
    }
}

fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

@DarkPreview
@Composable
private fun PreviewPlaybackScreenContent() {
    DarkSurface {
        PlaybackScreenContent(
            uiState = PlaybackUiState(
                title = "Sample Video",
                url = "https://example.com/video.mp4"
            ),
        )
    }
}