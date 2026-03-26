package com.example.wavecast.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.SubcomposeAsyncImage

@Composable
fun PodcastImage(
    url: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    SubcomposeAsyncImage(
        model = url,
        contentDescription = contentDescription,
        modifier = modifier.clip(MaterialTheme.shapes.medium),
        contentScale = contentScale,
        loading = {
            LoadingState()
        },
        error = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.LightGray)
            )
        }
    )
}

@Preview
@Composable
fun PodcastImagePreview() {
    MaterialTheme() {
        PodcastImage(
            url = "https://pbs.twimg.com/media/DmOBexcVAAIW7EE.jpg",
            contentDescription = "Example Podcast Image",
        )
    }
}