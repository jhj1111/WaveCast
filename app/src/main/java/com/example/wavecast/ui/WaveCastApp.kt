package com.example.wavecast.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.example.wavecast.R
import com.example.wavecast.core.ui.component.WaveCastTopAppBar
import com.example.wavecast.feature.home.navigation.HomeNavKey
import com.example.wavecast.feature.home.navigation.PodcastDetailNavKey
import com.example.wavecast.feature.home.navigation.SearchNavKey
import com.example.wavecast.feature.home.navigation.homeEntry
import com.example.wavecast.feature.home.navigation.podcastDetailEntry
import com.example.wavecast.feature.library.navigation.LibraryNavKey
import com.example.wavecast.feature.library.navigation.libraryEntry
import com.example.wavecast.feature.player.MiniPlayerRoute
import com.example.wavecast.feature.player.navigation.PlayerNavKey
import com.example.wavecast.feature.player.navigation.playerEntry
import com.example.wavecast.navigation.MainDestination
import com.example.wavecast.navigation.bottomBarDestinations
import com.example.wavecast.navigation.mainDestinations

@Composable
fun WaveCastApp(
    isOnline: Boolean
) {
    val backStack = rememberSaveable { mutableStateListOf<NavKey>(HomeNavKey) }
    val currentKey = backStack.last()
    val currentDestination = when (val key = backStack.last()) {
        is HomeNavKey -> MainDestination.Home
        is SearchNavKey -> MainDestination.Search
        is LibraryNavKey -> MainDestination.Library
        is PlayerNavKey -> MainDestination.Player
        is PodcastDetailNavKey -> MainDestination.PodcastDetail(key.podcast)
        else -> MainDestination.Home
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val networkNotConnectedMessage = "네트워크 연결이 끊어졌습니다."

    LaunchedEffect(isOnline) {
        if (!isOnline) {
            snackbarHostState.showSnackbar(
                message = networkNotConnectedMessage,
                withDismissAction = true
            )
        }
    }

    val myEntryProvider = entryProvider {
        homeEntry(onPodcastClick = { podcast ->
            backStack.add(PodcastDetailNavKey(podcast))
        })
        libraryEntry(onPodcastClick = { podcast ->
            backStack.add(PodcastDetailNavKey(podcast))
        })
        podcastDetailEntry(onBackClick = { backStack.removeLastOrNull() })
        playerEntry(onBackClick = { backStack.removeLastOrNull() })
        entry<SearchNavKey> {
            Text(
                text = stringResource(R.string.search) + " Screen Placeholder",
                modifier = Modifier.fillMaxSize()
            )
        }
    }

    val isPlayerScreen = currentDestination == MainDestination.Player
    val isDetailScreen = currentDestination is MainDestination.PodcastDetail

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (!isPlayerScreen && !isDetailScreen) {
                WaveCastTopAppBar(title = stringResource(currentDestination.label))
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (!isPlayerScreen) {
                Column {
                    MiniPlayerRoute(onMiniPlayerClick = {
                        backStack.add(PlayerNavKey)
                    })
                    if (!isDetailScreen) {
                        WaveCastBottomBar(
                            destinations = bottomBarDestinations,
                            currentDestination = currentDestination,
                            onNavigateToDestination = { destination ->
                                // Find key for destination
                                val key = mainDestinations.entries.find { it.value == destination }?.key
                                if (key != null && currentKey != key) {
                                    backStack.clear()
                                    backStack.add(key as NavKey)
                                }
                            }
                        )
                    }
                }
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
    destinations: List<MainDestination>,
    currentDestination: MainDestination,
    onNavigateToDestination: (MainDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(modifier = modifier) {
        destinations.forEach { destination ->
            val selected = currentDestination == destination
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigateToDestination(destination) },
                icon = {
                    destination.selectedIcon?.let {
                        Icon(
                            imageVector = if (selected) it else destination.unselectedIcon!!,
                            contentDescription = null
                        )
                    }
                },
                label = { Text(stringResource(destination.label)) }
            )
        }
    }
}
