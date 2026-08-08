package com.joshgm3z.netplayer.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joshgm3z.netplayer.repository.QrCodeRepository
import com.joshgm3z.netplayer.repository.VideoLink
import com.joshgm3z.netplayer.repository.VideoLinkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val videoLinks: List<VideoLink> = emptyList(),
    val qrCode: Bitmap? = null,
)

@HiltViewModel
class HomeViewModel
@Inject constructor(
    private val videoLinkRepository: VideoLinkRepository,
    private val qrCodeRepository: QrCodeRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    private lateinit var sessionId: String

    init {
        viewModelScope.launch {
            sessionId = videoLinkRepository.getSessionId()
            videoLinkRepository.listenToNewVideoLinks(sessionId)
            _uiState.update {
                it.copy(qrCode = qrCodeRepository.getQrCodeBitmap(sessionId))
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

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            videoLinkRepository.deleteSession(sessionId)
        }
    }
}
