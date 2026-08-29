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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.JarvisViewModel
import com.example.ui.components.HolographicCard
import com.example.ui.theme.JarvisBackground
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisCyanBright
import com.example.ui.theme.JarvisGreen
import com.example.ui.theme.JarvisTextPrimary
import com.example.ui.theme.JarvisTextSecondary

@Composable
fun AboutScreen(
  viewModel: JarvisViewModel,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxSize()
      .background(JarvisBackground)
      .padding(horizontal = 14.dp)
      .verticalScroll(rememberScrollState())
  ) {
    Spacer(modifier = Modifier.height(10.dp))

    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(
        imageVector = Icons.Default.Info,
        contentDescription = null,
        tint = JarvisCyan,
        modifier = Modifier.size(24.dp)
      )
      Spacer(modifier = Modifier.width(8.dp))
      Column {
        Text(
          text = "ABOUT J.A.R.V.I.S.",
          fontFamily = FontFamily.Monospace,
          fontWeight = FontWeight.Bold,
          fontSize = 14.sp,
          color = JarvisCyan
        )
        Text(
          text = "Just A Rather Very Intelligent System",
          fontFamily = FontFamily.SansSerif,
          fontSize = 11.sp,
          color = JarvisTextSecondary
        )
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    HolographicCard(modifier = Modifier.fillMaxWidth()) {
      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text("SYSTEM DESIGNATION", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = JarvisCyan)
        Text("J.A.R.V.I.S. Core Terminal Alpha", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = JarvisTextPrimary)
        Text("ARCHITECTURAL MATRIX", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = JarvisCyan)
        Text("Version 3.5.0 Quantum Edition", fontFamily = FontFamily.Monospace, fontSize = 12.sp, color = JarvisCyanBright)
        Text("NEURAL INTEGRATION", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = JarvisCyan)
        Text("Google Gemini (3.5 Flash / 3.1 Pro / Live Live-Preview) + Multi-Provider Engine", fontFamily = FontFamily.SansSerif, fontSize = 12.sp, color = JarvisTextPrimary)
        Text("LOCAL REPOSITORY", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = JarvisCyan)
        Text("Room SQLite Encrypted Local Storage", fontFamily = FontFamily.SansSerif, fontSize = 12.sp, color = JarvisTextPrimary)
        Text("OPERATIONAL INTEGRITY", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = JarvisCyan)
        Text("ALL MODULES NOMINAL • ONLINE", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = JarvisGreen)
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    HolographicCard(modifier = Modifier.fillMaxWidth()) {
      Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text("CORE SUBSYSTEMS", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = JarvisCyan)
        Text("• Voice Engine: TTS with acoustic modulation & SpeechRecognizer", fontFamily = FontFamily.SansSerif, fontSize = 11.sp, color = JarvisTextSecondary)
        Text("• Optical Array: Vision OCR & document reasoning engine", fontFamily = FontFamily.SansSerif, fontSize = 11.sp, color = JarvisTextSecondary)
        Text("• Telemetry Bus: Real-time Kernel, Battery, RAM, and Network feeds", fontFamily = FontFamily.SansSerif, fontSize = 11.sp, color = JarvisTextSecondary)
        Text("• Automation Layer: Multistep conditional routines & daily briefings", fontFamily = FontFamily.SansSerif, fontSize = 11.sp, color = JarvisTextSecondary)
        Text("• Memory Repositories: User Profile, Preferences, Knowledge Base", fontFamily = FontFamily.SansSerif, fontSize = 11.sp, color = JarvisTextSecondary)
        Text("• Privacy Shield: Hard-lock sensor killswitch & offline safe execution", fontFamily = FontFamily.SansSerif, fontSize = 11.sp, color = JarvisTextSecondary)
      }
    }

    Spacer(modifier = Modifier.height(20.dp))
  }
}
