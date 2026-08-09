package com.joshgm3z.netplayer.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joshgm3z.netplayer.repository.VideoLinkRepository
import javax.inject.Inject

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PlaybackUiState(
    val resumePosition: Long? = null,
    val title: String,
    val url: String,
)

@HiltViewModel
class PlaybackViewModel
@Inject
constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: VideoLinkRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<PlaybackUiState?>(null)
    val uiState = _uiState.asStateFlow()

    private val url = savedStateHandle.get<String>("url")
        ?: throw IllegalArgumentException("Missing url argument")

    init {
        viewModelScope.launch {
            val videoLink = repository.getVideoLink(url)
            _uiState.value = PlaybackUiState(
                resumePosition = videoLink.playedDuration,
                title = videoLink.title,
                url = videoLink.url
            )
        }
    }

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
