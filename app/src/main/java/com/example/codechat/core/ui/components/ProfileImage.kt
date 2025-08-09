package com.example.codechat.core.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle // Default placeholder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation



@Composable
fun ProfileImage(
    imageUrl: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp, // Default size
    placeholder: Painter? = null, // Optional custom placeholder painter
    errorPlaceholder: Painter? = null // Optional custom error painter
) {
    val imagePainter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .crossfade(true)
            .transformations(CircleCropTransformation())
            .build()
    )

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape) // Clip the Box to a circle shape
            .background(MaterialTheme.colorScheme.surfaceVariant), // Placeholder background
        contentAlignment = Alignment.Center
    ) {
        when (imagePainter.state) {
            is AsyncImagePainter.State.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.size(size * 0.6f),
                    strokeWidth = (size * 0.05f).coerceAtLeast(1.dp)
                )
            }

            is AsyncImagePainter.State.Error -> {
                val customErrorPainter = errorPlaceholder
                if (customErrorPainter != null) {
                    Icon(
                        painter = customErrorPainter,
                        contentDescription = contentDescription ?: "Error loading image",
                        modifier = Modifier.size(size * 0.8f),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.AccountCircle,
                        contentDescription = contentDescription ?: "Error loading image",
                        modifier = Modifier.size(size * 0.8f),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            is AsyncImagePainter.State.Empty -> {
                val customPlaceholderPainter = placeholder
                if (customPlaceholderPainter != null) {
                    Icon(
                        painter = customPlaceholderPainter,
                        contentDescription = contentDescription ?: "No image",
                        modifier = Modifier.size(size * 0.8f),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.AccountCircle,
                        contentDescription = contentDescription ?: "No image",
                        modifier = Modifier.size(size * 0.8f),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            is AsyncImagePainter.State.Success -> {
                Image(
                    painter = imagePainter,
                    contentDescription = contentDescription,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
