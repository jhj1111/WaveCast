package com.example.wavecast.navigation

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey
import com.example.wavecast.R
import com.example.wavecast.core.ui.component.WaveCastIcons
import com.example.wavecast.feature.home.navigation.HomeNavKey
import com.example.wavecast.feature.home.navigation.SearchNavKey
import com.example.wavecast.feature.library.navigation.LibraryNavKey
import com.example.wavecast.feature.player.navigation.PlayerNavKey
import kotlinx.serialization.Serializable

sealed interface MainDestination : NavKey {
    val selectedIcon: ImageVector?
    val unselectedIcon: ImageVector?
    @get:StringRes val label: Int

    @Serializable
    data object Home : MainDestination {
        override val selectedIcon = WaveCastIcons.Home
        override val unselectedIcon = WaveCastIcons.HomeBorder
        override val label = R.string.nav_home
    }

    @Serializable
    data object Search : MainDestination {
        override val selectedIcon = WaveCastIcons.Search
        override val unselectedIcon = WaveCastIcons.SearchBorder
        override val label = R.string.nav_search
    }

    @Serializable
    data object Library : MainDestination {
        override val selectedIcon = WaveCastIcons.Library
        override val unselectedIcon = WaveCastIcons.LibraryBorder
        override val label = R.string.nav_library
    }

    @Serializable
    data object Player : MainDestination {
        override val selectedIcon = null
        override val unselectedIcon = null
        override val label = R.string.nav_player
    }
}

val mainDestinations = mapOf(
//    MainDestination.Home::class.qualifiedName!! to MainDestination.Home,
    HomeNavKey to MainDestination.Home,
    SearchNavKey to MainDestination.Search,
    LibraryNavKey to MainDestination.Library,
    PlayerNavKey to MainDestination.Player
)

val bottomBarDestinations = listOf(
    MainDestination.Home,
    MainDestination.Search,
    MainDestination.Library,
)
