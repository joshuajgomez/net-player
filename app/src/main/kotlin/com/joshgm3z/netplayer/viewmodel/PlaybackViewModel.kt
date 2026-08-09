package com.joshgm3z.netplayer.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joshgm3z.netplayer.repository.VideoLinkRepository
import javax.inject.Inject

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch

@HiltViewModel
class PlaybackViewModel
@Inject
constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: VideoLinkRepository
) : ViewModel() {
    private val url = savedStateHandle.get<String>("url")
        ?: throw IllegalArgumentException("Missing url argument")

    fun updatePlayedPosition(positionMs: Long) {
        viewModelScope.launch {
            val videoLink = repository.getVideoLink(url)
            repository.update(videoLink.copy(playedDuration = positionMs))
        }
    }

    fun updateTotalDuration(durationMs: Long) {
        viewModelScope.launch {
            val videoLink = repository.getVideoLink(url)
            repository.update(videoLink.copy(totalDuration = durationMs))
        }
    }
}
