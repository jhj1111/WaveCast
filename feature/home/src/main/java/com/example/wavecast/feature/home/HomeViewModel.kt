package com.example.wavecast.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wavecast.core.data.model.Podcast
import com.example.wavecast.core.data.repository.PodcastRepository
import com.example.wavecast.core.domain.PlayPodcastUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface HomeUiState {
    object Loading : HomeUiState
    data class Success(
        val trendingPodcasts: List<Podcast> = emptyList(),
        val searchResults: List<Podcast> = emptyList(),
        val isSearching: Boolean = false,
        val searchQuery: String = "",
        val subscribedPodcastIds: Set<String> = emptySet()
    ) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val podcastRepository: PodcastRepository,
    private val playPodcastUseCase: PlayPodcastUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        // 구독 정보 실시간 관찰
        podcastRepository.getSubscribedPodcasts()
            .onEach { podcasts ->
                val ids = podcasts.map { it.id }.toSet()
                val currentState = _uiState.value
                if (currentState is HomeUiState.Success) {
                    _uiState.value = currentState.copy(subscribedPodcastIds = ids)
                }
            }
            .launchIn(viewModelScope)

        fetchTrendingPodcasts()
    }

    fun fetchTrendingPodcasts() {
        viewModelScope.launch {
            val currentIds = (_uiState.value as? HomeUiState.Success)?.subscribedPodcastIds ?: emptySet()
            _uiState.value = HomeUiState.Loading
            try {
                val trending = podcastRepository.getTrendingPodcasts()
                _uiState.value = HomeUiState.Success(
                    trendingPodcasts = trending,
                    subscribedPodcastIds = currentIds
                )
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun toggleSubscription(podcast: Podcast) {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState is HomeUiState.Success) {
                if (currentState.subscribedPodcastIds.contains(podcast.id)) {
                    podcastRepository.unsubscribePodcast(podcast.id)
                } else {
                    podcastRepository.subscribePodcast(podcast)
                }
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
                playPodcastUseCase(podcast)
            } catch (e: Exception) {
                // 에러 처리 로직
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
