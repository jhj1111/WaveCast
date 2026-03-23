package com.example.wavecast.feature.home.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.example.wavecast.feature.home.HomeRoute

fun EntryProviderScope<NavKey>.homeEntry() {
    entry<HomeNavKey> {
        HomeRoute(onPodcastClick = { /* Handle click */ })
    }
}