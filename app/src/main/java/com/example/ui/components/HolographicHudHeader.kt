package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DeviceTelemetry
import com.example.ui.theme.JarvisAmber
import com.example.ui.theme.JarvisBorderSubtle
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisCyanBright
import com.example.ui.theme.JarvisCyanDim
import com.example.ui.theme.JarvisGlassBackdrop
import com.example.ui.theme.JarvisGreen
import com.example.ui.theme.JarvisRed
import com.example.ui.theme.JarvisTextDim
import com.example.ui.theme.JarvisTextPrimary
import com.example.ui.theme.JarvisTextSecondary

@Composable
fun HolographicHudHeader(
  telemetry: DeviceTelemetry,
  activeModelName: String,
  isPrivacyMode: Boolean,
  isVoiceActive: Boolean,
  onMenuClick: () -> Unit,
  onTogglePrivacy: () -> Unit,
  onToggleVoice: () -> Unit,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .background(JarvisGlassBackdrop)
      .border(
        width = 1.dp,
        color = JarvisBorderSubtle,
        shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
      )
      .padding(horizontal = 14.dp, vertical = 10.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      // Left: Sidebar Menu + System Status & App Title
      Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(
          onClick = onMenuClick,
          modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0x33083344))
            .border(1.dp, Color(0x3300F0FF), RoundedCornerShape(10.dp))
        ) {
          Icon(
            imageVector = Icons.Default.Menu,
            contentDescription = "System Navigation Sidebar",
            tint = JarvisCyan,
            modifier = Modifier.size(18.dp)
          )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column {
          Text(
            text = "SYSTEM STATUS",
            fontFamily = FontFamily.Monospace,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.2.sp,
            color = JarvisCyanDim
          )
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = if (telemetry.isOnline) "J.A.R.V.I.S. ONLINE" else "J.A.R.V.I.S. OFFLINE",
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp,
              letterSpacing = 0.5.sp,
              color = if (telemetry.isOnline) JarvisCyanBright else JarvisAmber
            )
            Spacer(modifier = Modifier.width(5.dp))
            Box(
              modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(if (telemetry.isOnline) JarvisGreen else JarvisAmber)
            )
          }
        }
      }

      // Right: AI Model, Telemetry, and Action Toggles
      Row(verticalAlignment = Alignment.CenterVertically) {
        // AI Model Info block
        Column(horizontalAlignment = Alignment.End) {
          Text(
            text = "AI MODEL",
            fontFamily = FontFamily.Monospace,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            color = JarvisCyanDim
          )
          Text(
            text = activeModelName.take(12).uppercase(),
            fontFamily = FontFamily.Monospace,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = JarvisTextPrimary
          )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Vertical divider
        Box(
          modifier = Modifier
            .width(1.dp)
            .height(20.dp)
            .background(Color(0x4000F0FF))
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Battery / Power badge
        Column(
          horizontalAlignment = Alignment.End,
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0x2E083344))
            .border(1.dp, Color(0x2600F0FF), RoundedCornerShape(8.dp))
            .padding(horizontal = 6.dp, vertical = 3.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = if (telemetry.isCharging) Icons.Default.BatteryChargingFull else Icons.Default.BatteryFull,
              contentDescription = "Battery Status",
              tint = if (telemetry.batteryPercent < 20) JarvisRed else JarvisCyan,
              modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(
              text = "${telemetry.batteryPercent}%",
              fontFamily = FontFamily.Monospace,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = if (telemetry.batteryPercent < 20) JarvisRed else JarvisCyan
            )
          }
          Text(
            text = if (isPrivacyMode) "SHIELDED" else "SECURE",
            fontFamily = FontFamily.Monospace,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            color = if (isPrivacyMode) JarvisAmber else JarvisGreen
          )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Voice TTS Toggle
        Box(
          modifier = Modifier
            .size(32.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isVoiceActive) Color(0x4000F0FF) else Color(0x26083344))
            .border(1.dp, if (isVoiceActive) JarvisCyan else Color(0x3300F0FF), RoundedCornerShape(8.dp))
            .clickable { onToggleVoice() },
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = if (isVoiceActive) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
            contentDescription = "Voice Synthesis Audio",
            tint = if (isVoiceActive) JarvisCyanBright else JarvisTextDim,
            modifier = Modifier.size(16.dp)
          )
        }
      }
    }
  }
}

