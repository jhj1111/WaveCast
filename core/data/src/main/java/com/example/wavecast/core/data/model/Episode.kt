package com.example.wavecast.core.data.model

data class Episode(
    val title: String,
    val audioUrl: String,
    val duration: String? = null,
    val imageUrl: String? = null
)
