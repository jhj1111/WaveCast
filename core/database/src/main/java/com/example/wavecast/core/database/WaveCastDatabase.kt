package com.example.wavecast.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.wavecast.core.database.dao.PodcastDao
import com.example.wavecast.core.database.model.PodcastEntity

@Database(
    entities = [PodcastEntity::class],
    version = 1,
    exportSchema = true
)
abstract class WaveCastDatabase : RoomDatabase() {
    abstract fun podcastDao(): PodcastDao
}
