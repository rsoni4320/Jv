package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AvatarState
import com.example.ui.theme.JarvisAmber
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisCyanBright
import com.example.ui.theme.JarvisCyanDim
import com.example.ui.theme.JarvisDeepBlue
import com.example.ui.theme.JarvisElectricBlue
import com.example.ui.theme.JarvisGreen
import com.example.ui.theme.JarvisRed
import com.example.ui.theme.JarvisTextPrimary
import com.example.ui.theme.JarvisTextSecondary
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun HolographicAvatar(
  state: AvatarState,
  modifier: Modifier = Modifier,
  size: Dp = 200.dp,
  audioAmplitude: Float = 0f,
  onClick: () -> Unit = {}
) {
  val infiniteTransition = rememberInfiniteTransition(label = "jarvis_avatar")

  // Rotation animations
  val rotationClockwise by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
      animation = tween(
        durationMillis = if (state == AvatarState.THINKING) 3000 else 12000,
        easing = LinearEasing
      ),
      repeatMode = RepeatMode.Restart
    ),
    label = "rot_cw"
  )

  val rotationCounterClockwise by infiniteTransition.animateFloat(
    initialValue = 360f,
    targetValue = 0f,
    animationSpec = infiniteRepeatable(
      animation = tween(
        durationMillis = if (state == AvatarState.THINKING) 2000 else 8000,
        easing = LinearEasing
      ),
      repeatMode = RepeatMode.Restart
    ),
    label = "rot_ccw"
  )

  // Breathing pulse
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 0.94f,
    targetValue = 1.06f,
    animationSpec = infiniteRepeatable(
      animation = tween(
        durationMillis = when (state) {
          AvatarState.LISTENING -> 800
          AvatarState.THINKING -> 500
          AvatarState.SPEAKING -> 600
          AvatarState.ERROR -> 300
          else -> 2400
        },
        easing = FastOutSlowInEasing
      ),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulse"
  )

  val primaryColor = when (state) {
    AvatarState.IDLE -> JarvisCyan
    AvatarState.LISTENING -> JarvisCyanBright
    AvatarState.THINKING -> JarvisCyanBright
    AvatarState.SPEAKING -> JarvisCyan
    AvatarState.EXECUTING -> JarvisGreen
    AvatarState.SUCCESS -> JarvisGreen
    AvatarState.WARNING -> JarvisAmber
    AvatarState.ERROR -> JarvisRed
    AvatarState.SLEEP -> Color(0xFF1B3B6F)
    AvatarState.PRIVACY_MODE -> JarvisAmber
  }

  val secondaryColor = when (state) {
    AvatarState.WARNING -> Color(0xFFFFD54F)
    AvatarState.ERROR -> Color(0xFFFF8080)
    AvatarState.SUCCESS -> Color(0xFF80FFAA)
    else -> JarvisElectricBlue
  }

  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = modifier
  ) {
    Box(
      contentAlignment = Alignment.Center,
      modifier = Modifier
        .size(size)
        .clickable { onClick() }
    ) {
      Canvas(modifier = Modifier.size(size)) {
        val center = Offset(size.toPx() / 2f, size.toPx() / 2f)
        val baseRadius = (size.toPx() / 2f) * 0.85f

        // 1. Soft Frosted Cyan Glow Aura behind Arc Reactor
        drawCircle(
          brush = Brush.radialGradient(
            colors = listOf(
              primaryColor.copy(alpha = 0.28f),
              primaryColor.copy(alpha = 0.08f),
              Color.Transparent
            ),
            center = center,
            radius = baseRadius * 1.3f
          ),
          radius = baseRadius * 1.3f,
          center = center
        )

        // 2. Dashed Outer Orbit Ring with HUD tick marks
        rotate(rotationClockwise, pivot = center) {
          drawOuterHudRing(center, baseRadius, primaryColor)
        }

        // 3. Middle Counter-Rotating Energy Ring
        rotate(rotationCounterClockwise, pivot = center) {
          drawMiddleEnergyRing(center, baseRadius * 0.78f, secondaryColor, state == AvatarState.THINKING)
        }

        // 4. Core Glow & Arc Reactor with Concentric Frosted Rings
        val ampFactor = if (state == AvatarState.SPEAKING || state == AvatarState.LISTENING) {
          (audioAmplitude * 0.25f)
        } else {
          0f
        }
        val currentCoreRadius = (baseRadius * 0.55f) * (pulseScale + ampFactor)

        drawFrostedCoreReactor(center, currentCoreRadius, primaryColor, secondaryColor, state)

        // 5. Audio Acoustic Nodes around perimeter
        if (state == AvatarState.LISTENING || state == AvatarState.SPEAKING) {
          drawAudioRays(center, baseRadius * 0.88f, audioAmplitude, primaryColor)
        }
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Monospace Status Text
    Text(
      text = when (state) {
        AvatarState.LISTENING -> "LISTENING..."
        AvatarState.THINKING -> "PROCESSING QUERY..."
        AvatarState.SPEAKING -> "TRANSMITTING RESPONSE..."
        AvatarState.EXECUTING -> "EXECUTING PROTOCOL..."
        AvatarState.ERROR -> "SYSTEM ALERT"
        AvatarState.WARNING -> "ATTENTION REQUIRED"
        AvatarState.PRIVACY_MODE -> "PRIVACY SHIELD ACTIVE"
        AvatarState.SLEEP -> "STANDBY MODE"
        else -> "SYSTEM READY"
      },
      fontFamily = FontFamily.Monospace,
      fontWeight = FontWeight.Bold,
      fontSize = 11.sp,
      letterSpacing = 1.5.sp,
      color = primaryColor
    )
    Text(
      text = state.statusDescription,
      fontFamily = FontFamily.SansSerif,
      fontSize = 11.sp,
      color = JarvisTextSecondary
    )
  }
}

private fun DrawScope.drawOuterHudRing(center: Offset, radius: Float, color: Color) {
  // Dashed main circular track
  drawCircle(
    color = color.copy(alpha = 0.4f),
    radius = radius,
    center = center,
    style = Stroke(
      width = 2f,
      pathEffect = PathEffect.dashPathEffect(floatArrayOf(30f, 15f, 6f, 15f), 0f)
    )
  )

  // 4 Cardinal HUD brackets
  for (i in 0 until 4) {
    val angle = (i * 90) * (PI / 180.0)
    val bx = center.x + radius * cos(angle).toFloat()
    val by = center.y + radius * sin(angle).toFloat()
    drawCircle(
      color = color,
      radius = 3.5f,
      center = Offset(bx, by)
    )
  }
}

private fun DrawScope.drawMiddleEnergyRing(
  center: Offset,
  radius: Float,
  color: Color,
  isFastProcessing: Boolean
) {
  val segments = if (isFastProcessing) 6 else 4
  val sweep = 360f / segments - 20f

  for (i in 0 until segments) {
    val startAngle = i * (360f / segments)
    drawArc(
      brush = Brush.sweepGradient(
        listOf(color.copy(alpha = 0.2f), color.copy(alpha = 0.85f), color)
      ),
      startAngle = startAngle,
      sweepAngle = sweep,
      useCenter = false,
      topLeft = Offset(center.x - radius, center.y - radius),
      size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
      style = Stroke(width = 3f, cap = StrokeCap.Round)
    )
  }
}

private fun DrawScope.drawFrostedCoreReactor(
  center: Offset,
  radius: Float,
  primaryColor: Color,
  secondaryColor: Color,
  state: AvatarState
) {
  // Frosted Arc Reactor Multi-layer Gradient Core
  val radialBrush = Brush.radialGradient(
    colors = listOf(
      Color.White.copy(alpha = 0.95f),
      primaryColor.copy(alpha = 0.85f),
      secondaryColor.copy(alpha = 0.5f),
      Color.Transparent
    ),
    center = center,
    radius = radius * 1.25f
  )

  drawCircle(
    brush = radialBrush,
    radius = radius,
    center = center
  )

  // Concentric Glass Inner Rings
  drawCircle(
    color = Color.White.copy(alpha = 0.3f),
    radius = radius * 0.72f,
    center = center,
    style = Stroke(width = 2.5f)
  )

  drawCircle(
    color = primaryColor.copy(alpha = 0.6f),
    radius = radius * 0.42f,
    center = center,
    style = Stroke(width = 2f)
  )

  if (state == AvatarState.PRIVACY_MODE) {
    drawRect(
      color = primaryColor,
      topLeft = Offset(center.x - 10f, center.y - 4f),
      size = androidx.compose.ui.geometry.Size(20f, 16f)
    )
    drawArc(
      color = primaryColor,
      startAngle = 180f,
      sweepAngle = 180f,
      useCenter = false,
      topLeft = Offset(center.x - 8f, center.y - 14f),
      size = androidx.compose.ui.geometry.Size(16f, 16f),
      style = Stroke(width = 2.5f)
    )
  }
}

private fun DrawScope.drawAudioRays(
  center: Offset,
  radius: Float,
  amplitude: Float,
  color: Color
) {
  val rayCount = 18
  for (i in 0 until rayCount) {
    val angle = (i * (360f / rayCount)) * (PI / 180.0)
    val rayLength = 8f + (amplitude * 24f * ((i % 3 + 1) / 2f))
    val startX = center.x + radius * cos(angle).toFloat()
    val startY = center.y + radius * sin(angle).toFloat()
    val endX = center.x + (radius + rayLength) * cos(angle).toFloat()
    val endY = center.y + (radius + rayLength) * sin(angle).toFloat()

    drawLine(
      color = color.copy(alpha = 0.75f),
      start = Offset(startX, startY),
      end = Offset(endX, endY),
      strokeWidth = 2.5f,
      cap = StrokeCap.Round
    )
  }
}

