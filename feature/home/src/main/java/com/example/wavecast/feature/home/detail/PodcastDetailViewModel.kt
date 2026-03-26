package com.example.wavecast.feature.home.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wavecast.core.data.model.Episode
import com.example.wavecast.core.data.model.Podcast
import com.example.wavecast.core.data.repository.PodcastRepository
import com.example.wavecast.core.domain.PlayEpisodeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface PodcastDetailUiState {
    object Loading : PodcastDetailUiState
    data class Success(
        val podcast: Podcast,
        val episodes: List<Episode>
    ) : PodcastDetailUiState
    data class Error(val message: String) : PodcastDetailUiState
}

@HiltViewModel
class PodcastDetailViewModel @Inject constructor(
    private val podcastRepository: PodcastRepository,
    private val playEpisodeUseCase: PlayEpisodeUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<PodcastDetailUiState>(PodcastDetailUiState.Loading)
    val uiState: StateFlow<PodcastDetailUiState> = _uiState.asStateFlow()

    fun fetchPodcastDetail(podcast: Podcast) {
        viewModelScope.launch {
            _uiState.value = PodcastDetailUiState.Loading
            try {
                val episodes = podcastRepository.getEpisodes(podcast.feedUrl)
                _uiState.value = PodcastDetailUiState.Success(podcast, episodes)
            } catch (e: Exception) {
                _uiState.value = PodcastDetailUiState.Error(e.message ?: "Failed to load episodes")
            }
        }
    }

    fun playEpisode(episode: Episode) {
        val currentState = _uiState.value
        if (currentState is PodcastDetailUiState.Success) {
            playEpisodeUseCase(currentState.podcast, episode)
        }
    }
}
