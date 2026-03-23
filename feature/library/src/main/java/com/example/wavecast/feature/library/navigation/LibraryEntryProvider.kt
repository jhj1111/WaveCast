package com.example.wavecast.feature.library.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.example.wavecast.feature.library.LibraryRoute

fun EntryProviderScope<NavKey>.libraryEntry() {
    entry<LibraryNavKey> {
        LibraryRoute(onPodcastClick = { /* Handle click */ })
    }
}