package pl.put.cocktailrecipes


import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.fillMaxSize

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.size

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas

import androidx.compose.runtime.*

import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.withTransform
import kotlinx.coroutines.delay

@Composable
fun Loading(setIsDone: () -> Unit, isDataLoading: Boolean) {
    val infiniteTransition = rememberInfiniteTransition()

    // Paths
    val glassSvgPath =
        "M130.5 249L87 272.5H202.5L157.5 249V141L286 13L279 2.5H7L3.5 13L130.5 141V249Z"
    val strawSvgPath = "M3 24.5V3H69L142.5 194L126.5 201L54.5 24.5H3Z"
    val lemonSvgPath =
        "M102.435 66.7029C61.4591 121.606 -22.6261 80.9906 11.208 3.89037L102.435 66.7029Z"

    val glassAnimationTime = 700f

    // Offsets for the animations
    val strawOffsetX = -150f
    val strawOffsetY = -700f
    val strawTargetYOffset = -335f
    val strawAnimationTime = 500

    val lemonOffsetX = -33f
    val lemonOffsetY = -700f
    val lemonTargetYOffset = -255f
    val lemonAnimationTime = 500

    var showStraw by remember { mutableStateOf(false) }
    var showLemon by remember { mutableStateOf(false) }

    LaunchedEffect(isDataLoading) {
        do {
            showStraw = false
            showLemon = false
            delay(glassAnimationTime.toLong())

            showStraw = true
            delay(strawAnimationTime.toLong() - 300)

            showLemon = true
            delay(lemonAnimationTime.toLong() + 500)
        } while (isDataLoading)

        setIsDone()
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Drawing the glass (first)
        AnimatedStrokeDrawing(
            svgPathData = glassSvgPath,
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.Center),
            durationMillis = glassAnimationTime
        )

        if (showStraw) {
            AnimatedSVGWithVerticalMove(
                svgPathData = strawSvgPath,
                strawOffsetX,
                strawOffsetY,
                strawTargetYOffset,
                strawAnimationTime,
                color = Color.Red
            )
        }

        if (showLemon) {
            AnimatedSVGWithVerticalMove(
                svgPathData = lemonSvgPath,
                lemonOffsetX,
                lemonOffsetY,
                lemonTargetYOffset,
                lemonAnimationTime,
                color = Color.Yellow
            )
        }
    }
}



@Composable
fun AnimatedStrokeDrawing(
    svgPathData: String,
    modifier: Modifier = Modifier,
    durationMillis: Float = 3000f
) {
    val path = remember { PathParser().parsePathString(svgPathData).toPath() }
    val pathMeasure = remember { PathMeasure() }
    pathMeasure.setPath(path, false)

    val pathLength = pathMeasure.length
    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        animatedProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = durationMillis.toInt(), easing = LinearEasing)
        )
    }

    Canvas(modifier = modifier) {
        val drawPath = Path()
        pathMeasure.getSegment(
            0f,
            pathLength * animatedProgress.value,
            drawPath,
            true
        )
        withTransform({
            translate(left = 115f)
        }) {
            drawPath(
                path = drawPath,
                color = Color.Black,
                style = Stroke(width = 4f)
            )
        }
    }
}

@Composable
fun AnimatedSVGWithVerticalMove(
    svgPathData: String,
    startOffsetX: Float,
    startOffsetY: Float,
    targetOffset: Float,
    durationMillis: Int,
    modifier: Modifier = Modifier,
    color: Color = Color.Black
) {
    val path = remember { PathParser().parsePathString(svgPathData).toPath() }
    val offsetX = remember { Animatable(startOffsetX) }
    val offsetY = remember { Animatable(startOffsetY) }

    LaunchedEffect(Unit) {
        offsetY.animateTo(
            targetValue = targetOffset,
            animationSpec = tween(durationMillis = durationMillis, easing = LinearEasing)
        )
    }

    Canvas(modifier = modifier) {
        withTransform({
            translate(left = offsetX.value, top = offsetY.value)
        }) {
            drawPath(
                path = path,
                color = color,
                style = Stroke(width = 4f)
            )
        }
    }
}

