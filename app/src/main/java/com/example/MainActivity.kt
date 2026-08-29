package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Api
import androidx.compose.material.icons.filled.AutoMode
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.JarvisNavSection
import com.example.ui.JarvisViewModel
import com.example.ui.components.HolographicHudHeader
import com.example.ui.screens.AboutScreen
import com.example.ui.screens.ApiCenterScreen
import com.example.ui.screens.AutomationScreen
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.MemoryScreen
import com.example.ui.screens.ProductivityScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.TelemetryScreen
import com.example.ui.screens.ToolsScreen
import com.example.ui.screens.VisionScreen
import com.example.ui.theme.JarvisBackground
import com.example.ui.theme.JarvisBackgroundDark
import com.example.ui.theme.JarvisBorderGlow
import com.example.ui.theme.JarvisBorderSubtle
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisCyanBright
import com.example.ui.theme.JarvisGreen
import com.example.ui.theme.JarvisSurfaceElevated
import com.example.ui.theme.JarvisTextDim
import com.example.ui.theme.JarvisTextPrimary
import com.example.ui.theme.JarvisTextSecondary
import com.example.ui.theme.JarvisTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
  private val viewModel: JarvisViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    setContent {
      JarvisTheme {
        JarvisApp(viewModel = viewModel)
      }
    }
  }
}

@Composable
fun JarvisApp(viewModel: JarvisViewModel) {
  val context = LocalContext.current
  val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
  val scope = rememberCoroutineScope()

  val currentSection by viewModel.currentSection.collectAsStateWithLifecycle()
  val telemetry by viewModel.telemetry.collectAsStateWithLifecycle()
  val isPrivacyMode by viewModel.isPrivacyMode.collectAsStateWithLifecycle()

  // Permission Launcher for Audio & Camera
  val permissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestMultiplePermissions()
  ) { permissions ->
    // Handled
  }

  LaunchedEffect(Unit) {
    val needed = mutableListOf<String>()
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
      needed.add(Manifest.permission.RECORD_AUDIO)
    }
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
      needed.add(Manifest.permission.CAMERA)
    }
    if (needed.isNotEmpty()) {
      permissionLauncher.launch(needed.toTypedArray())
    }
  }

  ModalNavigationDrawer(
    drawerState = drawerState,
    drawerContent = {
      JarvisDrawerContent(
        selectedSection = currentSection,
        onSelectSection = { section ->
          viewModel.setNavSection(section)
          scope.launch { drawerState.close() }
        }
      )
    }
  ) {
    Scaffold(
      modifier = Modifier
        .fillMaxSize()
        .background(JarvisBackground),
      topBar = {
        HolographicHudHeader(
          telemetry = telemetry,
          activeModelName = viewModel.aiBrain.activeProvider.displayName,
          isPrivacyMode = isPrivacyMode,
          isVoiceActive = viewModel.voiceManager.isAutoSpeakEnabled,
          onMenuClick = { scope.launch { drawerState.open() } },
          onTogglePrivacy = { viewModel.togglePrivacyMode() },
          onToggleVoice = { viewModel.toggleVoiceSpeech() },
          modifier = Modifier.statusBarsPadding()
        )
      }
    ) { innerPadding ->
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding)
          .background(JarvisBackground)
      ) {
        when (currentSection) {
          JarvisNavSection.HOME -> DashboardScreen(viewModel = viewModel)
          JarvisNavSection.CHAT -> ChatScreen(viewModel = viewModel)
          JarvisNavSection.VISION -> VisionScreen(viewModel = viewModel)
          JarvisNavSection.AUTOMATION -> AutomationScreen(viewModel = viewModel)
          JarvisNavSection.MEMORY -> MemoryScreen(viewModel = viewModel)
          JarvisNavSection.TASKS -> ProductivityScreen(viewModel = viewModel)
          JarvisNavSection.TELEMETRY -> TelemetryScreen(viewModel = viewModel)
          JarvisNavSection.TOOLS -> ToolsScreen(viewModel = viewModel)
          JarvisNavSection.API_CENTER -> ApiCenterScreen(viewModel = viewModel)
          JarvisNavSection.SETTINGS -> SettingsScreen(viewModel = viewModel)
          JarvisNavSection.ABOUT -> AboutScreen(viewModel = viewModel)
        }
      }
    }
  }
}

@Composable
fun JarvisDrawerContent(
  selectedSection: JarvisNavSection,
  onSelectSection: (JarvisNavSection) -> Unit
) {
  ModalDrawerSheet(
    drawerContainerColor = Color(0xF2050B14),
    modifier = Modifier
      .width(300.dp)
      .fillMaxHeight()
      .border(1.dp, Color(0x3300F0FF), RoundedCornerShape(topEnd = 20.dp, bottomEnd = 20.dp))
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 14.dp, vertical = 20.dp)
        .verticalScroll(rememberScrollState())
    ) {
      // Top Drawer Header
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(Color(0x33083344))
            .border(1.dp, Color(0x8000F0FF), CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "J",
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = JarvisCyanBright
          )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column {
          Text(
            text = "J.A.R.V.I.S.",
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            letterSpacing = 1.2.sp,
            color = JarvisCyanBright
          )
          Text(
            text = "Quantum AI Command Hub",
            fontFamily = FontFamily.SansSerif,
            fontSize = 10.sp,
            color = JarvisCyan
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))
      HorizontalDivider(color = Color(0x2600F0FF))
      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = "SUBSYSTEM CHANNELS",
        fontFamily = FontFamily.Monospace,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        color = JarvisCyan
      )

      Spacer(modifier = Modifier.height(8.dp))

      JarvisNavSection.entries.forEach { section ->
        val isSelected = selectedSection == section
        val icon = getSectionIcon(section)

        NavigationDrawerItem(
          icon = {
            Icon(
              imageVector = icon,
              contentDescription = section.title,
              tint = if (isSelected) JarvisCyanBright else JarvisTextSecondary,
              modifier = Modifier.size(20.dp)
            )
          },
          label = {
            Text(
              text = section.title,
              fontFamily = FontFamily.Monospace,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
              fontSize = 12.sp,
              color = if (isSelected) JarvisCyanBright else JarvisTextSecondary
            )
          },
          selected = isSelected,
          onClick = { onSelectSection(section) },
          colors = NavigationDrawerItemDefaults.colors(
            selectedContainerColor = Color(0x40083344),
            unselectedContainerColor = Color.Transparent
          ),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .padding(vertical = 2.dp)
            .then(
              if (isSelected) Modifier.border(1.dp, Color(0x3300F0FF), RoundedCornerShape(12.dp))
              else Modifier
            )
        )
      }

      Spacer(modifier = Modifier.height(20.dp))
      Text(
        text = "STATUS: ALL SYSTEMS ONLINE",
        fontFamily = FontFamily.Monospace,
        fontSize = 9.sp,
        fontWeight = FontWeight.Bold,
        color = JarvisGreen
      )
    }
  }
}

fun getSectionIcon(section: JarvisNavSection): ImageVector {
  return when (section) {
    JarvisNavSection.HOME -> Icons.Default.Dashboard
    JarvisNavSection.CHAT -> Icons.AutoMirrored.Filled.Chat
    JarvisNavSection.VISION -> Icons.Default.CameraAlt
    JarvisNavSection.AUTOMATION -> Icons.Default.AutoMode
    JarvisNavSection.MEMORY -> Icons.Default.Psychology
    JarvisNavSection.TASKS -> Icons.Default.Checklist
    JarvisNavSection.TELEMETRY -> Icons.Default.MonitorHeart
    JarvisNavSection.TOOLS -> Icons.Default.Handyman
    JarvisNavSection.API_CENTER -> Icons.Default.Api
    JarvisNavSection.SETTINGS -> Icons.Default.Settings
    JarvisNavSection.ABOUT -> Icons.Default.Info
  }
}
