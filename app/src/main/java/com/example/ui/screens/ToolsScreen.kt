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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.JarvisViewModel
import com.example.ui.components.HolographicCard
import com.example.ui.theme.JarvisBackground
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisCyanBright
import com.example.ui.theme.JarvisTextPrimary
import com.example.ui.theme.JarvisTextSecondary

data class ToolGridItem(
  val id: String,
  val title: String,
  val description: String,
  val icon: ImageVector
)

@Composable
fun ToolsScreen(
  viewModel: JarvisViewModel,
  modifier: Modifier = Modifier
) {
  val tools = listOf(
    ToolGridItem("toggle_flashlight", "Tactical Light", "Toggle camera LED torch", Icons.Default.FlashlightOn),
    ToolGridItem("open_browser", "Web Browser", "Open Chrome / Web Navigator", Icons.Default.Language),
    ToolGridItem("open_youtube", "YouTube", "Launch media streaming feed", Icons.Default.VideoLibrary),
    ToolGridItem("search_web", "Google Search", "Global search query intent", Icons.Default.Search),
    ToolGridItem("open_maps", "Satellite Maps", "Geospatial coordinate maps", Icons.Default.Map),
    ToolGridItem("wifi_settings", "WiFi Setup", "Wireless network manager", Icons.Default.Wifi),
    ToolGridItem("bluetooth_settings", "Bluetooth Link", "Device communications panel", Icons.Default.Bluetooth),
    ToolGridItem("battery_settings", "Battery Diagnostics", "Hardware power consumption", Icons.Default.BatteryChargingFull),
    ToolGridItem("open_clock", "Chronometer", "Alarms and timer interface", Icons.Default.Alarm),
    ToolGridItem("open_settings", "System Settings", "Terminal OS configuration", Icons.Default.Settings)
  )

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
        imageVector = Icons.Default.Handyman,
        contentDescription = null,
        tint = JarvisCyan,
        modifier = Modifier.size(24.dp)
      )
      Spacer(modifier = Modifier.width(8.dp))
      Column {
        Text(
          text = "SUBSYSTEM CONTROLS & TOOLS",
          fontFamily = FontFamily.Monospace,
          fontWeight = FontWeight.Bold,
          fontSize = 14.sp,
          color = JarvisCyan
        )
        Text(
          text = "Direct Android hardware & application control intents",
          fontFamily = FontFamily.SansSerif,
          fontSize = 11.sp,
          color = JarvisTextSecondary
        )
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    LazyVerticalGrid(
      columns = GridCells.Fixed(2),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier.weight(1f)
    ) {
      items(tools, key = { it.id }) { tool ->
        HolographicCard(
          modifier = Modifier
            .fillMaxWidth()
            .clickable {
              viewModel.processUserInput("execute tool ${tool.id}")
            }
        ) {
          Column {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(JarvisCyan.copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = tool.icon,
                contentDescription = null,
                tint = JarvisCyanBright,
                modifier = Modifier.size(20.dp)
              )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
              text = tool.title,
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp,
              color = JarvisTextPrimary
            )

            Text(
              text = tool.description,
              fontFamily = FontFamily.SansSerif,
              fontSize = 10.sp,
              color = JarvisTextSecondary
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))
  }
}
