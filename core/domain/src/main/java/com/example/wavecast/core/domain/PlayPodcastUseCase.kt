package com.example.wavecast.core.domain

import com.example.wavecast.core.data.model.Podcast
import com.example.wavecast.core.data.repository.PodcastRepository
import com.example.wavecast.core.media.WaveCastPlayer
import javax.inject.Inject

class PlayPodcastUseCase @Inject constructor(
    private val podcastRepository: PodcastRepository,
    private val waveCastPlayer: WaveCastPlayer
) {
    suspend operator fun invoke(podcast: Podcast) {
        try {
            val episodes = podcastRepository.getEpisodes(podcast.feedUrl)
            if (episodes.isNotEmpty()) {
                val latestEpisode = episodes.first()
                waveCastPlayer.playPodcast(
                    url = latestEpisode.audioUrl,
                    title = latestEpisode.title,
                    author = podcast.author,
                    imageUrl = latestEpisode.imageUrl ?: podcast.imageUrl
                )
            }
        } catch (e: Exception) {
            // 로그 기록이나 사용자 알림 처리를 여기에 추가할 수 있습니다.
            throw e
        }
    }
}
