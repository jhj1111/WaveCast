package com.example.wavecast.navigation

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey
import com.example.wavecast.R
import com.example.wavecast.core.data.model.Podcast
import com.example.wavecast.core.ui.component.WaveCastIcons
import com.example.wavecast.feature.home.navigation.HomeNavKey
import com.example.wavecast.feature.home.navigation.PodcastDetailNavKey
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

    @Serializable
    data class PodcastDetail(val podcast: Podcast) : MainDestination {
        override val selectedIcon = null
        override val unselectedIcon = null
        override val label = R.string.nav_detail
    }
}

val mainDestinations = mapOf(
    HomeNavKey to MainDestination.Home,
    SearchNavKey to MainDestination.Search,
    LibraryNavKey to MainDestination.Library,
    PlayerNavKey to MainDestination.Player,
    PodcastDetailNavKey to MainDestination.PodcastDetail(Podcast("", "", "", "", "", "")) // placeholder for mapping
)

val bottomBarDestinations = listOf(
    MainDestination.Home,
    MainDestination.Search,
    MainDestination.Library,
)
