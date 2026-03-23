package com.example.wavecast.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wavecast.core.data.model.Podcast
import com.example.wavecast.core.data.repository.PodcastRepository
import com.example.wavecast.core.media.WaveCastPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface HomeUiState {
    object Loading : HomeUiState
    data class Success(
        val trendingPodcasts: List<Podcast> = emptyList(),
        val searchResults: List<Podcast> = emptyList(),
        val isSearching: Boolean = false,
        val searchQuery: String = ""
    ) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val podcastRepository: PodcastRepository,
    private val waveCastPlayer: WaveCastPlayer
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        fetchTrendingPodcasts()
    }

    fun fetchTrendingPodcasts() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            try {
                val trending = podcastRepository.getTrendingPodcasts()
                _uiState.value = HomeUiState.Success(trendingPodcasts = trending)
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        val currentState = _uiState.value
        if (currentState is HomeUiState.Success) {
            _uiState.value = currentState.copy(searchQuery = query)
            if (query.isBlank()) {
                _uiState.value = currentState.copy(searchQuery = query, isSearching = false, searchResults = emptyList())
            }
        }
    }

    fun performSearch() {
        val currentState = _uiState.value
        if (currentState is HomeUiState.Success && currentState.searchQuery.isNotBlank()) {
            viewModelScope.launch {
                try {
                    val results = podcastRepository.searchPodcasts(currentState.searchQuery)
                    _uiState.value = currentState.copy(
                        searchResults = results,
                        isSearching = true
                    )
                } catch (e: Exception) {
                    _uiState.value = HomeUiState.Error(e.message ?: "Search failed")
                }
            }
        }
    }

    fun playPodcast(podcast: Podcast) {
        viewModelScope.launch {
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
                // 에러 처리 로직 추가 가능
            }
        }
    }

    fun clearSearch() {
        val currentState = _uiState.value
        if (currentState is HomeUiState.Success) {
            _uiState.value = currentState.copy(
                searchQuery = "",
                isSearching = false,
                searchResults = emptyList()
            )
        }
    }
}
