package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.JarvisBorderSubtle
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisTextPrimary
import com.example.ui.theme.JarvisTextSecondary

@Composable
fun QuickCommandChips(
  onCommandSelected: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val commands = listOf(
    "What's my status?",
    "Start morning routine",
    "Open browser",
    "Search the web",
    "Toggle tactical flashlight",
    "Check memory",
    "Open settings",
    "Read scheduled reminders",
    "Activate work protocol",
    "Calculate 1024 * 768"
  )

  Row(
    modifier = modifier
      .fillMaxWidth()
      .horizontalScroll(rememberScrollState())
      .padding(vertical = 4.dp)
  ) {
    commands.forEach { cmd ->
      Surface(
        onClick = { onCommandSelected(cmd) },
        shape = RoundedCornerShape(20.dp),
        color = Color(0x33083344), // Frosted cyan-950/20
        border = BorderStroke(1.dp, Color(0x3300F0FF)), // Translucent cyan border
        modifier = Modifier.padding(end = 8.dp)
      ) {
        Text(
          text = cmd,
          fontFamily = FontFamily.Monospace,
          fontSize = 11.sp,
          fontWeight = FontWeight.Medium,
          color = JarvisTextSecondary,
          modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
        )
      }
    }
  }
}

