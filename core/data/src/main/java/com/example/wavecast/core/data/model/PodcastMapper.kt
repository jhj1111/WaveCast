package com.example.wavecast.core.data.model

import com.example.wavecast.core.database.model.PodcastEntity
import com.example.wavecast.core.network.model.PodcastFeedResponse

fun PodcastFeedResponse.asExternalModel() = Podcast(
    id = id.toString(),
    title = title,
    author = author,
    description = description,
    imageUrl = image,
    feedUrl = url
)

fun PodcastEntity.asExternalModel() = Podcast(
    id = id,
    title = title,
    author = author,
    description = description,
    imageUrl = imageUrl,
    feedUrl = feedUrl,
    isSubscribed = isSubscribed
)

fun Podcast.asEntity() = PodcastEntity(
    id = id,
    title = title,
    author = author,
    description = description,
    imageUrl = imageUrl,
    feedUrl = feedUrl,
    isSubscribed = isSubscribed
)
