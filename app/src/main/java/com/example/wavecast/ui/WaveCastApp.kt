package com.example.wavecast.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.example.wavecast.core.ui.component.WaveCastTopAppBar
import com.example.wavecast.feature.home.navigation.HomeNavKey
import com.example.wavecast.feature.home.navigation.homeEntry
import com.example.wavecast.feature.library.navigation.libraryEntry
import com.example.wavecast.navigation.MainDestination
import com.example.wavecast.navigation.mainDestinations

import androidx.compose.foundation.layout.Column
import com.example.wavecast.feature.player.MiniPlayerRoute

@Composable
fun WaveCastApp() {
//    val backStack = rememberNavBackStack(HomeNavKey)
    val backStack = rememberSaveable { mutableStateListOf<NavKey>(HomeNavKey) }
//    val currentDestination = mainDestinations.getValue(backStack.last())
    val currentKey = backStack.last()
    val currentDestination = mainDestinations[currentKey] ?: MainDestination.Home

    val myEntryProvider = entryProvider {
        homeEntry(onPodcastClick = { /* Handle navigation to detail later */ })
        libraryEntry(onPodcastClick = { /* Handle navigation to detail later */ })
//        searchEntry()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            WaveCastTopAppBar(title = stringResource(currentDestination.label))
        },
        bottomBar = {
            Column {
                MiniPlayerRoute()
                WaveCastBottomBar(
                    destinations = mainDestinations,
                    currentDestination = currentDestination,
                    onNavigateToDestination = { destination ->
                        // Find the key for this destination
                        val key = mainDestinations.entries.find { it.value == destination }?.key
                        if (key != null && currentKey != key) {
                            // Clear stack and add new destination (top-level navigation behavior)
                            backStack.clear()
                            backStack.add(key)
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavDisplay(
            modifier = Modifier.padding(innerPadding),
            backStack = backStack,
            entryProvider = myEntryProvider,
            onBack = { backStack.removeLastOrNull() }
        )
    }
}

@Composable
fun WaveCastBottomBar(
    destinations: Map<NavKey, MainDestination>,
    currentDestination: MainDestination,
    onNavigateToDestination: (MainDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(modifier = modifier) {
        destinations.forEach { (_, destination) ->
            val selected = currentDestination == destination
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigateToDestination(destination) },
                icon = {
                    Icon(
                        imageVector = if (selected) destination.selectedIcon else destination.unselectedIcon,
                        contentDescription = null
                    )
                },
                label = { Text(stringResource(destination.label)) }
            )
        }
    }
}
