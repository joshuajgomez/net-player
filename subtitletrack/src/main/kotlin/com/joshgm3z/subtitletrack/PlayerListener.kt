package com.joshgm3z.subtitletrack

import androidx.media3.common.Tracks
import com.joshgm3z.subtitletrack.view.LoadTrack
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerListener
@Inject constructor() {
    val trackChangesFlow = MutableStateFlow<Tracks?>(null)
    val trackToLoad = MutableStateFlow<LoadTrack?>(null)
    val enableCcButton = MutableStateFlow(false)
}
