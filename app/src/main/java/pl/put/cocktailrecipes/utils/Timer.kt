package pl.put.cocktailrecipes.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale
import java.util.Timer
import kotlin.concurrent.scheduleAtFixedRate

fun formatTime(timeInSec: Int): String {
    val minutes = timeInSec / 60 // integer division

    return "${String.format(Locale.US, "%02d", minutes)}:${
        String.format(
            Locale.US,
            "%02d",
            timeInSec - (minutes * 60)
        )
    }"
}

@Composable
fun TimerComponent(modifier: Modifier = Modifier) {
    var isPlaying by remember { mutableStateOf(false) }
    var timeInSec by remember { mutableIntStateOf(0) } // handle 59:99
    var timer by remember { mutableStateOf(Timer()) }

    val playButtonColor = Color(0xff05ac2b)
    val cancelButtonColor = Color(0xffc84011)
    val pauseButtonColor = Color(0xffdea501)

    if (timeInSec == (59 * 60) + 59) {
        isPlaying = false
        timer.cancel()
    }
    Row(
        modifier = modifier
            .background(Color.DarkGray, shape = RoundedCornerShape(10.dp))
            .heightIn(max = 75.dp)
            .widthIn(max = 300.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        Box {
            Text(
                formatTime(timeInSec),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                modifier = Modifier
                    .padding(start = 10.dp, end = 10.dp)
                    .align(Alignment.Center)
            )
        }

        PlayButton(color = playButtonColor) {
            if (isPlaying) {
                return@PlayButton
            }

            isPlaying = true
            timer = Timer()
            timer.scheduleAtFixedRate(1000L, 1000L) {
                timeInSec += 1
            }
        }

        PauseButton(pauseButtonColor) {
            if (!isPlaying) {
                return@PauseButton
            }
            isPlaying = false
            timer.cancel()
        }

        CancelButton(cancelButtonColor) {
            if (!isPlaying) {
                timeInSec = 0
                return@CancelButton
            }
            isPlaying = false
            timer.cancel()
            timeInSec = 0
        }
    }
}

@Composable
fun TimerButton(
    onClickEvent: () -> Unit,
    color: Color,
    shape: RoundedCornerShape = RoundedCornerShape(0.dp),
    content: @Composable () -> Unit
) {
    Button(
        onClick = onClickEvent,
        shape = shape,
        colors = ButtonDefaults.buttonColors(containerColor = color),
        modifier = Modifier
            .fillMaxHeight()
    ) {
        content()
    }
}


@Composable
fun PlayButton(color: Color, onPlay: () -> Unit) {
    TimerButton(
        color = color,
        onClickEvent = onPlay
    ) {
        Icon(
            imageVector = Icons.Filled.PlayArrow,
            contentDescription = "Play",
            tint = Color.White
        )
    }
}


var _Pause: ImageVector? = null


val Pause: ImageVector
    get() {
        if (_Pause != null) {
            return _Pause!!
        }
        _Pause = ImageVector.Builder(
            name = "Pause",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = null,
                fillAlpha = 1.0f,
                stroke = SolidColor(Color(0xFF0F172A)),
                strokeAlpha = 1.0f,
                strokeLineWidth = 1.5f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(15.75f, 5.25f)
                lineTo(15.75f, 18.75f)
                moveTo(8.25f, 5.25f)
                verticalLineTo(18.75f)
            }
        }.build()
        return _Pause!!
    }


@Composable
fun PauseButton(color: Color, onPause: () -> Unit) {
    TimerButton(
        color = color,
        onClickEvent = onPause
    ) {
        Icon(
            imageVector = Pause,
            contentDescription = "Pause",
            tint = Color.White
        )
    }
}


fun filledSquareWithBorderImageVector(): ImageVector {
    return ImageVector.Builder(
        name = "FilledSquareWithBorder",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = SolidColor(Color.Blue),
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 2f
        ) {
            moveTo(4f, 4f)
            lineTo(20f, 4f)
            lineTo(20f, 20f)
            lineTo(4f, 20f)
            close()
        }
    }.build()
}

@Composable
fun CancelButton(color: Color, onCancel: () -> Unit) {
    TimerButton(
        color = color,
        onClickEvent = onCancel,
        shape = RoundedCornerShape(
            bottomEnd = 10.dp,
            topEnd = 10.dp
        )
    ) {
        Icon(
            imageVector = filledSquareWithBorderImageVector(),
            contentDescription = "Cancel",
            tint = Color.White
        )
    }
}