package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisCyanBright
import com.example.ui.theme.JarvisElectricBlue

@Composable
fun WaveformVisualizer(
  isActive: Boolean,
  amplitude: Float,
  modifier: Modifier = Modifier,
  height: Dp = 38.dp,
  barCount: Int = 30,
  primaryColor: Color = JarvisCyan
) {
  val transition = rememberInfiniteTransition(label = "waveform")

  val animOffset by transition.animateFloat(
    initialValue = 0f,
    targetValue = 6.28f,
    animationSpec = infiniteRepeatable(
      animation = tween(1200, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "wave_phase"
  )

  Canvas(
    modifier = modifier
      .fillMaxWidth()
      .height(height)
  ) {
    val canvasWidth = size.width
    val canvasHeight = size.height
    val centerY = canvasHeight / 2f
    val barWidth = (canvasWidth / barCount) * 0.45f
    val spacing = canvasWidth / barCount

    for (i in 0 until barCount) {
      val x = i * spacing + spacing / 2f

      // Calculate bar height based on amplitude, position, and wave animation
      val waveFactor = if (isActive) {
        val sineWave = Math.sin((i.toDouble() / barCount * 4 * Math.PI) + animOffset.toDouble()).toFloat()
        val ampMod = (amplitude.coerceIn(0.12f, 1f))
        val baseH = (canvasHeight * 0.12f)
        (baseH + (canvasHeight * 0.78f * ampMod * Math.abs(sineWave))).coerceIn(4f, canvasHeight)
      } else {
        4f
      }

      val topY = centerY - (waveFactor / 2f)
      val bottomY = centerY + (waveFactor / 2f)

      val barBrush = Brush.verticalGradient(
        colors = listOf(
          primaryColor.copy(alpha = 0.2f),
          JarvisCyanBright,
          primaryColor.copy(alpha = 0.2f)
        ),
        startY = topY,
        endY = bottomY
      )

      drawLine(
        brush = barBrush,
        start = Offset(x, topY),
        end = Offset(x, bottomY),
        strokeWidth = barWidth,
        cap = StrokeCap.Round
      )
    }
  }
}

