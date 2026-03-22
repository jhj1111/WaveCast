package com.example.wavecast.core.data.di

import com.example.wavecast.core.data.repository.DefaultPodcastRepository
import com.example.wavecast.core.data.repository.PodcastRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {
    @Binds
    @Singleton
    fun bindPodcastRepository(
        repository: DefaultPodcastRepository
    ): PodcastRepository
}
