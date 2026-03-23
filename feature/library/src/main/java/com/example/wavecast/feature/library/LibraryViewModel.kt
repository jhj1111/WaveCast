package com.example.wavecast.feature.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wavecast.core.data.model.Podcast
import com.example.wavecast.core.data.repository.PodcastRepository
import com.example.wavecast.core.domain.PlayPodcastUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface LibraryUiState {
    object Loading : LibraryUiState
    data class Success(val podcasts: List<Podcast>) : LibraryUiState
    object Empty : LibraryUiState
    data class Error(val message: String) : LibraryUiState
}

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val podcastRepository: PodcastRepository,
    private val playPodcastUseCase: PlayPodcastUseCase
) : ViewModel() {

    val uiState: StateFlow<LibraryUiState> = podcastRepository.getSubscribedPodcasts()
        .map { podcasts ->
            if (podcasts.isEmpty()) LibraryUiState.Empty
            else LibraryUiState.Success(podcasts)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = LibraryUiState.Loading
        )

    fun playPodcast(podcast: Podcast) {
        viewModelScope.launch {
            try {
                playPodcastUseCase(podcast)
            } catch (e: Exception) {
                // 에러 처리
            }
        }
    }
}
