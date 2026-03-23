package com.example.wavecast.core.media

import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.wavecast.core.media.model.PlayerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.net.toUri

@Singleton
class WaveCastPlayer @Inject constructor(
    private val player: ExoPlayer
) {
    private val _playerState = MutableStateFlow(PlayerState())
    val playerState: StateFlow<PlayerState> = _playerState.asStateFlow()

    private var progressJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main + Job())

    init {
        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                updateState { it.copy(isPlaying = isPlaying) }
                if (isPlaying) startProgressUpdate() else stopProgressUpdate()
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                updateState {
                    it.copy(
                        currentTitle = mediaItem?.mediaMetadata?.title?.toString(),
                        currentAuthor = mediaItem?.mediaMetadata?.artist?.toString(),
                        currentImageUrl = mediaItem?.mediaMetadata?.artworkUri?.toString(),
                        duration = player.duration.coerceAtLeast(0L)
                    )
                }
            }
        })
    }

    private fun updateState(update: (PlayerState) -> PlayerState) {
        _playerState.value = update(_playerState.value)
    }

    private fun startProgressUpdate() {
        progressJob?.cancel()
        progressJob = scope.launch {
            while (isActive) {
                updateState { it.copy(currentPosition = player.currentPosition) }
                delay(1000)
            }
        }
    }

    private fun stopProgressUpdate() {
        progressJob?.cancel()
    }

    fun play() = player.play()
    fun pause() = player.pause()
    fun stop() = player.stop()

    fun seekForward() {
        val nextPos = player.currentPosition + 30_000 // 30초 앞으로
        player.seekTo(nextPos.coerceAtMost(player.duration))
    }

    fun seekBack() {
        val prevPos = player.currentPosition - 10_000 // 10초 뒤로
        player.seekTo(prevPos.coerceAtLeast(0L))
    }
    
    // 팟캐스트 재생을 위한 기초 메서드
    fun playPodcast(url: String, title: String, author: String, imageUrl: String) {
        val mediaItem = MediaItem.Builder()
            .setUri(url)
            .setMediaId(url)
            .setMediaMetadata(
                androidx.media3.common.MediaMetadata.Builder()
                    .setTitle(title)
                    .setArtist(author)
                    .setArtworkUri(imageUrl.toUri())
                    .build()
            )
            .build()
        
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()
    }
}
