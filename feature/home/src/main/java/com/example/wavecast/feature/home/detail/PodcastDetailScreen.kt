package com.example.wavecast.feature.home.detail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.wavecast.core.data.model.Episode
import com.example.wavecast.core.data.model.Podcast
import com.example.wavecast.core.ui.component.DynamicAsyncImage
import com.example.wavecast.core.ui.component.LoadingState
import com.example.wavecast.core.ui.component.WaveCastIcons
import com.example.wavecast.core.ui.theme.WaveCastTheme
import com.example.wavecast.core.ui.theme.spacing

@Composable
fun PodcastDetailRoute(
    podcast: Podcast,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PodcastDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(podcast) {
        viewModel.fetchPodcastDetail(podcast)
    }

    PodcastDetailScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onEpisodeClick = viewModel::playEpisode,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PodcastDetailScreen(
    uiState: PodcastDetailUiState,
    onBackClick: () -> Unit,
    onEpisodeClick: (Episode) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Podcast Detail") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(WaveCastIcons.GoBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        when (uiState) {
            is PodcastDetailUiState.Loading -> LoadingState()
            is PodcastDetailUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(uiState.message, color = MaterialTheme.colorScheme.error)
                }
            }
            is PodcastDetailUiState.Success -> {
                LazyColumn(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                    contentPadding = PaddingValues(bottom = spacing.large)
                ) {
                    // 헤더: 팟캐스트 정보
                    item {
                        PodcastHeader(uiState.podcast)
                    }

                    // 에피소드 리스트
                    items(uiState.episodes) { episode ->
                        EpisodeItem(episode, onClick = { onEpisodeClick(episode) })
                        HorizontalDivider(modifier = Modifier.padding(horizontal = spacing.medium))
                    }
                }
            }
        }
    }
}

@Composable
fun PodcastHeader(podcast: Podcast) {
    val spacing = MaterialTheme.spacing
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(spacing.medium),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DynamicAsyncImage(
            imageUrl = podcast.imageUrl,
            contentDescription = null,
            modifier = Modifier.size(200.dp)
        )
        Spacer(modifier = Modifier.height(spacing.medium))
        Text(text = podcast.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(text = podcast.author, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.secondary)
        Spacer(modifier = Modifier.height(spacing.small))
        Text(
            text = podcast.description,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun EpisodeItem(episode: Episode, onClick: () -> Unit) {
    val spacing = MaterialTheme.spacing
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(spacing.medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = episode.title, style = MaterialTheme.typography.titleSmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
            if (episode.duration != null) {
                Text(text = episode.duration!!, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Icon(WaveCastIcons.PlayCircleOutline, contentDescription = "Play", tint = MaterialTheme.colorScheme.primary)
    }
}

@Preview(showBackground = true)
@Composable
fun PodcastDetailPreview() {
    WaveCastTheme {
        PodcastDetailScreen(
            uiState = PodcastDetailUiState.Success(
                podcast = Podcast("1", "Android Podcast", "Google", "All about Android", "", ""),
                episodes = listOf(
                    Episode("Episode 1", "", "30:00"),
                    Episode("Episode 2", "", "45:00")
                )
            ),
            onBackClick = {},
            onEpisodeClick = {}
        )
    }
}
