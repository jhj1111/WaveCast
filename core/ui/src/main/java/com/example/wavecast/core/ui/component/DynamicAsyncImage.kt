package com.example.wavecast.core.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import com.example.wavecast.core.ui.R
import com.example.wavecast.core.ui.config.SAMPLE_PLACEHOLDER_URL
import com.example.wavecast.core.ui.theme.ColorUnspecified
import com.example.wavecast.core.ui.theme.LocalTintTheme
import com.example.wavecast.core.ui.theme.WaveCastTheme

@Composable
fun DynamicAsyncImage(
    modifier: Modifier = Modifier,
    imageUrl: String,
    contentDescription: String?,
    aspectRatio: Float = 1f,
    contentScale: ContentScale = ContentScale.Crop,
    placeholder: Painter = painterResource(R.drawable.place_holder_image),
) {
    val iconTint = LocalTintTheme.current.iconTint
    var isLoading by remember { mutableStateOf(true) }
    var isError by remember { mutableStateOf(false) }
    var isEmpty by remember { mutableStateOf(false) }
    val imageLoaer = rememberAsyncImagePainter(
        model = imageUrl,
        onState = { state ->
            isLoading = state is AsyncImagePainter.State.Loading
            isEmpty = state is AsyncImagePainter.State.Empty
            isError = state is AsyncImagePainter.State.Error
        },
    )
    val isLocalInspection = LocalInspectionMode.current
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        if (isLoading && !isLocalInspection) {
            // Display a progress bar while loading
            LoadingState()
        }

        Image(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(aspectRatio.takeIf { it > 0 } ?: 1f),
            painter = if (isError.not() && !isLocalInspection) imageLoaer else placeholder,
            contentDescription = contentDescription,
            contentScale = contentScale,
            colorFilter = if (iconTint != ColorUnspecified) ColorFilter.tint(iconTint) else null,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DynamicAsyncImagePreview(
    modifier: Modifier = Modifier,
) {
    WaveCastTheme {
        DynamicAsyncImage(
            imageUrl = SAMPLE_PLACEHOLDER_URL,
            contentDescription = null,
            modifier = modifier,
        )
    }
}