package com.example.wavecast.core.domain

import com.example.wavecast.core.data.model.Episode
import com.example.wavecast.core.data.model.Podcast
import com.example.wavecast.core.media.WaveCastPlayer
import javax.inject.Inject

class PlayEpisodeUseCase @Inject constructor(
    private val waveCastPlayer: WaveCastPlayer
) {
    operator fun invoke(podcast: Podcast, episode: Episode) {
        waveCastPlayer.playPodcast(
            url = episode.audioUrl,
            title = episode.title,
            author = podcast.author,
            imageUrl = episode.imageUrl ?: podcast.imageUrl
            // duration은 재생 시작 후 엔진에서 분석하도록 0L 전달 (또는 필요시 파싱)
        )
    }
}
