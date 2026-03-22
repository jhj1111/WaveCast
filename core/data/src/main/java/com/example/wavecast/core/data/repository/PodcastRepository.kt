package com.example.wavecast.core.data.repository

import com.example.wavecast.core.data.model.Podcast
import kotlinx.coroutines.flow.Flow

interface PodcastRepository {
    fun getSubscribedPodcasts(): Flow<List<Podcast>>
    suspend fun searchPodcasts(term: String): List<Podcast>
    suspend fun getTrendingPodcasts(): List<Podcast>
    suspend fun subscribePodcast(podcast: Podcast)
    suspend fun unsubscribePodcast(id: String)
}
