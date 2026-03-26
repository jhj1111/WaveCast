package com.example.wavecast.core.data.model

data class Podcast(
    val id: String,
    val title: String,
    val author: String,
    val description: String,
    val imageUrl: String,
    val feedUrl: String,
    val isSubscribed: Boolean = false
)
