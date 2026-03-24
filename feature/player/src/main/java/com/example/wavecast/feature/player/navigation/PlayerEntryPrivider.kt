package com.example.wavecast.feature.player.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.example.wavecast.feature.player.PlayerRoute

fun EntryProviderScope<NavKey>.playerEntry(onBackClick: () -> Unit) {
    entry<PlayerNavKey> {
        PlayerRoute(onBackClick = onBackClick)
    }
}