package com.example.wavecast.core.media.model

data class PlayerState(
    val isPlaying: Boolean = false,
    val currentTitle: String? = null,
    val currentAuthor: String? = null,
    val currentImageUrl: String? = null,
    val duration: Long = 0L,
    val currentPosition: Long = 0L
)
