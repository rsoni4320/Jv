package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.AutomationEntity
import com.example.ui.JarvisViewModel
import com.example.ui.components.HolographicCard
import com.example.ui.theme.JarvisAmber
import com.example.ui.theme.JarvisBackground
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisCyanBright
import com.example.ui.theme.JarvisGreen
import com.example.ui.theme.JarvisSurfaceElevated
import com.example.ui.theme.JarvisTextDim
import com.example.ui.theme.JarvisTextPrimary
import com.example.ui.theme.JarvisTextSecondary
import org.json.JSONArray

@Composable
fun AutomationScreen(
  viewModel: JarvisViewModel,
  modifier: Modifier = Modifier
) {
  val automations by viewModel.automations.collectAsStateWithLifecycle()

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(JarvisBackground)
      .padding(horizontal = 14.dp)
  ) {
    Spacer(modifier = Modifier.height(10.dp))

    // Header
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(
        imageVector = Icons.Default.AutoAwesome,
        contentDescription = null,
        tint = JarvisCyan,
        modifier = Modifier.size(24.dp)
      )
      Spacer(modifier = Modifier.width(8.dp))
      Column {
        Text(
          text = "AUTOMATION & PROTOCOLS",
          fontFamily = FontFamily.Monospace,
          fontWeight = FontWeight.Bold,
          fontSize = 14.sp,
          color = JarvisCyan
        )
        Text(
          text = "Conditional subroutines and system routines",
          fontFamily = FontFamily.SansSerif,
          fontSize = 11.sp,
          color = JarvisTextSecondary
        )
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    LazyColumn(
      modifier = Modifier.weight(1f),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      items(automations, key = { it.id }) { routine ->
        AutomationCard(
          routine = routine,
          onRun = { viewModel.runAutomation(routine) },
          onToggle = { isChecked ->
            viewModel.viewModelScopeLaunch {
              // Toggle routine state
            }
          }
        )
      }
    }

    Spacer(modifier = Modifier.height(16.dp))
  }
}

private fun JarvisViewModel.viewModelScopeLaunch(block: suspend () -> Unit) {
  // Helper
}

@Composable
fun AutomationCard(
  routine: AutomationEntity,
  onRun: () -> Unit,
  onToggle: (Boolean) -> Unit
) {
  val icon = when (routine.iconName) {
    "wb_sunny" -> Icons.Default.WbSunny
    "work" -> Icons.Default.Work
    "security" -> Icons.Default.Security
    "bedtime" -> Icons.Default.Bedtime
    else -> Icons.Default.AutoAwesome
  }

  val stepsList = try {
    val arr = JSONArray(routine.stepsJson)
    val l = mutableListOf<String>()
    for (i in 0 until arr.length()) l.add(arr.getString(i))
    l
  } catch (e: Exception) {
    emptyList<String>()
  }

  HolographicCard(
    modifier = Modifier.fillMaxWidth(),
    borderColor = if (routine.isEnabled) JarvisCyan else JarvisTextDim
  ) {
    Column {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(JarvisCyan.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = icon,
              contentDescription = null,
              tint = JarvisCyanBright,
              modifier = Modifier.size(20.dp)
            )
          }

          Spacer(modifier = Modifier.width(10.dp))

          Column {
            Text(
              text = routine.name,
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp,
              color = JarvisTextPrimary
            )
            if (routine.scheduleTime.isNotBlank()) {
              Text(
                text = "SCHEDULE: ${routine.scheduleTime}",
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                color = JarvisGreen
              )
            }
          }
        }

        // Run Button
        Button(
          onClick = onRun,
          colors = ButtonDefaults.buttonColors(containerColor = Color(0x3300FF88)),
          shape = RoundedCornerShape(8.dp)
        ) {
          Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = "Execute Routine",
            tint = JarvisGreen,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "RUN",
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            color = JarvisGreen
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = routine.description,
        fontFamily = FontFamily.SansSerif,
        fontSize = 12.sp,
        lineHeight = 17.sp,
        color = JarvisTextSecondary
      )

      if (stepsList.isNotEmpty()) {
        Spacer(modifier = Modifier.height(8.dp))
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .background(Color(0x33000000), RoundedCornerShape(6.dp))
            .padding(8.dp)
        ) {
          Text(
            text = "EXECUTION SUBROUTINES (${stepsList.size}):",
            fontFamily = FontFamily.Monospace,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = JarvisCyan
          )
          Spacer(modifier = Modifier.height(4.dp))
          stepsList.forEachIndexed { index, step ->
            Text(
              text = "${index + 1}. $step",
              fontFamily = FontFamily.SansSerif,
              fontSize = 11.sp,
              color = JarvisTextPrimary
            )
          }
        }
      }
    }
  }
}
