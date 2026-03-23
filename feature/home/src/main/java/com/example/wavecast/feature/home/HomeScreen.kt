package com.example.wavecast.feature.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.wavecast.core.data.model.Podcast
import com.example.wavecast.core.ui.component.DynamicAsyncImage
import com.example.wavecast.core.ui.component.LoadingState
import com.example.wavecast.core.ui.component.WaveCastIcons
import com.example.wavecast.core.ui.config.SAMPLE_PLACEHOLDER_URL
import com.example.wavecast.core.ui.theme.WaveCastTheme
import com.example.wavecast.core.ui.theme.spacing

@Composable
fun HomeRoute(
    onPodcastClick: (Podcast) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeScreen(
        uiState = uiState,
        onSearchQueryChanged = viewModel::onSearchQueryChanged,
        onSearchTriggered = viewModel::performSearch,
        onPodcastClick = { podcast ->
            viewModel.playPodcast(podcast)
            onPodcastClick(podcast)
        },
        onToggleSubscription = viewModel::toggleSubscription,
        modifier = modifier
    )
}

@Composable
internal fun HomeScreen(
    uiState: HomeUiState,
    onSearchQueryChanged: (String) -> Unit,
    onSearchTriggered: () -> Unit,
    onPodcastClick: (Podcast) -> Unit,
    onToggleSubscription: (Podcast) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = spacing.medium)
    ) {
        // 검색 바
        OutlinedTextField(
            value = if (uiState is HomeUiState.Success) uiState.searchQuery else "",
            onValueChange = onSearchQueryChanged,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = spacing.medium),
            placeholder = { Text(stringResource(R.string.search_hint)) },
            leadingIcon = { Icon(WaveCastIcons.Search, contentDescription = null) },
            trailingIcon = {
                if (uiState is HomeUiState.Success && uiState.searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChanged("") }) {
                        Icon(WaveCastIcons.More, contentDescription = "Clear")
                    }
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                onSearchTriggered()
                keyboardController?.hide()
            }),
            singleLine = true,
            shape = MaterialTheme.shapes.large
        )

        when (uiState) {
            is HomeUiState.Loading -> {
                LoadingState()
            }
            is HomeUiState.Success -> {
                val titleRes = if (uiState.isSearching) R.string.search_hint else R.string.popular_podcasts
                val displayList = if (uiState.isSearching) uiState.searchResults else uiState.trendingPodcasts

                Text(
                    text = stringResource(titleRes),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = spacing.medium)
                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(spacing.smallMedium),
                    contentPadding = PaddingValues(bottom = spacing.large)
                ) {
                    items(displayList, key = { it.id }) { podcast ->
                        val isSubscribed = uiState.subscribedPodcastIds.contains(podcast.id)
                        PodcastItem(
                            podcast = podcast,
                            isSubscribed = isSubscribed,
                            onToggleSubscription = { onToggleSubscription(podcast) },
                            onClick = { onPodcastClick(podcast) }
                        )
                    }
                }
            }
            is HomeUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = uiState.message, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
fun PodcastItem(
    podcast: Podcast,
    isSubscribed: Boolean,
    onToggleSubscription: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        DynamicAsyncImage(
            imageUrl = podcast.imageUrl,
            contentDescription = podcast.title,
            modifier = Modifier.size(spacing.extraExtraLarge),
        )
        
        Spacer(modifier = Modifier.width(spacing.medium))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = podcast.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            Text(
                text = podcast.author,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }

        IconButton(onClick = onToggleSubscription) {
            Icon(
                imageVector = if (isSubscribed) WaveCastIcons.Favorite else WaveCastIcons.FavoriteBorder,
                contentDescription = if (isSubscribed) "Unsubscribe" else "Subscribe",
                tint = if (isSubscribed) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    WaveCastTheme {
        HomeScreen(
            uiState = HomeUiState.Success(
                trendingPodcasts = listOf(
                    Podcast("1", "Preview 1", "Author 1", "", "", SAMPLE_PLACEHOLDER_URL),
                    Podcast("2", "Preview 2", "Author 2", "", "", "")
                ),
                subscribedPodcastIds = setOf("2")
            ),
            onSearchQueryChanged = {},
            onSearchTriggered = {},
            onPodcastClick = {},
            onToggleSubscription = {}
        )
    }
}
