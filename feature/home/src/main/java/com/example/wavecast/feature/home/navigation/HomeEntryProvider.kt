package com.example.wavecast.feature.home.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.example.wavecast.core.data.model.Podcast
import com.example.wavecast.feature.home.HomeRoute
import com.example.wavecast.feature.home.detail.PodcastDetailRoute

fun EntryProviderScope<NavKey>.homeEntry(onPodcastClick: (Podcast) -> Unit) {
    entry<HomeNavKey> {
        HomeRoute(onPodcastClick = onPodcastClick)
    }
}

fun EntryProviderScope<NavKey>.podcastDetailEntry(onBackClick: () -> Unit) {
    entry<PodcastDetailNavKey> { key ->
        PodcastDetailRoute(
            podcast = key.podcast,
            onBackClick = onBackClick
        )
    }
}
