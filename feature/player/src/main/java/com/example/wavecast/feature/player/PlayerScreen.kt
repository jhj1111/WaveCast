package com.example.wavecast.feature.player

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
fun PlayerRoute(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val playerState by viewModel.playerState.collectAsState()

    PlayerScreen(
        playerState = playerState,
        onBackClick = onBackClick,
        onPlayPauseClick = viewModel::togglePlayPause,
        onSkipForwardClick = viewModel::skipForward,
        onSkipBackwardClick = viewModel::skipBackward,
        onSeekChanged = viewModel::seekTo,
        modifier = modifier
    )
}

@Composable
internal fun PlayerScreen(
    playerState: PlayerState,
    onBackClick: () -> Unit,
    onPlayPauseClick: () -> Unit,
    onSkipForwardClick: () -> Unit,
    onSkipBackwardClick: () -> Unit,
    onSeekChanged: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            IconButton(onClick = onBackClick, modifier = Modifier.padding(spacing.small)) {
                Icon(WaveCastIcons.GoBack, contentDescription = "Close")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = spacing.large)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 앨범 아트
            DynamicAsyncImage(
                imageUrl = playerState.currentImageUrl ?: "",
                contentDescription = null,
                modifier = Modifier
                    .size(300.dp)
                    .aspectRatio(1f)
            )

            Spacer(modifier = Modifier.height(spacing.extraLarge))

            // 곡 정보
            Text(
                text = playerState.currentTitle ?: "Unknown",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            Text(
                text = playerState.currentAuthor ?: "Unknown Author",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(spacing.extraLarge))

            // 프로그레스 바
            Slider(
                value = playerState.currentPosition.toFloat(),
                onValueChange = onSeekChanged,
                valueRange = 0f..playerState.duration.toFloat().coerceAtLeast(1f),
                modifier = Modifier.fillMaxWidth()
            )
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Log.d("TAG-PlayerScreen", "duration: ${playerState.duration}")
                Text(text = formatTime(playerState.currentPosition), style = MaterialTheme.typography.labelMedium)
                Text(text = formatTime(playerState.duration), style = MaterialTheme.typography.labelMedium)
            }

            Spacer(modifier = Modifier.height(spacing.large))

            // 컨트롤 바
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing.large)
            ) {
                IconButton(onClick = onSkipBackwardClick) {
                    Icon(WaveCastIcons.SkipBackward, contentDescription = null, modifier = Modifier.size(32.dp))
                }

                IconButton(onClick = onPlayPauseClick) {
                    Icon(
                        imageVector = if (playerState.isPlaying) WaveCastIcons.PauseCircle else WaveCastIcons.PlayCircle,
                        contentDescription = null,
                        modifier = Modifier.size(72.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                IconButton(onClick = onSkipForwardClick) {
                    Icon(WaveCastIcons.SkipForward, contentDescription = null, modifier = Modifier.size(32.dp))
                }
            }
        }
    }
}

private fun formatTime(millis: Long): String {
    val totalSeconds = millis / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}

@Preview(showBackground = true)
@Composable
fun PlayerScreenPreview() {
    WaveCastTheme {
        PlayerScreen(
            playerState = PlayerState(
                isPlaying = false,
                currentTitle = "Detailed Episode Title Example That Might Be Long",
                currentAuthor = "Great Podcast Studio",
                duration = 300000L,
                currentPosition = 120000L
            ),
            onBackClick = {},
            onPlayPauseClick = {},
            onSkipForwardClick = {},
            onSkipBackwardClick = {},
            onSeekChanged = {}
        )
    }
}
