package com.example.wavecast.feature.library

import app.cash.turbine.test
import com.example.wavecast.core.data.model.Podcast
import com.example.wavecast.core.data.repository.PodcastRepository
import com.example.wavecast.core.data.util.NetworkMonitor
import com.example.wavecast.core.domain.PlayPodcastUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LibraryViewModelTest {

    private lateinit var podcastRepository: PodcastRepository
    private lateinit var playPodcastUseCase: PlayPodcastUseCase
    private lateinit var networkMonitor: NetworkMonitor
    private lateinit var viewModel: LibraryViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        podcastRepository = mockk()
        playPodcastUseCase = mockk()
    }

    @Test
    fun `구독한 팟캐스트가 있는 경우 Success 상태를 반환한다`() = runTest {
        // Given
        val podcasts = listOf(Podcast("1", "Title", "Author", "Desc", "img", "feed"))
        every { podcastRepository.getSubscribedPodcasts() } returns flowOf(podcasts)

        // When
        viewModel = LibraryViewModel(podcastRepository, playPodcastUseCase, networkMonitor)

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assert(state is LibraryUiState.Success)
            assertEquals(podcasts, (state as LibraryUiState.Success).podcasts)
        }
    }

    @Test
    fun `구독한 팟캐스트가 없는 경우 Empty 상태를 반환한다`() = runTest {
        // Given
        every { podcastRepository.getSubscribedPodcasts() } returns flowOf(emptyList())

        // When
        viewModel = LibraryViewModel(podcastRepository, playPodcastUseCase, networkMonitor)

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assert(state is LibraryUiState.Empty)
        }
    }
}
