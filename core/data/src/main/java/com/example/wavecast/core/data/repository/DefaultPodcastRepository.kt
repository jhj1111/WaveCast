package com.example.wavecast.core.data.repository

import com.example.wavecast.core.data.model.Episode
import com.example.wavecast.core.data.model.Podcast
import com.example.wavecast.core.data.model.asEntity
import com.example.wavecast.core.data.model.asExternalModel
import com.example.wavecast.core.database.dao.PodcastDao
import com.example.wavecast.core.network.api.PodcastIndexApi
import com.example.wavecast.core.network.api.RssService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DefaultPodcastRepository @Inject constructor(
    private val api: PodcastIndexApi,
    private val rssService: RssService,
    private val dao: PodcastDao
) : PodcastRepository {

    override fun getSubscribedPodcasts(): Flow<List<Podcast>> {
        return dao.getAllPodcasts().map { entities ->
            entities.map { it.asExternalModel() }
        }
    }

    override suspend fun searchPodcasts(term: String): List<Podcast> {
        return api.searchPodcasts(term).feeds.map { it.asExternalModel() }
    }

    override suspend fun getTrendingPodcasts(): List<Podcast> {
        return api.getTrendingPodcasts().feeds.map { it.asExternalModel() }
    }

    override suspend fun subscribePodcast(podcast: Podcast) {
        dao.insertPodcasts(listOf(podcast.copy(isSubscribed = true).asEntity()))
    }

    override suspend fun unsubscribePodcast(id: String) {
        dao.deletePodcastById(id)
    }

    override suspend fun getEpisodes(feedUrl: String): List<Episode> {
        return rssService.fetchEpisodes(feedUrl).map { it.asExternalModel() }
    }
}
