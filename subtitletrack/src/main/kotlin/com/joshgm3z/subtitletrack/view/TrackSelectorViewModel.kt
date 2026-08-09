package com.joshgm3z.subtitletrack.view

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.C
import androidx.media3.common.C.FORMAT_HANDLED
import androidx.media3.common.Format
import androidx.media3.common.TrackGroup
import androidx.media3.common.Tracks
import androidx.media3.common.util.UnstableApi
import com.joshgm3z.subtitletrack.PlayerListener
import com.joshgm3z.subtitletrack.repository.SubtitleData
import com.joshgm3z.subtitletrack.repository.SubtitleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.openjdk.tools.sjavac.Log
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

data class TrackInfo(
    var id: String = "",
    val groupIndex: Int = 0,
    val trackIndexInGroup: Int = 0,
    val mimeType: String? = null,
    val language: String? = null,
    val label: String? = null,
    val roleFlags: Int = 0,
    val isSupported: Boolean = false,
    var isSelected: Boolean = false,
    val trackGroup: TrackGroup? = null,
    val trackType: TrackType,
) {
    var disableTrack: Boolean
        get() = id == "disabled"
        set(value) {
            if (value) id = "disabled"
        }

    override fun toString() = "\n{$language,$isSelected,$label}"
}

enum class TrackType {
    Subtitle, Audio
}

data class TrackButtonState(
    val enableCaptionsButton: Boolean = false,
    val enableAudioButton: Boolean = false,
)

sealed class ListState {
    class SubtitleTracks(val list: List<TrackInfo>) : ListState()
    class AudioTracks(val list: List<TrackInfo>) : ListState()
    data class OnlineSubtitleTracks(
        val list: List<SubtitleData>,
        val languages: List<String> = emptyList(),
        val selectedLanguage: String? = null,
    ) : ListState()
}

data class TrackSelectorUiState(
    val isLoading: Boolean = false,
    val statusText: String = "",
    val listState: ListState? = null,
)

sealed class LoadTrack {
    class OfflineTrack(val trackInfo: TrackInfo) : LoadTrack()
    class OnlineSubtitle(val subtitleData: SubtitleData) : LoadTrack()
}

@HiltViewModel
class TrackSelectorViewModel
@Inject
constructor(
    savedStateHandle: SavedStateHandle,
    private val subtitleRepository: SubtitleRepository,
    private val playerListener: PlayerListener,
) : ViewModel() {

    private val title: String = savedStateHandle.get<String>("title")
        ?: throw IllegalArgumentException("title is required")

    private val _uiState = MutableStateFlow<TrackSelectorUiState?>(TrackSelectorUiState())
    val uiState = _uiState.asStateFlow()

    private var subtitleTracks: List<TrackInfo> = emptyList()
        set(value) {
            field = value
            Log.info("subtitleTracks = $value")
        }

    private val _trackButtonState = MutableStateFlow(TrackButtonState())
    val trackButtonState = _trackButtonState.asStateFlow()

    private var onlineSubtitleTracks = emptyList<SubtitleData>()

    init {
        loadTracksOfType(TrackType.Subtitle)
        viewModelScope.launch {
            playerListener.trackChangesFlow.collectLatest {
                it?.let {
                    onTracksChanged(it)
                }
            }
        }
    }

    private fun onTracksChanged(tracks: Tracks) {
        val subtitleTracks_ = mutableListOf<TrackInfo>()
        val audioTracks_ = mutableListOf<TrackInfo>()

        tracks.groups.forEachIndexed { groupIndex, group ->
            if (group.type.isSubtitleTrack())
                subtitleTracks_.addAll(group.parseTracks(groupIndex, TrackType.Subtitle))
            if (group.type.isAudioTrack())
                audioTracks_.addAll(group.parseTracks(groupIndex, TrackType.Audio))
        }

        _trackButtonState.update {
            it.copy(
                enableCaptionsButton = true,
                enableAudioButton = audioTracks_.isNotEmpty()
            )
        }
        subtitleTracks = subtitleTracks_.plusDisableSubtitleTrack()

        if (playerListener.trackToLoad.value is LoadTrack.OnlineSubtitle && subtitleTracks.size > 1)
            closeTrackSelectionPopup()
    }

    private fun loadTracksOfType(trackType: TrackType) {
        _uiState.update {
            TrackSelectorUiState(
                listState = ListState.SubtitleTracks(subtitleTracks)
            )
        }
        Log.debug("_uiState[$trackType] = [${_uiState.value}]")
    }

    fun onFindMoreClicked() {
        _uiState.update { it?.copy(isLoading = true) }

        viewModelScope.launch {
            val subtitles = subtitleRepository.findSubtitles(title!!)
            if (subtitles.isEmpty()) {
                delay(1.seconds)
                _uiState.update {
                    it?.copy(
                        isLoading = false,
                        statusText = "No subtitles found online"
                    )
                }
            } else _uiState.update {
                onlineSubtitleTracks = subtitles
                it?.copy(
                    isLoading = false,
                    listState = ListState.OnlineSubtitleTracks(
                        list = subtitles,
                        languages = subtitles.getLanguages(),
                        selectedLanguage = "All"
                    )
                )
            }
        }
    }

    private fun List<SubtitleData>.getLanguages(): List<String> =
        mutableListOf("All").apply {
            addAll(
                this@getLanguages
                    .mapNotNull { it.language }
                    .distinct()
                    .sortedBy { it != "en" }
            )
        }

    fun onLanguageClick(language: String) {
        _uiState.update { uiState ->
            val listState = uiState?.listState as ListState.OnlineSubtitleTracks
            uiState.copy(
                listState = listState.copy(
                    list = if (language == "All") onlineSubtitleTracks
                    else onlineSubtitleTracks.filter { it.language == language },
                    selectedLanguage = language
                )
            )
        }
    }

    fun onTrackClicked(trackInfo: TrackInfo) {
        Log.debug("trackInfo = [${trackInfo}]")
        if (!trackInfo.isSelected) playerListener.trackToLoad.value =
            LoadTrack.OfflineTrack(trackInfo)
        closeTrackSelectionPopup()
    }

    private fun closeTrackSelectionPopup() {
        viewModelScope.launch {
            delay(1.seconds)
            playerListener.trackToLoad.value = null
            _uiState.value = null
        }
    }

    fun onDownloadedSubtitleClick(subtitleData: SubtitleData) {
        Log.debug("subtitleData.title = [${subtitleData}]")
        _uiState.update { it?.copy(isLoading = true) }

        viewModelScope.launch {
            val url = subtitleRepository.getSubtitleUrl(subtitleData.fileId)
            Log.info("JJJJJJ0 load url = $url")
            playerListener.trackToLoad.value =
                LoadTrack.OnlineSubtitle(subtitleData.copy(url = url))
        }
    }
}

private fun MutableList<TrackInfo>.plusDisableSubtitleTrack(): MutableList<TrackInfo> =
    this.apply {
        if (any { it.disableTrack }) return@apply
        add(
            TrackInfo(trackType = TrackType.Subtitle).apply {
                disableTrack = true
                isSelected = !any { it.isSelected }
            }
        )
    }

@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(UnstableApi::class)
private fun Tracks.Group.parseTracks(
    groupIndex: Int,
    trackType: TrackType,
): List<TrackInfo> {
    val tracks = mutableListOf<TrackInfo>()
    for (i in 0 until length) {
        val format: Format = getTrackFormat(i)
        if (trackType == TrackType.Subtitle)
            Log.info("\n${format.language},${isTrackSelected(i)},${format.label}")

        tracks += TrackInfo(
            id = format.id ?: "",
            groupIndex = groupIndex,
            trackIndexInGroup = i,
            mimeType = format.sampleMimeType,
            language = format.language,
            label = format.label,
            roleFlags = format.roleFlags,
            isSupported = FORMAT_HANDLED == getTrackSupport(i),
            isSelected = isTrackSelected(i),
            trackType = trackType,
            trackGroup = mediaTrackGroup
        ).apply {
//            Logger.debug("trackType=$trackType,$this")
        }
    }
    return tracks
}

fun Int.isSubtitleTrack() = this == C.TRACK_TYPE_TEXT
fun Int.isAudioTrack() = this == C.TRACK_TYPE_AUDIO