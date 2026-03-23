package com.example.wavecast.navigation

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey
import com.example.wavecast.R
import com.example.wavecast.core.ui.component.WaveCastIcons
import com.example.wavecast.feature.home.navigation.HomeNavKey
import com.example.wavecast.feature.library.navigation.LibraryNavKey
import kotlinx.serialization.Serializable

sealed interface MainDestination : NavKey {
    val selectedIcon: ImageVector
    val unselectedIcon: ImageVector
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

}

val mainDestinations = mapOf(
    HomeNavKey to MainDestination.Home,
    LibraryNavKey to MainDestination.Library,
//    PlayerNavKey to MainDestination.Player,
//    SearchNavKey to MainDestination.Search,
)
