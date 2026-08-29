package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.SdCard
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.JarvisViewModel
import com.example.ui.components.HolographicCard
import com.example.ui.theme.JarvisAmber
import com.example.ui.theme.JarvisBackground
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisCyanBright
import com.example.ui.theme.JarvisGreen
import com.example.ui.theme.JarvisRed
import com.example.ui.theme.JarvisTextPrimary
import com.example.ui.theme.JarvisTextSecondary

@Composable
fun TelemetryScreen(
  viewModel: JarvisViewModel,
  modifier: Modifier = Modifier
) {
  val telemetry by viewModel.telemetry.collectAsStateWithLifecycle()

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(JarvisBackground)
      .padding(horizontal = 14.dp)
      .verticalScroll(rememberScrollState())
  ) {
    Spacer(modifier = Modifier.height(10.dp))

    // Header
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(
        imageVector = Icons.Default.MonitorHeart,
        contentDescription = null,
        tint = JarvisCyan,
        modifier = Modifier.size(24.dp)
      )
      Spacer(modifier = Modifier.width(8.dp))
      Column {
        Text(
          text = "HARDWARE & SENSOR TELEMETRY",
          fontFamily = FontFamily.Monospace,
          fontWeight = FontWeight.Bold,
          fontSize = 14.sp,
          color = JarvisCyan
        )
        Text(
          text = "Live kernel metrics, memory bus, and power subsystem",
          fontFamily = FontFamily.SansSerif,
          fontSize = 11.sp,
          color = JarvisTextSecondary
        )
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Battery Detailed Card
    HolographicCard(modifier = Modifier.fillMaxWidth()) {
      Column {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.BatteryFull, contentDescription = null, tint = JarvisCyan, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("POWER SUBSYSTEM", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = JarvisCyan)
          }
          Text(
            text = if (telemetry.isCharging) "CHARGING (AC/USB)" else "DISCHARGING",
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            color = if (telemetry.isCharging) JarvisGreen else JarvisAmber
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text("CAPACITY: ${telemetry.batteryPercent}%", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = JarvisTextPrimary)
          Text("HEALTH: NOMINAL (Good)", fontFamily = FontFamily.Monospace, fontSize = 12.sp, color = JarvisGreen)
        }

        Spacer(modifier = Modifier.height(6.dp))

        LinearProgressIndicator(
          progress = { telemetry.batteryPercent / 100f },
          modifier = Modifier.fillMaxWidth().height(6.dp),
          color = if (telemetry.batteryPercent < 20) JarvisRed else JarvisCyan,
          trackColor = Color(0x3300F0FF)
        )
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // RAM Memory Card
    HolographicCard(modifier = Modifier.fillMaxWidth()) {
      Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Memory, contentDescription = null, tint = JarvisCyan, modifier = Modifier.size(20.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("RAM COMPUTATIONAL POOL", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = JarvisCyan)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text("ALLOCATED: ${telemetry.usedRamPercent}%", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = JarvisTextPrimary)
          Text("${telemetry.freeRamMb} MB Free / ${telemetry.totalRamMb} MB Total", fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = JarvisCyanBright)
        }

        Spacer(modifier = Modifier.height(6.dp))

        LinearProgressIndicator(
          progress = { telemetry.usedRamPercent / 100f },
          modifier = Modifier.fillMaxWidth().height(6.dp),
          color = JarvisCyan,
          trackColor = Color(0x3300F0FF)
        )
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Storage Card
    HolographicCard(modifier = Modifier.fillMaxWidth()) {
      Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.SdCard, contentDescription = null, tint = JarvisCyan, modifier = Modifier.size(20.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("INTERNAL FLASH STORAGE", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = JarvisCyan)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text("AVAILABLE: ${telemetry.availableStorageGb} GB", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = JarvisTextPrimary)
          Text("TOTAL: ${telemetry.totalStorageGb} GB", fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = JarvisCyanBright)
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Network & Hardware Details
    HolographicCard(modifier = Modifier.fillMaxWidth()) {
      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = JarvisCyan, modifier = Modifier.size(20.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("TERMINAL METADATA", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = JarvisCyan)
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text("DEVICE: ${telemetry.deviceModel}", fontFamily = FontFamily.Monospace, fontSize = 12.sp, color = JarvisTextPrimary)
        Text("KERNEL / OS: ${telemetry.osVersion}", fontFamily = FontFamily.Monospace, fontSize = 12.sp, color = JarvisTextPrimary)
        Text("NETWORK LINK: ${telemetry.networkType} (${if (telemetry.isOnline) "ONLINE" else "OFFLINE"})", fontFamily = FontFamily.Monospace, fontSize = 12.sp, color = if (telemetry.isOnline) JarvisGreen else JarvisAmber)
        Text("SYSTEM CHRONOMETER: ${telemetry.currentTimeFormatted} • ${telemetry.currentDateFormatted}", fontFamily = FontFamily.Monospace, fontSize = 12.sp, color = JarvisCyanBright)
      }
    }

    Spacer(modifier = Modifier.height(20.dp))
  }
}
