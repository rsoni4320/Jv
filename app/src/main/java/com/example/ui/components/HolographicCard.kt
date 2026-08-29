package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.JarvisBorderGlow
import com.example.ui.theme.JarvisBorderSubtle
import com.example.ui.theme.JarvisCardBg
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisGlassHighlight

@Composable
fun HolographicCard(
  modifier: Modifier = Modifier,
  borderColor: Color = JarvisBorderSubtle,
  backgroundColor: Color = JarvisCardBg,
  glowAlpha: Float = 0.35f,
  cornerRadius: Dp = 16.dp,
  content: @Composable BoxScope.() -> Unit
) {
  val shape = RoundedCornerShape(cornerRadius)

  Box(
    modifier = modifier
      .clip(shape)
      .background(
        brush = Brush.verticalGradient(
          colors = listOf(
            backgroundColor,
            backgroundColor.copy(alpha = (backgroundColor.alpha * 0.75f).coerceIn(0.1f, 1f))
          )
        ),
        shape = shape
      )
      .border(
        BorderStroke(1.dp, borderColor.copy(alpha = glowAlpha)),
        shape = shape
      )
  ) {
    // Subtle frosted glass top shine highlight
    Canvas(modifier = Modifier.fillMaxSize()) {
      val w = size.width
      val h = size.height
      val cornerLen = 14.dp.toPx()

      // Top edge subtle glow gradient line
      drawLine(
        brush = Brush.horizontalGradient(
          colors = listOf(
            Color.Transparent,
            borderColor.copy(alpha = (glowAlpha * 1.2f).coerceAtMost(0.8f)),
            Color.Transparent
          )
        ),
        start = Offset(10.dp.toPx(), 0f),
        end = Offset(w - 10.dp.toPx(), 0f),
        strokeWidth = 1.5f
      )

      // Tech bracket accents
      val bracketColor = borderColor.copy(alpha = (glowAlpha * 1.4f).coerceAtMost(0.9f))
      drawLine(bracketColor, Offset(0f, 0f), Offset(cornerLen, 0f), strokeWidth = 2f)
      drawLine(bracketColor, Offset(0f, 0f), Offset(0f, cornerLen), strokeWidth = 2f)
      drawLine(bracketColor, Offset(w, 0f), Offset(w - cornerLen, 0f), strokeWidth = 2f)
      drawLine(bracketColor, Offset(w, 0f), Offset(w, cornerLen), strokeWidth = 2f)
      drawLine(bracketColor, Offset(0f, h), Offset(cornerLen, h), strokeWidth = 2f)
      drawLine(bracketColor, Offset(0f, h), Offset(0f, h - cornerLen), strokeWidth = 2f)
      drawLine(bracketColor, Offset(w, h), Offset(w - cornerLen, h), strokeWidth = 2f)
      drawLine(bracketColor, Offset(w, h), Offset(w, h - cornerLen), strokeWidth = 2f)
    }

    Box(modifier = Modifier.padding(14.dp)) {
      content()
    }
  }
}

