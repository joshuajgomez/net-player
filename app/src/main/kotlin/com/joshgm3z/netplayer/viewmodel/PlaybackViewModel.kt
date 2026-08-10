package com.joshgm3z.netplayer.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joshgm3z.netplayer.repository.VideoLinkRepository
import com.joshgm3z.subtitletrack.PlayerListener
import com.joshgm3z.subtitletrack.repository.SubtitleData
import com.joshgm3z.subtitletrack.view.LoadTrack
import javax.inject.Inject

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PlaybackUiState(
    val resumePosition: Long? = null,
    val title: String,
    val url: String,
    val trackToLoad: LoadTrack? = null,
)

@HiltViewModel
class PlaybackViewModel
@Inject
constructor(
    savedStateHandle: SavedStateHandle,
    val playerListener: PlayerListener,
    private val repository: VideoLinkRepository,
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
                url = videoLink.url,
                trackToLoad = videoLink.subtitleUrl?.let { subtitleUrl ->
                    LoadTrack.OnlineSubtitle(
                        subtitleData = SubtitleData(
                            title = videoLink.title,
                            language = videoLink.subtitleLanguage,
                            url = subtitleUrl
                        )
                    )
                }
            )
        }
        viewModelScope.launch {
            playerListener.trackToLoad.collect {
                _uiState.value = _uiState.value?.copy(trackToLoad = it)
                if (it is LoadTrack.OnlineSubtitle)
                    updateSelectedSubtitle(
                        it.subtitleData.url!!,
                        it.subtitleData.language!!
                    )
            }
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

    private fun updateSelectedSubtitle(url: String, language: String) {
        viewModelScope.launch {
            val videoLink = repository.getVideoLink(url)
            repository.update(videoLink.copy(subtitleUrl = url, subtitleLanguage = language))
        }
    }
}
