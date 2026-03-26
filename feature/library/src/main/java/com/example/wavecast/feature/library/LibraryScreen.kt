package com.example.wavecast.feature.library

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.wavecast.core.data.model.Podcast
import com.example.wavecast.core.ui.component.DynamicAsyncImage
import com.example.wavecast.core.ui.component.ErrorState
import com.example.wavecast.core.ui.component.LoadingState
import com.example.wavecast.core.ui.theme.WaveCastTheme
import com.example.wavecast.core.ui.theme.spacing
import kotlinx.coroutines.flow.collectLatest

@Composable
fun LibraryRoute(
    onPodcastClick: (Podcast) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LibraryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is LibraryEffect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    LibraryScreen(
        uiState = uiState,
        onIntent = viewModel::handleIntent,
        onPodcastClick = { podcast ->
            viewModel.handleIntent(LibraryIntent.PlayPodcast(podcast))
            onPodcastClick(podcast)
        },
        modifier = modifier
    )
}

@Composable
internal fun LibraryScreen(
    uiState: LibraryUiState,
    onIntent: (LibraryIntent) -> Unit,
    onPodcastClick: (Podcast) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = spacing.medium)
    ) {
        Text(
            text = stringResource(R.string.my_library),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = spacing.medium)
        )

        when (uiState) {
            is LibraryUiState.Loading -> {
                LoadingState()
            }
            is LibraryUiState.Empty -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.empty_library_message),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            is LibraryUiState.Success -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(spacing.medium),
                    verticalArrangement = Arrangement.spacedBy(spacing.medium),
                    contentPadding = PaddingValues(bottom = spacing.large)
                ) {
                    items(uiState.podcasts, key = { it.id }) { podcast ->
                        SubscriptionItem(
                            title = podcast.title,
                            imageUrl = podcast.imageUrl,
                            onClick = { onPodcastClick(podcast) }
                        )
                    }
                }
            }
            is LibraryUiState.Error -> {
                ErrorState(
                    message = uiState.message ?: stringResource(R.string.unknown_error_message),
                    onRetry = { onIntent(LibraryIntent.Retry) }
                )
            }
        }
    }
}

@Composable
fun SubscriptionItem(
    title: String,
    imageUrl: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(spacing.extraSmall)
    ) {
        DynamicAsyncImage(
            imageUrl = imageUrl,
            contentDescription = title,
            modifier = Modifier
                .aspectRatio(1f)
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(spacing.small))
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LibraryScreenPreview() {
    WaveCastTheme {
        LibraryScreen(
            uiState = LibraryUiState.Success(
                podcasts = listOf(
                    Podcast("1", "Android Dev", "Google", "", "", ""),
                    Podcast("2", "Kotlin Talk", "JetBrains", "", "", "")
                )
            ),
            onIntent = {},
            onPodcastClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LibraryScreenEmptyPreview() {
    WaveCastTheme {
        LibraryScreen(
            uiState = LibraryUiState.Empty(),
            onIntent = {},
            onPodcastClick = {}
        )
    }
}
