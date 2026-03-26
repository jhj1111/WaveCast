package com.example.wavecast.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wavecast.core.data.model.Podcast
import com.example.wavecast.core.data.repository.PodcastRepository
import com.example.wavecast.core.data.util.NetworkMonitor
import com.example.wavecast.core.data.util.Result
import com.example.wavecast.core.data.util.asResult
import com.example.wavecast.core.domain.PlayPodcastUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface HomeUiState {
    object Loading : HomeUiState
    data class Success(
        val trendingPodcasts: List<Podcast> = emptyList(),
        val searchResults: List<Podcast> = emptyList(),
        val isSearching: Boolean = false,
        val searchQuery: String = "",
        val subscribedPodcastIds: Set<String> = emptySet(),
        val isOnline: Boolean = true
    ) : HomeUiState
    data class Error(val message: String, val isOnline: Boolean = true) : HomeUiState
}

sealed interface HomeIntent {
    object FetchTrending : HomeIntent
    data class OnSearchQueryChanged(val query: String) : HomeIntent
    object PerformSearch : HomeIntent
    data class ToggleSubscription(val podcast: Podcast) : HomeIntent
    data class PlayPodcast(val podcast: Podcast) : HomeIntent
    object ClearSearch : HomeIntent
    object Retry : HomeIntent
}

sealed interface HomeEffect {
    data class ShowError(val message: String) : HomeEffect
    object NavigateToPlayer : HomeEffect
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val podcastRepository: PodcastRepository,
    private val playPodcastUseCase: PlayPodcastUseCase,
    networkMonitor: NetworkMonitor
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _isSearching = MutableStateFlow(false)
    private val _searchResults = MutableStateFlow<List<Podcast>>(emptyList())
    private val _subscribedPodcastIds = MutableStateFlow<Set<String>>(emptySet())
    
    // Repository data flow
    private val _trendingPodcastsResult = MutableStateFlow<Result<List<Podcast>>>(Result.Loading)

    private val _effect = Channel<HomeEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    val uiState: StateFlow<HomeUiState> = combine(
        combine(
            _trendingPodcastsResult,
            _searchResults,
            _isSearching
        ) { trending, searchResults, isSearching ->
            Triple(trending, searchResults, isSearching)
        },
        combine(
            _searchQuery,
            _subscribedPodcastIds,
            networkMonitor.isOnline
        ) { query, subscribedIds, isOnline ->
            Triple(query, subscribedIds, isOnline)
        }
    ) { (trending, searchResults, isSearching), (query, subscribedIds, isOnline) ->
        when (trending) {
            is Result.Loading -> HomeUiState.Loading
            is Result.Success -> HomeUiState.Success(
                trendingPodcasts = trending.data,
                searchResults = searchResults,
                isSearching = isSearching,
                searchQuery = query,
                subscribedPodcastIds = subscribedIds,
                isOnline = isOnline
            )
            is Result.Error -> HomeUiState.Error(
                message = trending.exception?.message ?: "Unknown error",
                isOnline = isOnline
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState.Loading
    )

    init {
        // Observe subscribed podcasts
        podcastRepository.getSubscribedPodcasts()
            .onEach { podcasts ->
                _subscribedPodcastIds.value = podcasts.map { it.id }.toSet()
            }
            .launchIn(viewModelScope)

        handleIntent(HomeIntent.FetchTrending)
    }

    fun handleIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.FetchTrending -> fetchTrendingPodcasts()
            is HomeIntent.OnSearchQueryChanged -> onSearchQueryChanged(intent.query)
            is HomeIntent.PerformSearch -> performSearch()
            is HomeIntent.ToggleSubscription -> toggleSubscription(intent.podcast)
            is HomeIntent.PlayPodcast -> playPodcast(intent.podcast)
            is HomeIntent.ClearSearch -> clearSearch()
            is HomeIntent.Retry -> fetchTrendingPodcasts()
        }
    }

    private fun fetchTrendingPodcasts() {
        viewModelScope.launch {
            _trendingPodcastsResult.value = Result.Loading
            try {
                val trending = podcastRepository.getTrendingPodcasts()
                _trendingPodcastsResult.value = Result.Success(trending)
            } catch (e: Exception) {
                _trendingPodcastsResult.value = Result.Error(e)
            }
        }
    }

    private fun toggleSubscription(podcast: Podcast) {
        viewModelScope.launch {
            if (_subscribedPodcastIds.value.contains(podcast.id)) {
                podcastRepository.unsubscribePodcast(podcast.id)
            } else {
                podcastRepository.subscribePodcast(podcast)
            }
        }
    }

    private fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        if (query.isBlank()) {
            _isSearching.value = false
            _searchResults.value = emptyList()
        }
    }

    private fun performSearch() {
        val query = _searchQuery.value
        if (query.isNotBlank()) {
            viewModelScope.launch {
                try {
                    val results = podcastRepository.searchPodcasts(query)
                    _searchResults.value = results
                    _isSearching.value = true
                } catch (e: Exception) {
                    _effect.send(HomeEffect.ShowError(e.message ?: "Search failed"))
                }
            }
        }
    }

    private fun playPodcast(podcast: Podcast) {
        viewModelScope.launch {
            try {
                playPodcastUseCase(podcast)
                _effect.send(HomeEffect.NavigateToPlayer)
            } catch (e: Exception) {
                _effect.send(HomeEffect.ShowError(e.message ?: "Playback failed"))
            }
        }
    }

    private fun clearSearch() {
        _searchQuery.value = ""
        _isSearching.value = false
        _searchResults.value = emptyList()
    }
}
