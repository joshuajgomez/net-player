package com.joshgm3z.netplayer.repository.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.joshgm3z.netplayer.repository.VideoLink

@Database(
    entities = [VideoLink::class],
    version = 1,
    exportSchema = false
)
abstract class AppDb : RoomDatabase() {
    abstract fun videoLinkDao(): VideoLinkDao
}