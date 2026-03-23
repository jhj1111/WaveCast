package com.example.wavecast.core.network.model

data class EpisodeResponse(
    val title: String,
    val audioUrl: String,
    val duration: String? = null,
    val imageUrl: String? = null
)
