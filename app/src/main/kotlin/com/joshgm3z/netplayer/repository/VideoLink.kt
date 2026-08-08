package com.joshgm3z.netplayer.repository

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class VideoLink(
    @PrimaryKey(autoGenerate = false)
    val url: String,
    val title: String,
    val added: Long,
    val totalDuration: Long,
    val playedDuration: Long,
)
