package com.joshgm3z.netplayer.repository

import com.joshgm3z.netplayer.repository.room.VideoLinkDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class VideoLinkRepository
@Inject constructor(
    private val videoLinkDao: VideoLinkDao,
) {
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