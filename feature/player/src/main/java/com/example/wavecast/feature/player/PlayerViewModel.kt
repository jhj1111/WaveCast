package com.example.wavecast.feature.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wavecast.core.media.WaveCastPlayer
import com.example.wavecast.core.media.model.PlayerState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val waveCastPlayer: WaveCastPlayer
) : ViewModel() {

    val playerState: StateFlow<PlayerState> = waveCastPlayer.playerState

    fun togglePlayPause() {
        if (playerState.value.isPlaying) {
            waveCastPlayer.pause()
        } else {
            waveCastPlayer.play()
        }
    }
}
