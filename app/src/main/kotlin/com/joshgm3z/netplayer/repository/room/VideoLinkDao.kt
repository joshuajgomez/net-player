package com.joshgm3z.netplayer.repository.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.joshgm3z.netplayer.repository.VideoLink
import kotlinx.coroutines.flow.Flow

@Dao
interface VideoLinkDao {

    @Query("SELECT * FROM VideoLink WHERE url = :url")
    suspend fun get(url: String): VideoLink

    @Query("SELECT * FROM VideoLink")
    fun getAllFlow(): Flow<List<VideoLink>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(videoLink: VideoLink)

    @Delete
    suspend fun delete(videoLink: VideoLink)

    @Query("DELETE FROM VideoLink")
    suspend fun deleteAll()

    @Update
    suspend fun update(videoLink: VideoLink)
}
