package com.example.wavecast.feature.library

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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface LibraryUiState {
    object Loading : LibraryUiState
    data class Success(
        val podcasts: List<Podcast>,
        val isOnline: Boolean = true
    ) : LibraryUiState
    data class Empty(val isOnline: Boolean = true) : LibraryUiState
    data class Error(val message: String? = null, val isOnline: Boolean = true) : LibraryUiState
}

sealed interface LibraryIntent {
    data class PlayPodcast(val podcast: Podcast) : LibraryIntent
    object Retry : LibraryIntent
}

sealed interface LibraryEffect {
    data class ShowError(val message: String) : LibraryEffect
}

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val podcastRepository: PodcastRepository,
    private val playPodcastUseCase: PlayPodcastUseCase,
    networkMonitor: NetworkMonitor
) : ViewModel() {

    private val _effect = Channel<LibraryEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    val uiState: StateFlow<LibraryUiState> = combine(
        podcastRepository.getSubscribedPodcasts().asResult(),
        networkMonitor.isOnline
    ) { result, isOnline ->
        when (result) {
            is Result.Loading -> LibraryUiState.Loading
            is Result.Success -> {
                if (result.data.isEmpty()) LibraryUiState.Empty(isOnline)
                else LibraryUiState.Success(result.data, isOnline)
            }
            is Result.Error -> LibraryUiState.Error(result.exception?.message, isOnline)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = LibraryUiState.Loading
    )

    fun handleIntent(intent: LibraryIntent) {
        when (intent) {
            is LibraryIntent.PlayPodcast -> playPodcast(intent.podcast)
            is LibraryIntent.Retry -> { /* Subscribed podcasts flow automatically retries if it's hot */ }
        }
    }

    private fun playPodcast(podcast: Podcast) {
        viewModelScope.launch {
            try {
                playPodcastUseCase(podcast)
            } catch (e: Exception) {
                _effect.send(LibraryEffect.ShowError(e.message ?: "Playback failed"))
            }
        }
    }
}
