package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PlanStep
import com.example.ui.theme.JarvisBorderGlow
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisCyanBright
import com.example.ui.theme.JarvisGreen
import com.example.ui.theme.JarvisRed
import com.example.ui.theme.JarvisTextPrimary
import com.example.ui.theme.JarvisTextSecondary

@Composable
fun PlanProgressCard(
  planSteps: List<PlanStep>,
  modifier: Modifier = Modifier
) {
  if (planSteps.isEmpty()) return

  var isExpanded by remember { mutableStateOf(true) }
  val completedCount = planSteps.count { it.isCompleted }
  val progress = if (planSteps.isNotEmpty()) completedCount.toFloat() / planSteps.size else 0f

  HolographicCard(
    modifier = modifier.fillMaxWidth(),
    borderColor = JarvisCyan
  ) {
    Column(modifier = Modifier.fillMaxWidth()) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { isExpanded = !isExpanded },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(24.dp)
              .clip(CircleShape)
              .background(JarvisCyan.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = if (progress >= 1f) Icons.Default.CheckCircle else Icons.Default.HourglassTop,
              contentDescription = null,
              tint = if (progress >= 1f) JarvisGreen else JarvisCyan,
              modifier = Modifier.size(16.dp)
            )
          }
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text(
              text = "EXECUTION PROTOCOL",
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp,
              letterSpacing = 0.8.sp,
              color = JarvisCyanBright
            )
            Text(
              text = "$completedCount / ${planSteps.size} Subroutines Completed",
              fontFamily = FontFamily.SansSerif,
              fontSize = 11.sp,
              color = JarvisTextSecondary
            )
          }
        }

        Icon(
          imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
          contentDescription = null,
          tint = JarvisCyan,
          modifier = Modifier.size(20.dp)
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      LinearProgressIndicator(
        progress = { progress },
        modifier = Modifier
          .fillMaxWidth()
          .height(4.dp)
          .clip(RoundedCornerShape(2.dp)),
        color = if (progress >= 1f) JarvisGreen else JarvisCyan,
        trackColor = Color(0x3300F0FF)
      )

      AnimatedVisibility(visible = isExpanded) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp)
        ) {
          planSteps.forEach { step ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(18.dp)
                  .clip(CircleShape)
                  .background(
                    when {
                      step.isCompleted -> JarvisGreen.copy(alpha = 0.25f)
                      step.isFailed -> JarvisRed.copy(alpha = 0.25f)
                      else -> JarvisCyan.copy(alpha = 0.15f)
                    }
                  ),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = "${step.stepNumber}",
                  fontFamily = FontFamily.Monospace,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = when {
                    step.isCompleted -> JarvisGreen
                    step.isFailed -> JarvisRed
                    else -> JarvisCyan
                  }
                )
              }

              Spacer(modifier = Modifier.width(8.dp))

              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "[${step.title}]",
                  fontFamily = FontFamily.Monospace,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = JarvisTextPrimary
                )
                Text(
                  text = step.description,
                  fontFamily = FontFamily.SansSerif,
                  fontSize = 11.sp,
                  color = JarvisTextSecondary
                )
              }
            }
          }
        }
      }
    }
  }
}
