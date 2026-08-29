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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.HudUiMode
import com.example.data.model.PersonalityStyle
import com.example.ui.JarvisViewModel
import com.example.ui.components.HolographicCard
import com.example.ui.theme.JarvisAmber
import com.example.ui.theme.JarvisBackground
import com.example.ui.theme.JarvisBorderSubtle
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisCyanBright
import com.example.ui.theme.JarvisGreen
import com.example.ui.theme.JarvisRed
import com.example.ui.theme.JarvisTextPrimary
import com.example.ui.theme.JarvisTextSecondary

@Composable
fun SettingsScreen(
  viewModel: JarvisViewModel,
  modifier: Modifier = Modifier
) {
  val isPrivacyMode by viewModel.isPrivacyMode.collectAsStateWithLifecycle()
  val hudMode by viewModel.hudUiMode.collectAsStateWithLifecycle()

  var userNameInput by remember { mutableStateOf(viewModel.aiBrain.userName) }
  var personality by remember { mutableStateOf(viewModel.aiBrain.personality) }
  var speechRate by remember { mutableFloatStateOf(viewModel.voiceManager.speechRate) }
  var pitchRate by remember { mutableFloatStateOf(viewModel.voiceManager.pitchRate) }
  var soundFxOn by remember { mutableStateOf(viewModel.soundFxManager.isSoundEnabled) }
  var autoSpeakOn by remember { mutableStateOf(viewModel.voiceManager.isAutoSpeakEnabled) }

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
        imageVector = Icons.Default.Settings,
        contentDescription = null,
        tint = JarvisCyan,
        modifier = Modifier.size(24.dp)
      )
      Spacer(modifier = Modifier.width(8.dp))
      Column {
        Text(
          text = "CORE SYSTEM SETTINGS",
          fontFamily = FontFamily.Monospace,
          fontWeight = FontWeight.Bold,
          fontSize = 14.sp,
          color = JarvisCyan
        )
        Text(
          text = "Personality, voice modulation, and HUD interface config",
          fontFamily = FontFamily.SansSerif,
          fontSize = 11.sp,
          color = JarvisTextSecondary
        )
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // User & Identity
    HolographicCard(modifier = Modifier.fillMaxWidth()) {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Person, contentDescription = null, tint = JarvisCyan, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("USER PROTOCOL & HONORIFIC", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = JarvisCyan)
        }

        OutlinedTextField(
          value = userNameInput,
          onValueChange = {
            userNameInput = it
            viewModel.aiBrain.userName = it
          },
          label = { Text("Designation / Title (e.g. Sir, Boss, Doctor)", color = JarvisCyan) },
          singleLine = true,
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = JarvisCyan,
            unfocusedBorderColor = JarvisBorderSubtle,
            focusedTextColor = JarvisTextPrimary,
            unfocusedTextColor = JarvisTextPrimary
          ),
          modifier = Modifier.fillMaxWidth()
        )
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Personality Style Selection
    HolographicCard(modifier = Modifier.fillMaxWidth()) {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
          text = "NEURAL PERSONALITY STYLE",
          fontFamily = FontFamily.Monospace,
          fontWeight = FontWeight.Bold,
          fontSize = 12.sp,
          color = JarvisCyan
        )

        PersonalityStyle.entries.forEach { style ->
          val isSelected = personality == style
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .background(if (isSelected) Color(0x3300F0FF) else Color.Transparent, RoundedCornerShape(6.dp))
              .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = style.displayName,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = if (isSelected) JarvisCyanBright else JarvisTextPrimary
              )
              Text(
                text = style.promptDescription,
                fontFamily = FontFamily.SansSerif,
                fontSize = 10.sp,
                color = JarvisTextSecondary
              )
            }

            Button(
              onClick = {
                personality = style
                viewModel.aiBrain.personality = style
              },
              colors = ButtonDefaults.buttonColors(
                containerColor = if (isSelected) JarvisCyan else Color(0x2200F0FF)
              ),
              shape = RoundedCornerShape(6.dp)
            ) {
              Text(
                text = if (isSelected) "ACTIVE" else "SET",
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                color = if (isSelected) JarvisBackground else JarvisCyan
              )
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Voice Synthesis & Modulation
    HolographicCard(modifier = Modifier.fillMaxWidth()) {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.VolumeUp, contentDescription = null, tint = JarvisCyan, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("VOICE SYNTHESIS & ACOUSTICS", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = JarvisCyan)
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("Automatic Vocal Responses", fontFamily = FontFamily.SansSerif, fontSize = 12.sp, color = JarvisTextPrimary)
          Switch(
            checked = autoSpeakOn,
            onCheckedChange = {
              autoSpeakOn = it
              viewModel.voiceManager.isAutoSpeakEnabled = it
            },
            colors = SwitchDefaults.colors(checkedThumbColor = JarvisCyan, checkedTrackColor = Color(0x3300F0FF))
          )
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("Sci-Fi Sound FX & Chimes", fontFamily = FontFamily.SansSerif, fontSize = 12.sp, color = JarvisTextPrimary)
          Switch(
            checked = soundFxOn,
            onCheckedChange = {
              soundFxOn = it
              viewModel.soundFxManager.isSoundEnabled = it
            },
            colors = SwitchDefaults.colors(checkedThumbColor = JarvisCyan, checkedTrackColor = Color(0x3300F0FF))
          )
        }

        Text("Pitch Modulation: ${String.format("%.2f", pitchRate)}x", fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = JarvisCyanBright)
        Slider(
          value = pitchRate,
          onValueChange = {
            pitchRate = it
            viewModel.voiceManager.pitchRate = it
          },
          valueRange = 0.8f..1.5f,
          colors = SliderDefaults.colors(thumbColor = JarvisCyan, activeTrackColor = JarvisCyan)
        )

        Text("Speech Velocity: ${String.format("%.2f", speechRate)}x", fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = JarvisCyanBright)
        Slider(
          value = speechRate,
          onValueChange = {
            speechRate = it
            viewModel.voiceManager.speechRate = it
          },
          valueRange = 0.7f..1.5f,
          colors = SliderDefaults.colors(thumbColor = JarvisCyan, activeTrackColor = JarvisCyan)
        )
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Privacy & Security
    HolographicCard(modifier = Modifier.fillMaxWidth()) {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Security, contentDescription = null, tint = JarvisAmber, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("PRIVACY SHIELD & SENSOR KILLSWITCH", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = JarvisAmber)
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("Microphone & Vision Isolation", fontFamily = FontFamily.SansSerif, fontSize = 12.sp, color = JarvisTextPrimary)
          Switch(
            checked = isPrivacyMode,
            onCheckedChange = { viewModel.togglePrivacyMode() },
            colors = SwitchDefaults.colors(checkedThumbColor = JarvisAmber, checkedTrackColor = Color(0x33FFB300))
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(20.dp))
  }
}
