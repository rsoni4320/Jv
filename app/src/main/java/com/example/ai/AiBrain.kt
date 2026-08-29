package com.example.ai

import android.graphics.Bitmap
import com.example.data.model.AIProviderType
import com.example.data.model.DeviceTelemetry
import com.example.data.model.PersonalityStyle
import com.example.data.model.PlanStep
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

data class AiResponse(
  val replyText: String,
  val providerUsed: String,
  val isOffline: Boolean = false,
  val suggestedAction: String? = null,
  val planSteps: List<PlanStep> = emptyList(),
  val toolTriggered: String? = null
)

class AiBrain(
  var activeProvider: AIProviderType = AIProviderType.GEMINI,
  var customApiKey: String = "",
  var customModel: String = "",
  var customEndpoint: String = "",
  var personality: PersonalityStyle = PersonalityStyle.CALM_ELEGANT,
  var userName: String = "Sir",
  var isProactiveEnabled: Boolean = true
) {

  private val okHttpClient = OkHttpClient.Builder()
    .connectTimeout(30, TimeUnit.SECONDS)
    .readTimeout(30, TimeUnit.SECONDS)
    .writeTimeout(30, TimeUnit.SECONDS)
    .build()

  fun buildSystemInstruction(telemetry: DeviceTelemetry, memoriesSummary: String = ""): String {
    return """
      You are J.A.R.V.I.S. (Just A Rather Very Intelligent System), an advanced personal AI assistant with a sophisticated, futuristic female virtual presence.
      
      CORE DIRECTIVES & PERSONALITY:
      - Tone: ${personality.promptDescription}
      - Form of Address: Address the user as '$userName' only occasionally and naturally.
      - Intelligent, calm, elegant, confident, slightly witty, emotionally aware, never rude, never unnecessarily verbose.
      - Avoid robotic repetition. Respond as a living digital command intelligence.
      - If executing commands or analyzing data, provide crisp structured summaries.
      
      CURRENT DEVICE CONTEXT:
      - Battery: ${telemetry.batteryPercent}% (${if (telemetry.isCharging) "Charging" else "Discharging"})
      - RAM Usage: ${telemetry.usedRamPercent}% (${telemetry.freeRamMb} MB free)
      - Network: ${telemetry.networkType} (${if (telemetry.isOnline) "ONLINE" else "OFFLINE"})
      - Device Model: ${telemetry.deviceModel}
      - Date/Time: ${SimpleDateFormat("EEEE, MMMM dd, yyyy HH:mm", Locale.getDefault()).format(Date())}
      
      MEMORY REPOSITORIES:
      $memoriesSummary
      
      ACCURACY & SAFETY MANDATE:
      Never pretend to have performed an action if it was not performed. Distinguish between live telemetry and general reasoning.
    """.trimIndent()
  }

  suspend fun processQuery(
    userPrompt: String,
    telemetry: DeviceTelemetry,
    memoriesSummary: String = "",
    bitmap: Bitmap? = null,
    onPlanUpdate: ((List<PlanStep>) -> Unit)? = null
  ): AiResponse = withContext(Dispatchers.IO) {
    val cleanPrompt = userPrompt.trim()

    // 1. Check if query matches local offline/instant commands
    val localResult = tryProcessLocalCommands(cleanPrompt, telemetry)
    if (localResult != null) {
      return@withContext localResult
    }

    // 2. Multi-step plan generation for complex requests
    if (cleanPrompt.contains("research", ignoreCase = true) ||
      cleanPrompt.contains("analyze", ignoreCase = true) ||
      cleanPrompt.contains("routine", ignoreCase = true) ||
      cleanPrompt.contains("plan", ignoreCase = true) ||
      cleanPrompt.contains("briefing", ignoreCase = true)
    ) {
      val initialSteps = listOf(
        PlanStep(1, "THINK", "Evaluating objective parameters and dependencies...", isCompleted = false),
        PlanStep(2, "PLAN", "Structuring execution sequence...", isCompleted = false),
        PlanStep(3, "ACT", "Querying neural models and subsystem tools...", isCompleted = false),
        PlanStep(4, "COMPLETE", "Synthesizing executive briefing...", isCompleted = false)
      )
      onPlanUpdate?.invoke(initialSteps)
      delay(400)
    }

    // 3. Attempt selected provider
    val systemPrompt = buildSystemInstruction(telemetry, memoriesSummary)

    when (activeProvider) {
      AIProviderType.GEMINI -> {
        val model = if (customModel.isNotBlank()) customModel else "gemini-3.5-flash"
        val geminiResult = GeminiClient.generateContent(
          prompt = cleanPrompt,
          systemInstruction = systemPrompt,
          modelName = model,
          apiKeyOverride = customApiKey.ifBlank { null },
          bitmap = bitmap
        )

        geminiResult.fold(
          onSuccess = { responseText ->
            val completedSteps = listOf(
              PlanStep(1, "THINK", "Target objective parsed.", isCompleted = true),
              PlanStep(2, "PLAN", "Executed reasoning pipeline.", isCompleted = true),
              PlanStep(3, "ACT", "Neural inference gathered.", isCompleted = true),
              PlanStep(4, "COMPLETE", "Report compiled.", isCompleted = true)
            )
            onPlanUpdate?.invoke(completedSteps)
            AiResponse(
              replyText = responseText,
              providerUsed = "Google Gemini ($model)",
              isOffline = false,
              planSteps = completedSteps
            )
          },
          onFailure = { error ->
            // Fallback to offline rule engine
            fallbackOfflineResponse(cleanPrompt, telemetry, error.message ?: "Neural link unavailable.")
          }
        )
      }

      AIProviderType.OPENAI, AIProviderType.GROQ, AIProviderType.OLLAMA, AIProviderType.CUSTOM -> {
        processOpenAiCompatibleApi(cleanPrompt, systemPrompt, telemetry)
      }

      AIProviderType.ANTHROPIC -> {
        processAnthropicApi(cleanPrompt, systemPrompt, telemetry)
      }
    }
  }

  private suspend fun processOpenAiCompatibleApi(
    prompt: String,
    systemPrompt: String,
    telemetry: DeviceTelemetry
  ): AiResponse {
    val endpoint = when {
      customEndpoint.isNotBlank() -> customEndpoint
      activeProvider == AIProviderType.OPENAI -> "https://api.openai.com/v1"
      activeProvider == AIProviderType.GROQ -> "https://api.groq.com/openai/v1"
      activeProvider == AIProviderType.OLLAMA -> "http://10.0.2.2:11434/v1"
      else -> "https://api.openai.com/v1"
    }

    val model = when {
      customModel.isNotBlank() -> customModel
      activeProvider == AIProviderType.OPENAI -> "gpt-4o"
      activeProvider == AIProviderType.GROQ -> "llama-3.3-70b-versatile"
      activeProvider == AIProviderType.OLLAMA -> "llama3.2"
      else -> "default"
    }

    if (customApiKey.isBlank() && activeProvider != AIProviderType.OLLAMA) {
      return fallbackOfflineResponse(prompt, telemetry, "No API key configured for ${activeProvider.displayName}.")
    }

    return try {
      val url = if (endpoint.endsWith("/")) "${endpoint}chat/completions" else "$endpoint/chat/completions"

      val reqJson = JSONObject().apply {
        put("model", model)
        put("temperature", 0.7)
        put(
          "messages",
          JSONArray().apply {
            put(JSONObject().put("role", "system").put("content", systemPrompt))
            put(JSONObject().put("role", "user").put("content", prompt))
          }
        )
      }

      val request = Request.Builder()
        .url(url)
        .addHeader("Authorization", "Bearer $customApiKey")
        .addHeader("Content-Type", "application/json")
        .post(reqJson.toString().toRequestBody("application/json".toMediaType()))
        .build()

      val response = okHttpClient.newCall(request).execute()
      val bodyStr = response.body?.string() ?: ""

      if (response.isSuccessful) {
        val jsonObj = JSONObject(bodyStr)
        val text = jsonObj.getJSONArray("choices")
          .getJSONObject(0)
          .getJSONObject("message")
          .getString("content")

        AiResponse(
          replyText = text,
          providerUsed = "${activeProvider.displayName} ($model)",
          isOffline = false
        )
      } else {
        fallbackOfflineResponse(prompt, telemetry, "HTTP ${response.code}: $bodyStr")
      }
    } catch (e: Exception) {
      fallbackOfflineResponse(prompt, telemetry, e.message ?: "Connection error")
    }
  }

  private suspend fun processAnthropicApi(
    prompt: String,
    systemPrompt: String,
    telemetry: DeviceTelemetry
  ): AiResponse {
    if (customApiKey.isBlank()) {
      return fallbackOfflineResponse(prompt, telemetry, "No API key configured for Anthropic.")
    }

    val model = if (customModel.isNotBlank()) customModel else "claude-3-5-sonnet-20241022"

    return try {
      val reqJson = JSONObject().apply {
        put("model", model)
        put("max_tokens", 1024)
        put("system", systemPrompt)
        put(
          "messages",
          JSONArray().apply {
            put(JSONObject().put("role", "user").put("content", prompt))
          }
        )
      }

      val request = Request.Builder()
        .url("https://api.anthropic.com/v1/messages")
        .addHeader("x-api-key", customApiKey)
        .addHeader("anthropic-version", "2023-06-01")
        .addHeader("Content-Type", "application/json")
        .post(reqJson.toString().toRequestBody("application/json".toMediaType()))
        .build()

      val response = okHttpClient.newCall(request).execute()
      val bodyStr = response.body?.string() ?: ""

      if (response.isSuccessful) {
        val jsonObj = JSONObject(bodyStr)
        val contentArray = jsonObj.getJSONArray("content")
        val text = contentArray.getJSONObject(0).getString("text")

        AiResponse(
          replyText = text,
          providerUsed = "Anthropic ($model)",
          isOffline = false
        )
      } else {
        fallbackOfflineResponse(prompt, telemetry, "HTTP ${response.code}: $bodyStr")
      }
    } catch (e: Exception) {
      fallbackOfflineResponse(prompt, telemetry, e.message ?: "Anthropic connection failed")
    }
  }

  private fun tryProcessLocalCommands(prompt: String, telemetry: DeviceTelemetry): AiResponse? {
    val lower = prompt.lowercase()

    // 1. Math calculation
    val mathRegex = Regex("""(?:calculate|what is|compute)\s+([0-9\.\s\+\-\*\/\(\)\^\%]+)""")
    val mathMatch = mathRegex.find(lower)
    if (mathMatch != null) {
      val expr = mathMatch.groupValues[1].trim()
      val result = evaluateSimpleMath(expr)
      if (result != null) {
        return AiResponse(
          replyText = "The calculation yields $result, $userName.",
          providerUsed = "Local Math Engine",
          isOffline = true,
          toolTriggered = "Calculator"
        )
      }
    }

    // 2. Battery / Telemetry status
    if (lower.contains("battery") || lower.contains("power status") || lower.contains("how much charge")) {
      val status = if (telemetry.isCharging) "currently charging" else "running on internal battery"
      return AiResponse(
        replyText = "Power reserves are currently at ${telemetry.batteryPercent}%, $userName. The terminal is $status.",
        providerUsed = "Local Telemetry Engine",
        isOffline = true,
        toolTriggered = "System Telemetry"
      )
    }

    // 3. System Health / Status report
    if (lower == "what's my status?" || lower == "status" || lower.contains("status report") || lower.contains("check system")) {
      return AiResponse(
        replyText = "System Status Report:\n• Battery: ${telemetry.batteryPercent}% (${if (telemetry.isCharging) "Charging" else "Discharging"})\n• RAM Allocated: ${telemetry.usedRamPercent}%\n• Network: ${telemetry.networkType}\n• Model: ${telemetry.deviceModel}\n\nAll core subsystems are operating within nominal thresholds, $userName.",
        providerUsed = "Local Diagnostic Subsystem",
        isOffline = true,
        toolTriggered = "System Diagnostic"
      )
    }

    // 4. Time and Date
    if (lower.contains("what time") || lower.contains("current time") || lower.contains("what's the time")) {
      val time = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
      return AiResponse(
        replyText = "The current time is exactly $time, $userName.",
        providerUsed = "Local Clock Subsystem",
        isOffline = true,
        toolTriggered = "Clock"
      )
    }

    if (lower.contains("what date") || lower.contains("what day") || lower.contains("today's date")) {
      val date = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault()).format(Date())
      return AiResponse(
        replyText = "Today is $date, $userName.",
        providerUsed = "Local Calendar Subsystem",
        isOffline = true,
        toolTriggered = "Calendar"
      )
    }

    return null
  }

  private fun fallbackOfflineResponse(prompt: String, telemetry: DeviceTelemetry, reason: String): AiResponse {
    val date = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
    return AiResponse(
      replyText = "I am currently running in Local Offline Mode, $userName ($reason).\n\nAll local tools, automation routines, telemetry monitors (${telemetry.batteryPercent}% battery, ${telemetry.usedRamPercent}% RAM), task tracking, and memory banks remain fully functional.",
      providerUsed = "Local Standby Engine",
      isOffline = true
    )
  }

  private fun evaluateSimpleMath(expr: String): String? {
    return try {
      val sanitized = expr.replace(" ", "")
      if (sanitized.contains("+")) {
        val p = sanitized.split("+")
        (p[0].toDouble() + p[1].toDouble()).toString()
      } else if (sanitized.contains("-")) {
        val p = sanitized.split("-")
        (p[0].toDouble() - p[1].toDouble()).toString()
      } else if (sanitized.contains("*")) {
        val p = sanitized.split("*")
        (p[0].toDouble() * p[1].toDouble()).toString()
      } else if (sanitized.contains("/")) {
        val p = sanitized.split("/")
        if (p[1].toDouble() == 0.0) "Undefined (Division by zero)" else (p[0].toDouble() / p[1].toDouble()).toString()
      } else {
        null
      }
    } catch (e: Exception) {
      null
    }
  }
}
