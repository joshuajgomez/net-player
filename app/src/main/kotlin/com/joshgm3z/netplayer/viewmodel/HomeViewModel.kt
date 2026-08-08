package com.joshgm3z.netplayer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joshgm3z.netplayer.repository.VideoLink
import com.joshgm3z.netplayer.repository.VideoLinkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
@Inject constructor(
    private val videoLinkRepository: VideoLinkRepository
) : ViewModel() {

    private val _videoLinkState = MutableStateFlow<List<VideoLink>>(emptyList())
    val videoLinkState = _videoLinkState.asStateFlow()

    init {
        videoLinkRepository.listenToNewVideoLinks("default")
        viewModelScope.launch {
            videoLinkRepository.videoLinksFlow().collectLatest {
                _videoLinkState.value = it
            }
        }
    }
}