package com.example.wavecast.core.domain

import com.example.wavecast.core.data.model.Episode
import com.example.wavecast.core.data.model.Podcast
import com.example.wavecast.core.data.repository.PodcastRepository
import com.example.wavecast.core.media.WaveCastPlayer
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PlayPodcastUseCaseTest {

    private lateinit var podcastRepository: PodcastRepository
    private lateinit var waveCastPlayer: WaveCastPlayer
    private lateinit var playPodcastUseCase: PlayPodcastUseCase

    @Before
    fun setUp() {
        podcastRepository = mockk()
        waveCastPlayer = mockk(relaxed = true)
        playPodcastUseCase = PlayPodcastUseCase(podcastRepository, waveCastPlayer)
    }

    @Test
    fun `팟캐스트 재생 시 최신 에피소드를 가져와 플레이어에 전달한다`() = runTest {
        // Given
        val podcast = Podcast("id", "Title", "Author", "Desc", "img", "feed")
        val episodes = listOf(
            Episode("Episode 1", "https://audio.mp3", "30:00", "ep_img")
        )
        coEvery { podcastRepository.getEpisodes(any()) } returns episodes

        // When
        playPodcastUseCase(podcast)

        // Then
        verify {
            waveCastPlayer.playPodcast(
                url = "https://audio.mp3",
                title = "Episode 1",
                author = "Author",
                imageUrl = "ep_img",
                duration = 1800000L // 30:00 -> 30*60*1000
            )
        }
    }

    @Test
    fun `에피소드가 없는 팟캐스트는 플레이어를 호출하지 않는다`() = runTest {
        // Given
        val podcast = Podcast("id", "Title", "Author", "Desc", "img", "feed")
        coEvery { podcastRepository.getEpisodes(any()) } returns emptyList()

        // When
        playPodcastUseCase(podcast)

        // Then
        verify(exactly = 0) { waveCastPlayer.playPodcast(any(), any(), any(), any(), any()) }
    }
}
