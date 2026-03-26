package com.example.wavecast.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class PodcastSearchResponse(
    val status: String,
    val feeds: List<PodcastFeedResponse>
)