package com.example.wavecast.feature.home.navigation

import androidx.navigation3.runtime.NavKey
import com.example.wavecast.core.data.model.Podcast
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
object HomeNavKey : NavKey

@Serializable
object SearchNavKey : NavKey

@Serializable
data class PodcastDetailNavKey(@Contextual val podcast: Podcast) : NavKey