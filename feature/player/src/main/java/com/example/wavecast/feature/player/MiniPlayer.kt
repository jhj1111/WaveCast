package com.example.wavecast.feature.player

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.wavecast.core.media.model.PlayerState
import com.example.wavecast.core.ui.component.DynamicAsyncImage
import com.example.wavecast.core.ui.component.WaveCastIcons
import com.example.wavecast.core.ui.theme.WaveCastTheme
import com.example.wavecast.core.ui.theme.spacing

@Composable
fun MiniPlayerRoute(
    modifier: Modifier = Modifier,
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val playerState by viewModel.playerState.collectAsState()

    if (playerState.currentTitle != null) {
        MiniPlayer(
            playerState = playerState,
            onPlayPauseClick = viewModel::togglePlayPause,
            onSkipForwardClick = viewModel::skipForward,
            onSkipBackwardClick = viewModel::skipBackward,
            modifier = modifier
        )
    }
}

@Composable
fun MiniPlayer(
    playerState: PlayerState,
    onPlayPauseClick: () -> Unit,
    onSkipForwardClick: () -> Unit,
    onSkipBackwardClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 8.dp
    ) {
        Column {
            val progress = if (playerState.duration > 0) {
                playerState.currentPosition.toFloat() / playerState.duration.toFloat()
            } else 0f
            
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(2.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = Color.Transparent
            )

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = spacing.medium),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DynamicAsyncImage(
                    imageUrl = playerState.currentImageUrl ?: "",
                    contentDescription = null,
                    modifier = Modifier.size(40.dp)
                )
                
                Spacer(modifier = Modifier.width(spacing.medium))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = playerState.currentTitle ?: "Unknown",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = playerState.currentAuthor ?: "Unknown Author",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                IconButton(onClick = onSkipBackwardClick) {
                    Icon(
                        imageVector = WaveCastIcons.SkipBackward,
                        contentDescription = "Skip Back 10s"
                    )
                }

                IconButton(onClick = onPlayPauseClick) {
                    Icon(
                        imageVector = if (playerState.isPlaying) WaveCastIcons.Pause else WaveCastIcons.Play,
                        contentDescription = if (playerState.isPlaying) "Pause" else "Play"
                    )
                }

                IconButton(onClick = onSkipForwardClick) {
                    Icon(
                        imageVector = WaveCastIcons.SkipForward,
                        contentDescription = "Skip Forward 30s"
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun MiniPlayerPreview() {
    WaveCastTheme {
        MiniPlayer(
            playerState = PlayerState(
                isPlaying = true,
                currentTitle = "Preview Episode",
                currentAuthor = "WaveCast Studio",
                duration = 100L,
                currentPosition = 30L
            ),
            onPlayPauseClick = {},
            onSkipForwardClick = {},
            onSkipBackwardClick = {}
        )
    }
}
