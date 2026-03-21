package com.example.wavecast.core.database.di

import android.content.Context
import androidx.room.Room
import com.example.wavecast.core.database.WaveCastDatabase
import com.example.wavecast.core.database.dao.PodcastDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideWaveCastDatabase(
        @ApplicationContext context: Context,
    ): WaveCastDatabase = Room.databaseBuilder(
        context,
        WaveCastDatabase::class.java,
        "wavecast-database",
    ).build()

    @Provides
    fun providePodcastDao(
        database: WaveCastDatabase,
    ): PodcastDao = database.podcastDao()
}
