package com.joshgm3z.netplayer.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joshgm3z.netplayer.repository.VideoLink
import com.joshgm3z.netplayer.repository.VideoLinkRepository
import com.joshgm3z.netplayer.repository.getBitmap
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val videoLinks: List<VideoLink>? = null,
    val qrCode: Bitmap? = null,
)

const val ONLINE_INPUT_URL = "https://net-player-487fb.web.app"

@HiltViewModel
class HomeViewModel
@Inject constructor(
    private val videoLinkRepository: VideoLinkRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    private lateinit var sessionId: String

    init {
        viewModelScope.launch {
            sessionId = videoLinkRepository.getSessionId()
            videoLinkRepository.listenToNewVideoLinks(sessionId)
            _uiState.update {
                val url = "$ONLINE_INPUT_URL?id=$sessionId"
                it.copy(qrCode = getBitmap(url))
            }
        }
        viewModelScope.launch {
            videoLinkRepository.videoLinksFlow().collectLatest { videoLinks ->
                _uiState.update {
                    it.copy(videoLinks = videoLinks)
                }
            }
        }
    }

    fun onViewPaused() {
        viewModelScope.launch {
            videoLinkRepository.deleteSession(sessionId)
        }
    }

    fun deleteAll() {
        viewModelScope.launch {
            videoLinkRepository.deleteAll()
        }
    }
}
