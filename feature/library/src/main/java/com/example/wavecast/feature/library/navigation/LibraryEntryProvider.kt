package com.example.wavecast.feature.library.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.example.wavecast.core.data.model.Podcast
import com.example.wavecast.feature.library.LibraryRoute

fun EntryProviderScope<NavKey>.libraryEntry(onPodcastClick: (Podcast) -> Unit) {
    entry<LibraryNavKey> {
        LibraryRoute(onPodcastClick = onPodcastClick)
    }
}