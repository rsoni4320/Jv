package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val JarvisDarkColorScheme = darkColorScheme(
  primary = JarvisCyan,
  onPrimary = JarvisBackgroundDark,
  primaryContainer = JarvisDeepBlue,
  onPrimaryContainer = JarvisCyanBright,
  secondary = JarvisElectricBlue,
  onSecondary = JarvisBackgroundDark,
  secondaryContainer = JarvisSurfaceElevated,
  onSecondaryContainer = JarvisTextPrimary,
  tertiary = JarvisGreen,
  onTertiary = JarvisBackgroundDark,
  background = JarvisBackground,
  onBackground = JarvisTextPrimary,
  surface = JarvisSurface,
  onSurface = JarvisTextPrimary,
  surfaceVariant = JarvisCardBg,
  onSurfaceVariant = JarvisTextSecondary,
  error = JarvisRed,
  onError = JarvisBackgroundDark
)

@Composable
fun JarvisTheme(
  content: @Composable () -> Unit
) {
  MaterialTheme(
    colorScheme = JarvisDarkColorScheme,
    typography = Typography,
    content = content
  )
}
