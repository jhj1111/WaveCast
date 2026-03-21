package com.example.wavecast.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class PodcastFeedResponse(
    val id: Long,
    val title: String,
    val url: String,
    val image: String,
    val author: String,
    val description: String
)