package com.joshgm3z.netplayer.repository

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class VideoLink(
    @PrimaryKey(autoGenerate = false)
    val url: String,
    val title: String,
    val added: Long,
    val totalDuration: Long? = null,
    val playedDuration: Long? = null,
    val subtitleUrl: String? = null,
    val subtitleLanguage: String? = null,
) {
    val progress: Float
        get() = when {
            totalDuration == null || playedDuration == null -> 0f
            totalDuration == 0L || playedDuration == 0L -> 0f
            else -> playedDuration.toFloat() / totalDuration.toFloat()
        }
}
