package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.AvatarState
import com.example.data.model.JarvisNavSection
import com.example.ui.JarvisViewModel
import com.example.ui.components.HolographicAvatar
import com.example.ui.components.HolographicCard
import com.example.ui.components.PlanProgressCard
import com.example.ui.components.QuickCommandChips
import com.example.ui.components.WaveformVisualizer
import com.example.ui.theme.JarvisAmber
import com.example.ui.theme.JarvisBackground
import com.example.ui.theme.JarvisBorderGlow
import com.example.ui.theme.JarvisBorderSubtle
import com.example.ui.theme.JarvisCardBg
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisCyanBright
import com.example.ui.theme.JarvisCyanDim
import com.example.ui.theme.JarvisDeepBlue
import com.example.ui.theme.JarvisElectricBlue
import com.example.ui.theme.JarvisGreen
import com.example.ui.theme.JarvisRed
import com.example.ui.theme.JarvisSurfaceElevated
import com.example.ui.theme.JarvisTextDim
import com.example.ui.theme.JarvisTextPrimary
import com.example.ui.theme.JarvisTextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
  viewModel: JarvisViewModel,
  modifier: Modifier = Modifier
) {
  val avatarState by viewModel.avatarState.collectAsStateWithLifecycle()
  val telemetry by viewModel.telemetry.collectAsStateWithLifecycle()
  val currentOp by viewModel.currentOperation.collectAsStateWithLifecycle()
  val latestReply by viewModel.latestResponseText.collectAsStateWithLifecycle()
  val activePlan by viewModel.activePlanSteps.collectAsStateWithLifecycle()
  val isListening by viewModel.voiceManager.isListening.collectAsStateWithLifecycle()
  val isSpeaking by viewModel.voiceManager.isSpeaking.collectAsStateWithLifecycle()
  val audioAmplitude by viewModel.voiceManager.audioAmplitude.collectAsStateWithLifecycle()
  val recognizedText by viewModel.voiceManager.recognizedTranscript.collectAsStateWithLifecycle()

  var inputPrompt by remember { mutableStateOf("") }
  val currentTimeStr = remember { SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date()) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(JarvisBackground)
      .padding(horizontal = 14.dp)
      .verticalScroll(rememberScrollState()),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Spacer(modifier = Modifier.height(6.dp))

    // 1. HUD Atmospheric & Coordinate Brackets
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 4.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Atmospheric Left Bracket
      Column(
        modifier = Modifier
          .clip(RoundedCornerShape(8.dp))
          .background(Color(0x26083344))
          .border(1.dp, Color(0x2600F0FF), RoundedCornerShape(8.dp))
          .padding(horizontal = 8.dp, vertical = 4.dp)
      ) {
        Text(
          text = "ATMOSPHERIC",
          fontFamily = FontFamily.Monospace,
          fontSize = 8.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 1.sp,
          color = JarvisCyanDim
        )
        Text(
          text = "${telemetry.temperatureC}°C / NOMINAL",
          fontFamily = FontFamily.Monospace,
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
          color = JarvisCyanBright
        )
      }

      // Coordinate Right Bracket
      Column(
        horizontalAlignment = Alignment.End,
        modifier = Modifier
          .clip(RoundedCornerShape(8.dp))
          .background(Color(0x26083344))
          .border(1.dp, Color(0x2600F0FF), RoundedCornerShape(8.dp))
          .padding(horizontal = 8.dp, vertical = 4.dp)
      ) {
        Text(
          text = "COORDINATE",
          fontFamily = FontFamily.Monospace,
          fontSize = 8.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 1.sp,
          color = JarvisCyanDim
        )
        Text(
          text = "${telemetry.latitude}° N, ${telemetry.longitude}° W",
          fontFamily = FontFamily.Monospace,
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
          color = JarvisCyanBright
        )
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // 2. Central Frosted Arc Reactor Avatar Orb
    HolographicAvatar(
      state = avatarState,
      audioAmplitude = audioAmplitude,
      size = 200.dp,
      onClick = {
        if (isListening || isSpeaking) {
          viewModel.stopSpeaking()
        } else {
          viewModel.startVoiceListening()
        }
      },
      modifier = Modifier.testTag("central_avatar_orb")
    )

    Spacer(modifier = Modifier.height(10.dp))

    // 3. Waveform Visualizer
    WaveformVisualizer(
      isActive = isListening || isSpeaking,
      amplitude = if (audioAmplitude > 0f) audioAmplitude else 0.15f,
      modifier = Modifier.padding(horizontal = 16.dp)
    )

    Spacer(modifier = Modifier.height(12.dp))

    // 4. Frosted Glass Response Card
    val displayResponseText = when {
      isListening && recognizedText.isNotBlank() -> "\"$recognizedText\""
      latestReply.isNotBlank() -> "\"$latestReply\""
      else -> "\"Standing by, Commander. All neural systems calibrated and ready for your directive.\""
    }

    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(20.dp))
        .background(
          brush = Brush.verticalGradient(
            colors = listOf(
              Color(0x33083344), // cyan-950/20
              Color(0x1F051B24)
            )
          ),
          shape = RoundedCornerShape(20.dp)
        )
        .border(
          width = 1.dp,
          color = Color(0x3300F0FF), // cyan-500/20
          shape = RoundedCornerShape(20.dp)
        )
        .padding(16.dp)
    ) {
      Column {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = if (isListening) "LIVE VOICE STREAM" else "PRIMARY NEURAL FEED",
            fontFamily = FontFamily.Monospace,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.2.sp,
            color = JarvisCyan
          )
          Text(
            text = if (currentOp.isNotBlank()) currentOp else "CHANNEL ACTIVE",
            fontFamily = FontFamily.Monospace,
            fontSize = 9.sp,
            color = JarvisCyanDim
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Italic Quote styled Response Text
        Text(
          text = displayResponseText,
          fontFamily = FontFamily.SansSerif,
          fontStyle = FontStyle.Italic,
          fontSize = 14.sp,
          lineHeight = 21.sp,
          fontWeight = FontWeight.Normal,
          color = JarvisTextPrimary
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Horizontal Frosted Cyan Accent Divider Line
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(
              Brush.horizontalGradient(
                colors = listOf(
                  Color.Transparent,
                  Color(0x8000F0FF),
                  Color.Transparent
                )
              )
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "RESPONSE LOG 04.2",
            fontFamily = FontFamily.Monospace,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.2.sp,
            color = JarvisCyanDim
          )
          Text(
            text = currentTimeStr,
            fontFamily = FontFamily.Monospace,
            fontSize = 9.sp,
            color = JarvisCyanBright
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // 5. Multi-Step Execution Plan (if active)
    if (activePlan.isNotEmpty()) {
      PlanProgressCard(planSteps = activePlan)
      Spacer(modifier = Modifier.height(10.dp))
    }

    // 6. Quick Telemetry Row (Battery, RAM, Network)
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      // Battery Card
      HolographicCard(
        modifier = Modifier
          .weight(1f)
          .clickable { viewModel.setNavSection(JarvisNavSection.TELEMETRY) }
      ) {
        Column {
          Text("BATTERY", fontFamily = FontFamily.Monospace, fontSize = 9.sp, color = JarvisCyanDim)
          Text("${telemetry.batteryPercent}%", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = JarvisTextPrimary)
          Text(if (telemetry.isCharging) "CHARGING" else "NOMINAL", fontFamily = FontFamily.Monospace, fontSize = 8.sp, color = JarvisGreen)
        }
      }

      // RAM Card
      HolographicCard(
        modifier = Modifier
          .weight(1f)
          .clickable { viewModel.setNavSection(JarvisNavSection.TELEMETRY) }
      ) {
        Column {
          Text("RAM LOAD", fontFamily = FontFamily.Monospace, fontSize = 9.sp, color = JarvisCyanDim)
          Text("${telemetry.usedRamPercent}%", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = JarvisTextPrimary)
          Text("${telemetry.freeRamMb}MB FREE", fontFamily = FontFamily.Monospace, fontSize = 8.sp, color = JarvisCyanBright)
        }
      }

      // Network Card
      HolographicCard(
        modifier = Modifier
          .weight(1f)
          .clickable { viewModel.setNavSection(JarvisNavSection.TELEMETRY) }
      ) {
        Column {
          Text("UPLINK", fontFamily = FontFamily.Monospace, fontSize = 9.sp, color = JarvisCyanDim)
          Text(if (telemetry.isOnline) "LINKED" else "OFFLINE", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = if (telemetry.isOnline) JarvisGreen else JarvisAmber)
          Text(telemetry.networkType.take(10), fontFamily = FontFamily.Monospace, fontSize = 8.sp, color = JarvisTextSecondary)
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // 7. Frosted Quick Command Chips
    QuickCommandChips(
      onCommandSelected = { cmd ->
        viewModel.processUserInput(cmd)
      }
    )

    Spacer(modifier = Modifier.height(14.dp))

    // 8. Frosted Glass Push-To-Talk Control Bar
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      // Left Frosted Action: Optical Vision / Camera
      IconButton(
        onClick = { viewModel.setNavSection(JarvisNavSection.VISION) },
        modifier = Modifier
          .size(48.dp)
          .clip(RoundedCornerShape(14.dp))
          .background(Color(0x33083344))
          .border(1.dp, Color(0x3300F0FF), RoundedCornerShape(14.dp))
      ) {
        Icon(
          imageVector = Icons.Default.CameraAlt,
          contentDescription = "Vision Optical Sensor",
          tint = JarvisCyan,
          modifier = Modifier.size(22.dp)
        )
      }

      // Center: Prominent Glowing Gradient Push-to-Talk Pill
      Button(
        onClick = {
          if (isListening || isSpeaking) {
            viewModel.stopSpeaking()
          } else {
            viewModel.startVoiceListening()
          }
        },
        shape = RoundedCornerShape(28.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = Color.Transparent
        ),
        modifier = Modifier
          .weight(1f)
          .height(48.dp)
          .clip(RoundedCornerShape(28.dp))
          .background(
            brush = Brush.horizontalGradient(
              colors = if (isListening || isSpeaking) {
                listOf(Color(0xFFDC2626), Color(0xFF991B1B))
              } else {
                listOf(Color(0xFF0891B2), Color(0xFF1D4ED8))
              }
            ),
            shape = RoundedCornerShape(28.dp)
          )
          .border(
            width = 1.dp,
            color = if (isListening || isSpeaking) JarvisRed else Color(0x8000F0FF),
            shape = RoundedCornerShape(28.dp)
          )
          .testTag("mic_button")
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center
        ) {
          Icon(
            imageVector = if (isListening || isSpeaking) Icons.Default.Stop else Icons.Default.Mic,
            contentDescription = "Voice Action",
            tint = Color.White,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = if (isListening) "STOP LISTENING" else if (isSpeaking) "INTERRUPT VOICE" else "PUSH TO TALK",
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            letterSpacing = 1.sp,
            color = Color.White
          )
        }
      }

      // Right Frosted Action: Tools / Direct Prompt
      IconButton(
        onClick = { viewModel.setNavSection(JarvisNavSection.TOOLS) },
        modifier = Modifier
          .size(48.dp)
          .clip(RoundedCornerShape(14.dp))
          .background(Color(0x33083344))
          .border(1.dp, Color(0x3300F0FF), RoundedCornerShape(14.dp))
      ) {
        Icon(
          imageVector = Icons.Default.Tune,
          contentDescription = "System Tools and Parameters",
          tint = JarvisCyan,
          modifier = Modifier.size(22.dp)
        )
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // 9. Frosted Text Direct Command Input Bar
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(14.dp))
        .background(Color(0x2E083344), RoundedCornerShape(14.dp))
        .border(1.dp, Color(0x3300F0FF), RoundedCornerShape(14.dp))
        .padding(horizontal = 8.dp, vertical = 2.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      OutlinedTextField(
        value = inputPrompt,
        onValueChange = { inputPrompt = it },
        placeholder = {
          Text(
            text = "Command J.A.R.V.I.S....",
            fontFamily = FontFamily.SansSerif,
            fontSize = 13.sp,
            color = JarvisCyanDim
          )
        },
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = Color.Transparent,
          unfocusedBorderColor = Color.Transparent,
          cursorColor = JarvisCyan,
          focusedTextColor = JarvisTextPrimary,
          unfocusedTextColor = JarvisTextPrimary
        ),
        modifier = Modifier
          .weight(1f)
          .testTag("command_text_input")
      )

      IconButton(
        onClick = {
          if (inputPrompt.isNotBlank()) {
            viewModel.processUserInput(inputPrompt)
            inputPrompt = ""
          }
        },
        modifier = Modifier
          .size(36.dp)
          .clip(CircleShape)
          .background(Color(0x3300F0FF))
          .testTag("send_button")
      ) {
        Icon(
          imageVector = Icons.Default.Send,
          contentDescription = "Transmit Command",
          tint = JarvisCyanBright,
          modifier = Modifier.size(16.dp)
        )
      }
    }

    Spacer(modifier = Modifier.height(24.dp))
  }
}

