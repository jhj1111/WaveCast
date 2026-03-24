package com.example.wavecast.ui

import androidx.compose.foundation.layout.Column
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
import com.example.wavecast.R
import com.example.wavecast.core.ui.component.WaveCastTopAppBar
import com.example.wavecast.feature.home.navigation.HomeNavKey
import com.example.wavecast.feature.home.navigation.homeEntry
import com.example.wavecast.feature.library.navigation.libraryEntry
import com.example.wavecast.feature.player.MiniPlayerRoute
import com.example.wavecast.feature.player.navigation.PlayerNavKey
import com.example.wavecast.feature.player.navigation.playerEntry
import com.example.wavecast.navigation.MainDestination
import com.example.wavecast.navigation.bottomBarDestinations
import com.example.wavecast.navigation.mainDestinations

@Composable
fun WaveCastApp() {
    val backStack = rememberSaveable { mutableStateListOf<NavKey>(HomeNavKey) }
    val currentKey = backStack.last()
    val currentDestination = mainDestinations[currentKey] ?: MainDestination.Home

    val myEntryProvider = entryProvider {
        homeEntry(onPodcastClick = { /* Handle navigation to detail later */ })
        libraryEntry(onPodcastClick = { /* Handle navigation to detail later */ })
        playerEntry(onBackClick = { backStack.removeLastOrNull() })
        entry<MainDestination.Search> {
            Text(
                text = stringResource(R.string.search) + " Screen Placeholder",
                modifier = Modifier.fillMaxSize()
            )
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            // Player 화면일 때는 상단바를 숨기거나 별도 처리 (PlayerScreen 내부에 이미 있음)
            if (currentDestination != MainDestination.Player) {
                WaveCastTopAppBar(title = stringResource(currentDestination.label))
            }
        },
        bottomBar = {
            // Player 화면이 아닐 때만 하단바와 미니플레이어 표시
            if (currentDestination != MainDestination.Player) {
                Column {
                    MiniPlayerRoute(onMiniPlayerClick = {
                        backStack.add(PlayerNavKey)
                    })
                    WaveCastBottomBar(
                        destinations = bottomBarDestinations,
                        currentDestination = currentDestination,
                        onNavigateToDestination = { destination ->
                            // Find the key for this destination
                            val key = mainDestinations.entries.find { it.value == destination }?.key
                            if (key != null && currentKey != key) {
                                backStack.clear()
                                backStack.add(key)
                            }
                        }
                    )
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
