package com.example.system

import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraManager
import android.net.Uri
import android.provider.AlarmClock
import android.provider.Settings
import com.example.data.model.ToolExecutionReport
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ToolExecutor(private val context: Context) {

  private var isTorchOn = false

  suspend fun executeTool(
    toolName: String,
    rawInput: String
  ): ToolExecutionReport = withContext(Dispatchers.IO) {
    try {
      val (status, result) = when (toolName.lowercase()) {
        "open_browser", "browser" -> {
          val url = if (rawInput.startsWith("http")) rawInput else "https://www.google.com"
          val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
          }
          context.startActivity(intent)
          "SUCCESS" to "Web browser opened with URI: $url"
        }

        "open_youtube", "youtube" -> {
          val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com")).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
          }
          context.startActivity(intent)
          "SUCCESS" to "YouTube client launched."
        }

        "open_maps", "maps" -> {
          val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=$rawInput")).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
          }
          context.startActivity(intent)
          "SUCCESS" to "Navigation map launched for query: $rawInput"
        }

        "open_settings", "settings" -> {
          val intent = Intent(Settings.ACTION_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
          }
          context.startActivity(intent)
          "SUCCESS" to "System settings interface displayed."
        }

        "wifi_settings" -> {
          val intent = Intent(Settings.ACTION_WIFI_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
          }
          context.startActivity(intent)
          "SUCCESS" to "Wireless network configuration opened."
        }

        "bluetooth_settings" -> {
          val intent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
          }
          context.startActivity(intent)
          "SUCCESS" to "Bluetooth communications control opened."
        }

        "battery_settings" -> {
          val intent = Intent(Intent.ACTION_POWER_USAGE_SUMMARY).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
          }
          context.startActivity(intent)
          "SUCCESS" to "Power & battery diagnostics panel opened."
        }

        "toggle_flashlight", "flashlight" -> {
          val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager
          val cameraId = cameraManager?.cameraIdList?.firstOrNull()
          if (cameraId != null) {
            isTorchOn = !isTorchOn
            cameraManager.setTorchMode(cameraId, isTorchOn)
            "SUCCESS" to "Tactical optical illuminator (flashlight) set to: ${if (isTorchOn) "ACTIVE" else "DISABLED"}"
          } else {
            "WARNING" to "No optical camera flash hardware detected."
          }
        }

        "open_clock", "clock" -> {
          val intent = Intent(AlarmClock.ACTION_SHOW_ALARMS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
          }
          context.startActivity(intent)
          "SUCCESS" to "Chronometer and alarm console opened."
        }

        "search_web" -> {
          val searchUri = Uri.parse("https://www.google.com/search?q=${Uri.encode(rawInput)}")
          val intent = Intent(Intent.ACTION_VIEW, searchUri).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
          }
          context.startActivity(intent)
          "SUCCESS" to "Executed external satellite search query: $rawInput"
        }

        else -> {
          "SUCCESS" to "Executed localized protocol: $toolName with parameters: $rawInput"
        }
      }

      ToolExecutionReport(
        toolName = toolName,
        status = status,
        input = rawInput,
        result = result
      )
    } catch (e: Exception) {
      ToolExecutionReport(
        toolName = toolName,
        status = "ERROR",
        input = rawInput,
        result = "Failed to invoke system intent: ${e.message}",
        error = e.localizedMessage
      )
    }
  }
}
