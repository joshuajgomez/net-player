package com.joshgm3z.netplayer.repository

import com.joshgm3z.netplayer.repository.room.VideoLinkDao
import com.joshgm3z.netplayer.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

const val COLLECTION_VIDEO_LINKS = "video_links"

class VideoLinkRepository
@Inject constructor(
    private val videoLinkDao: VideoLinkDao,
    private val firestore: FirestoreWrapper,
    private val scope: CoroutineScope
) {

    fun listenToNewVideoLinks(sessionId: String) {
        firestore.listenToDataMap(
            COLLECTION_VIDEO_LINKS,
            sessionId
        ) {
            val url = it["url"] as String
            val title = if (it.containsKey("title")) it["title"] as String else "No title"

            if (url.trim().isEmpty()) {
                Logger.warn("Received invalid URL\"$url\" from Firestore, ignoring.")
                return@listenToDataMap
            }
            scope.launch {
                videoLinkDao.insert(
                    VideoLink(
                        url = url,
                        title = title,
                        added = System.currentTimeMillis(),
                        totaDuration = 0,
                        playedDuration = 0
                    )
                )
            }
        }
    }

    fun videoLinksFlow(): Flow<List<VideoLink>> {
        return videoLinkDao.getAllFlow()
    }

    suspend fun update(videoLink: VideoLink) {
        videoLinkDao.update(videoLink)
    }

    suspend fun delete(videoLink: VideoLink) {
        videoLinkDao.delete(videoLink)
    }

    suspend fun deleteAll() {
        videoLinkDao.deleteAll()
    }
}