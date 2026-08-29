package com.example.data.model

enum class AvatarState(val label: String, val statusDescription: String) {
  IDLE("IDLE", "Systems nominal. Ready for instruction."),
  LISTENING("LISTENING", "Awaiting vocal command..."),
  THINKING("THINKING", "Processing neural parameters..."),
  SPEAKING("SPEAKING", "Synthesizing response..."),
  EXECUTING("EXECUTING", "Executing command sequence..."),
  SUCCESS("SUCCESS", "Operation completed successfully."),
  WARNING("WARNING", "System alert threshold exceeded."),
  ERROR("ERROR", "Subsystem fault encountered."),
  SLEEP("STANDBY", "Low-power standby mode."),
  PRIVACY_MODE("PRIVACY SHIELD", "Sensors and inputs suspended.")
}

enum class JarvisNavSection(val title: String, val iconName: String) {
  HOME("Command Center", "dashboard"),
  CHAT("Conversation", "chat"),
  VISION("Vision & OCR", "camera"),
  AUTOMATION("Automations", "auto_mode"),
  MEMORY("Memory Bank", "psychology"),
  TASKS("Tasks & Notes", "checklist"),
  TELEMETRY("Telemetry", "monitor_heart"),
  TOOLS("AI Tools", "handyman"),
  API_CENTER("API Center", "api"),
  SETTINGS("System Settings", "settings"),
  ABOUT("About JARVIS", "info")
}

enum class HudUiMode(val title: String, val description: String) {
  HOLOGRAPHIC("Holographic HUD", "Full futuristic Iron-Man inspired sci-fi HUD"),
  CLASSIC("Command Center", "Structured high-tech dashboard"),
  ORB("Reactor Core", "Dominant central animated AI energy core"),
  VISOR("Cockpit Visor", "High-density tactical telemetry overlay"),
  MINIMAL("Compact Stream", "Clean minimal interface")
}

enum class MemoryCategory(val displayName: String) {
  USER_PROFILE("User Profile"),
  PREFERENCES("Preferences"),
  CONVERSATION_MEMORY("Conversation Memory"),
  TASK_MEMORY("Task Memory"),
  PERSONAL_KNOWLEDGE("Personal Knowledge"),
  AUTOMATION_MEMORY("Automation Memory")
}

enum class TaskPriority(val label: String) {
  LOW("LOW"),
  MEDIUM("MED"),
  HIGH("HIGH"),
  CRITICAL("CRIT")
}

enum class AIProviderType(val displayName: String, val defaultModel: String, val defaultEndpoint: String) {
  GEMINI("Google Gemini", "gemini-3.5-flash", "https://generativelanguage.googleapis.com"),
  OPENAI("OpenAI", "gpt-4o", "https://api.openai.com/v1"),
  ANTHROPIC("Anthropic Claude", "claude-3-5-sonnet", "https://api.anthropic.com/v1"),
  GROQ("Groq LPU", "llama-3.3-70b-versatile", "https://api.groq.com/openai/v1"),
  OLLAMA("Ollama Local", "llama3.2", "http://10.0.2.2:11434/v1"),
  CUSTOM("Custom API", "custom-model", "https://api.custom.ai/v1")
}

enum class PersonalityStyle(val displayName: String, val promptDescription: String) {
  CALM_ELEGANT("Calm & Elegant (Default)", "Calm, polite, elegant, confident, slightly witty, occasionally addressing the user naturally as Sir."),
  CASUAL_WITTY("Casual & Witty", "Friendly, clever, playful, sharp humor, warm, highly proactive."),
  URGENT_DIRECT("Direct & Concise", "Ultra-fast, crisp, high-urgency, zero fluff, tactical brevity."),
  TECHNICAL_STRUCTURED("Technical & Structured", "Analytical, precise, engineering-focused with bulleted metrics and step-by-step breakdown.")
}

data class PlanStep(
  val stepNumber: Int,
  val title: String,
  val description: String,
  val isCompleted: Boolean = false,
  val isFailed: Boolean = false,
  val resultDetail: String? = null
)

data class ToolExecutionReport(
  val toolName: String,
  val status: String,
  val input: String,
  val result: String,
  val error: String? = null,
  val timestamp: Long = System.currentTimeMillis()
)

data class DeviceTelemetry(
  val batteryPercent: Int = 100,
  val isCharging: Boolean = false,
  val totalRamMb: Long = 8192,
  val freeRamMb: Long = 4096,
  val usedRamPercent: Int = 50,
  val networkType: String = "WiFi - 5GHz",
  val isOnline: Boolean = true,
  val osVersion: String = "Android 15",
  val deviceModel: String = "Stark Terminal Alpha",
  val cpuEstimatePercent: Int = 18,
  val totalStorageGb: Long = 128,
  val availableStorageGb: Long = 74,
  val temperatureC: Float = 21.4f,
  val latitude: Double = 40.7128,
  val longitude: Double = 74.0060,
  val currentTimeFormatted: String = "",
  val currentDateFormatted: String = ""
)
