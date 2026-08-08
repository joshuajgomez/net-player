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
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.joshgm3z.netplayer.R
import com.joshgm3z.netplayer.ui.util.DarkPreview
import com.joshgm3z.netplayer.ui.util.DarkSurface

val sampleUrl =
    "https://rd2.seedr.cc/ff_get/3931638/5959460378/The.Drama.2026.1080p.WEBRip.AAC5.1.10bits.x265-Rapta.mkv?st=z3otH1pk1wu9ZgrqqX-GxQ&e=1786223834"

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

    val userAgent =
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"

    // 2. Create a DataSource Factory with the User-Agent and common headers to avoid 403
    val dataSourceFactory = remember {
        androidx.media3.datasource.DefaultHttpDataSource.Factory()
            .setUserAgent(userAgent)
            .setAllowCrossProtocolRedirects(true)
            .setDefaultRequestProperties(
                mapOf(
                    "Accept" to "*/*",
                    "Connection" to "keep-alive",
                    "Referer" to "https://www.seedr.cc/",
                )
            )
    }

    // 3. Create a MediaSource Factory using that DataSource
    val mediaSourceFactory = remember {
        androidx.media3.exoplayer.source.DefaultMediaSourceFactory(context)
            .setDataSourceFactory(dataSourceFactory)
    }

    val renderersFactory = remember {
        DefaultRenderersFactory(context).apply {
            setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON)
            setEnableDecoderFallback(true)
        }
    }

    val exoPlayer = remember {
        ExoPlayer.Builder(context, renderersFactory)
            .setMediaSourceFactory(mediaSourceFactory)
            .build()
    }

    LaunchedEffect(videoUrl) {
        val mediaItem = MediaItem.Builder()
            .setUri(videoUrl)
            .setMimeType(
                when {
                    videoUrl.contains(".mkv") -> MimeTypes.VIDEO_MATROSKA
                    videoUrl.contains(".mp4") -> MimeTypes.VIDEO_MP4
                    else -> null
                }
            )
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