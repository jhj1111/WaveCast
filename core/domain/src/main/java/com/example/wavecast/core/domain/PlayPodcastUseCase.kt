package com.example.wavecast.core.domain

import android.util.Log
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
                val durationMillis = parseDuration(latestEpisode.duration)
                Log.d("TAG-PlayPodcastUseCase", "durationMillis: $durationMillis")
                
                waveCastPlayer.playPodcast(
                    url = latestEpisode.audioUrl,
                    title = latestEpisode.title,
                    author = podcast.author,
                    imageUrl = latestEpisode.imageUrl ?: podcast.imageUrl,
                    duration = durationMillis
                )
            }
        } catch (e: Exception) {
            throw e
        }
    }

    private fun parseDuration(duration: String?): Long {
        if (duration == null) return 0L
        return try {
            val parts = duration.split(":")
            when (parts.size) {
                3 -> { // HH:MM:SS
                    val hours = parts[0].toLong()
                    val minutes = parts[1].toLong()
                    val seconds = parts[2].toLong()
                    (hours * 3600 + minutes * 60 + seconds) * 1000
                }
                2 -> { // MM:SS
                    val minutes = parts[0].toLong()
                    val seconds = parts[1].toLong()
                    (minutes * 60 + seconds) * 1000
                }
                1 -> { // SS
                    parts[0].toLong() * 1000
                }
                else -> 0L
            }
        } catch (e: Exception) {
            0L
        }
    }
}
