package pl.put.cocktailrecipes.utils

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollapsingToolbar(
    headerHeight: Dp,
    collapsedHeight: Dp,
    expandedHeight: Dp,
    imgSrc: String,
    onMenuClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val collapseProgress = remember(headerHeight) {
        derivedStateOf {
            val range = (headerHeight - collapsedHeight).coerceAtLeast(0.dp)
            val totalRange = (expandedHeight - collapsedHeight).coerceAtLeast(1.dp)
            (range / totalRange).coerceIn(0f, 1f)
        }
    }

    val imageAlpha = animateFloatAsState(
        targetValue = collapseProgress.value,
        animationSpec = tween(durationMillis = 300)
    )

    Box(
        modifier
            .fillMaxWidth()
            .height(headerHeight)
    ) {
        AsyncImage(
            model = imgSrc,
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .alpha(imageAlpha.value),
            contentScale = ContentScale.Crop

        )

        IconButton(
            onClick = onMenuClick,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp)
        ) {
            Icon(Icons.Filled.Menu, contentDescription = "Menu")
        }
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
        }
    }
}

