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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.joshgm3z.netplayer.R
import com.joshgm3z.netplayer.ui.util.DarkPreview
import com.joshgm3z.netplayer.ui.util.DarkSurface

@Composable
fun PlayerScreen(
    url: String,
    title: String?,
) {
    PlaybackScreenContent(
        videoUrl = url,
        resumePosition = null/*uiState?.resumePosition*/,
        updateLastPlayedPosition = {
//                viewModel.updateLastPlayedPosition(it)
        },
        onError = {
            /*navController.navigate(
                NavMainDestination.Error(
                    message = "Error playing video",
                    summary = it
                )
            )*/
        },
        onBackPress = {},
        onCaptionsClicked = {
            /*trackViewModel.loadTracksOfType(TrackType.Subtitle)
            navController.navigate(NavMainDestination.TrackSelector)*/
        },
//            subtitleTrackListener = trackViewModel.subtitleTrackListener,
//            trackToLoadFlow = trackViewModel.trackToLoad,
        updateSelectedSubtitle = { language, title, url ->
//                viewModel.updateSelectedSubtitle(language, title, url)
        }
    )
//    }
}

@OptIn(UnstableApi::class)
@Composable
private fun PlaybackScreenContent(
    videoUrl: String,
    resumePosition: Long? = null,
    updateLastPlayedPosition: (Long) -> Unit = {},
    onError: (String) -> Unit = {},
    onCaptionsClicked: () -> Unit = {},
    onBackPress: () -> Unit = {},
    updateSelectedSubtitle: (language: String, title: String, url: String?) -> Unit = { _, _, _ -> },
//    subtitleTrackListener: Player.Listener? = null,
//    trackToLoadFlow: StateFlow<LoadTrack?> = MutableStateFlow(null),
) {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build()
    }

    LaunchedEffect(videoUrl) {
        val mediaItem = MediaItem.Builder()
            .setUri(videoUrl)
            .build()
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        resumePosition?.let { exoPlayer.seekTo(it) }
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
            val playerView = LayoutInflater.from(ctx)
                .inflate(R.layout.custom_player_view, null) as PlayerView
            playerView.apply {
                player = exoPlayer
                keepScreenOn = true
//                subtitleTrackListener?.let { player?.addListener(it) }
//                setShowSubtitleButton(true)
                findViewById<ImageButton>(R.id.custom_exo_subtitle)?.let {
                    it.setOnClickListener { onCaptionsClicked() }
                }
                /*findViewById<ImageButton>(R.id.iv_back_button)?.let {
                    it.setOnClickListener { onBackPress() }
                }*/
            }
        },
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black)
    )

    LaunchedEffect(Unit) {
        /*remindPeriodically {
            if (exoPlayer.isPlaying)
                updateLastPlayedPosition(exoPlayer.currentPosition)
        }*/
    }

    /*val trackToLoad by trackToLoadFlow.collectAsState()
    trackToLoad?.let {
        when (it) {
            is LoadTrack.OnlineSubtitle -> with(it.subtitleData) {
                exoPlayer.loadSubtitle(this)
                updateSelectedSubtitle(language ?: "", title, url)
            }

            is LoadTrack.OfflineTrack -> with(it.trackInfo) {
                exoPlayer.switchTrack(this)
                if (trackType == TrackType.Subtitle) updateSelectedSubtitle(
                    language ?: "",
                    label ?: "",
                    null
                )
            }
        }
    }*/
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
        PlaybackScreenContent("")
    }
}