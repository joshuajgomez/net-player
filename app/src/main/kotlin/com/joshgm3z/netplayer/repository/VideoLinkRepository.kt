package com.joshgm3z.netplayer.repository

import com.joshgm3z.netplayer.repository.room.VideoLinkDao
import com.joshgm3z.netplayer.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

const val COLLECTION_VIDEO_LINKS = "video_links"

class VideoLinkRepository
@Inject constructor(
    private val videoLinkDao: VideoLinkDao,
    private val firestore: FirestoreWrapper,
    private val dataStoreWrapper: DatastoreWrapper,
    private val scope: CoroutineScope,
    private val urlValidityChecker: UrlValidityChecker
) {

    suspend fun getSessionId(): String =
        when (val currentSessionId = dataStoreWrapper.getSessionId()) {
            null -> {
                val newId = firestore.newDocumentId(
                    "app_session",
                    mapOf("added" to System.currentTimeMillis())
                )
                dataStoreWrapper.setSessionId(newId)
                newId
            }

            else -> currentSessionId
        }

    suspend fun listenToNewVideoLinks(sessionId: String) {
        firestore.activateSession(COLLECTION_VIDEO_LINKS, sessionId)
        firestore.listenToDataMap(
            COLLECTION_VIDEO_LINKS,
            sessionId
        ) { map ->
            val url = map["url"] as? String ?: return@listenToDataMap
            if (url.trim().isEmpty()) {
                Logger.warn("Received invalid URL\"$url\" from Firestore, ignoring.")
                return@listenToDataMap
            }
            val title = (map["title"] as? String)?.trim().takeIf { !it.isNullOrEmpty() }
                ?: url.tryToGetTitle()

            scope.launch {
                videoLinkDao.insert(
                    VideoLink(
                        url = url,
                        title = title,
                        added = System.currentTimeMillis(),
                    )
                )
            }
        }
    }

    fun videoLinksFlow(): Flow<List<VideoLink>> {
        return videoLinkDao.getAllFlow().map {
            it.map { videoLink ->
                videoLink.apply {
                    linkInvalid = urlValidityChecker.isUrlInvalid(videoLink.url)
                }
            }
        }
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

    suspend fun deleteSession(sessionId: String) {
        Logger.debug("sessionId = [${sessionId}]")
        firestore.deleteDocumentWithId(COLLECTION_VIDEO_LINKS, sessionId)
    }

    suspend fun getVideoLink(url: String): VideoLink {
        return videoLinkDao.get(url)
    }

}

fun String.tryToGetTitle(): String = try {
    val urlWithoutQuery = this.split("?").first()
    val title = urlWithoutQuery.substringAfterLast('/')
    title
} catch (e: Exception) {
    ""
}

