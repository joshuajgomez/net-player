package com.joshgm3z.netplayer.repository

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class VideoLink(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val url: String,
    val title: String,
    val added: Long,
    val totaDuration: Long,
    val playedDuration: Long,
)
