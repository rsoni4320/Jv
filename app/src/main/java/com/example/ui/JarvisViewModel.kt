package com.example.ui

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.AiBrain
import com.example.data.local.AutomationEntity
import com.example.data.local.ChatMessageEntity
import com.example.data.local.JarvisDatabase
import com.example.data.local.MemoryEntity
import com.example.data.local.NoteEntity
import com.example.data.local.ReminderEntity
import com.example.data.local.TaskEntity
import com.example.data.model.AIProviderType
import com.example.data.model.AvatarState
import com.example.data.model.DeviceTelemetry
import com.example.data.model.HudUiMode
import com.example.data.model.JarvisNavSection
import com.example.data.model.MemoryCategory
import com.example.data.model.PersonalityStyle
import com.example.data.model.PlanStep
import com.example.data.model.TaskPriority
import com.example.data.repository.JarvisRepository
import com.example.system.SoundFxManager
import com.example.system.TelemetryManager
import com.example.system.ToolExecutor
import com.example.system.VoiceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.json.JSONArray

class JarvisViewModel(application: Application) : AndroidViewModel(application) {

  private val repository: JarvisRepository
  private val telemetryManager = TelemetryManager(application)
  val voiceManager = VoiceManager(application, viewModelScope)
  val soundFxManager = SoundFxManager(viewModelScope)
  val toolExecutor = ToolExecutor(application)
  val aiBrain = AiBrain()

  // Navigation & UI Mode
  private val _currentSection = MutableStateFlow(JarvisNavSection.HOME)
  val currentSection: StateFlow<JarvisNavSection> = _currentSection.asStateFlow()

  private val _hudUiMode = MutableStateFlow(HudUiMode.HOLOGRAPHIC)
  val hudUiMode: StateFlow<HudUiMode> = _hudUiMode.asStateFlow()

  // Avatar state
  private val _avatarState = MutableStateFlow(AvatarState.IDLE)
  val avatarState: StateFlow<AvatarState> = _avatarState.asStateFlow()

  // Telemetry
  private val _telemetry = MutableStateFlow(telemetryManager.getLiveTelemetry())
  val telemetry: StateFlow<DeviceTelemetry> = _telemetry.asStateFlow()

  // Privacy & Settings
  private val _isPrivacyMode = MutableStateFlow(false)
  val isPrivacyMode: StateFlow<Boolean> = _isPrivacyMode.asStateFlow()

  private val _activePlanSteps = MutableStateFlow<List<PlanStep>>(emptyList())
  val activePlanSteps: StateFlow<List<PlanStep>> = _activePlanSteps.asStateFlow()

  private val _currentOperation = MutableStateFlow("Subsystems Nominal")
  val currentOperation: StateFlow<String> = _currentOperation.asStateFlow()

  private val _latestResponseText = MutableStateFlow("Ready for vocal or text instructions, Sir.")
  val latestResponseText: StateFlow<String> = _latestResponseText.asStateFlow()

  // Database Flows
  val memories: StateFlow<List<MemoryEntity>>
  val tasks: StateFlow<List<TaskEntity>>
  val reminders: StateFlow<List<ReminderEntity>>
  val notes: StateFlow<List<NoteEntity>>
  val automations: StateFlow<List<AutomationEntity>>
  val chatMessages: StateFlow<List<ChatMessageEntity>>

  private var telemetryTickerJob: Job? = null

  init {
    val db = JarvisDatabase.getDatabase(application)
    repository = JarvisRepository(db.jarvisDao())

    memories = repository.memories.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    tasks = repository.tasks.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    reminders = repository.reminders.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    notes = repository.notes.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    automations = repository.automations.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    chatMessages = repository.chatMessages.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    viewModelScope.launch {
      repository.seedDefaultsIfEmpty()
    }

    // Startup sound and telemetry polling
    soundFxManager.playStartup()
    startTelemetryTicker()
  }

  fun setNavSection(section: JarvisNavSection) {
    _currentSection.value = section
    soundFxManager.playClick()
  }

  fun setHudMode(mode: HudUiMode) {
    _hudUiMode.value = mode
  }

  fun togglePrivacyMode() {
    val newMode = !_isPrivacyMode.value
    _isPrivacyMode.value = newMode
    if (newMode) {
      voiceManager.stopSpeaking()
      voiceManager.stopListening()
      _avatarState.value = AvatarState.PRIVACY_MODE
      _currentOperation.value = "Privacy Shield Active: Mic & Vision Suspended"
      soundFxManager.playWarning()
    } else {
      _avatarState.value = AvatarState.IDLE
      _currentOperation.value = "Systems Active"
      soundFxManager.playSuccess()
    }
  }

  fun toggleVoiceSpeech() {
    voiceManager.isAutoSpeakEnabled = !voiceManager.isAutoSpeakEnabled
    if (!voiceManager.isAutoSpeakEnabled) {
      voiceManager.stopSpeaking()
    }
    soundFxManager.playClick()
  }

  fun startVoiceListening() {
    if (_isPrivacyMode.value) {
      _latestResponseText.value = "Privacy Mode is currently active. Please disable Privacy Shield before initiating voice recognition."
      return
    }

    _avatarState.value = AvatarState.LISTENING
    _currentOperation.value = "Listening to vocal input..."
    soundFxManager.playListening()

    voiceManager.startListening(
      onResult = { text ->
        processUserInput(text)
      },
      onError = { errMsg ->
        _avatarState.value = AvatarState.IDLE
        _currentOperation.value = errMsg
      }
    )
  }

  fun stopSpeaking() {
    voiceManager.stopSpeaking()
    voiceManager.stopListening()
    _avatarState.value = AvatarState.IDLE
    _currentOperation.value = "Awaiting command"
  }

  fun processUserInput(prompt: String, bitmap: Bitmap? = null) {
    if (prompt.isBlank() && bitmap == null) return

    val cleanPrompt = prompt.trim()

    viewModelScope.launch(Dispatchers.IO) {
      // 1. Record user message
      repository.insertChatMessage(
        sender = "USER",
        text = cleanPrompt.ifBlank { "[Image Optical Analysis Request]" }
      )
      repository.insertLog("Neural Interface", "INPUT", "Received instruction: $cleanPrompt")

      // 2. Check for voice / state control commands
      if (handleDirectControls(cleanPrompt)) {
        return@launch
      }

      // 3. Set avatar thinking state
      _avatarState.value = AvatarState.THINKING
      _currentOperation.value = "Synthesizing response..."

      val currentTelemetry = telemetryManager.getLiveTelemetry()
      val memoriesText = memories.value.joinToString("\n") { "[${it.category}] ${it.keyName}: ${it.content}" }

      val response = aiBrain.processQuery(
        userPrompt = cleanPrompt,
        telemetry = currentTelemetry,
        memoriesSummary = memoriesText,
        bitmap = bitmap,
        onPlanUpdate = { steps ->
          _activePlanSteps.value = steps
        }
      )

      // 4. Update state & UI
      _latestResponseText.value = response.replyText
      _activePlanSteps.value = response.planSteps
      _avatarState.value = AvatarState.SPEAKING
      _currentOperation.value = "Responding via ${response.providerUsed}"

      // 5. Store response message
      repository.insertChatMessage(
        sender = "JARVIS",
        text = response.replyText,
        toolExecutionJson = response.toolTriggered ?: ""
      )
      repository.insertLog(
        toolName = response.toolTriggered ?: response.providerUsed,
        status = if (response.isOffline) "OFFLINE_EXEC" else "SUCCESS",
        actionSummary = "Generated response for: $cleanPrompt"
      )

      // 6. Vocalize response
      soundFxManager.playSuccess()
      voiceManager.speak(response.replyText) {
        if (_avatarState.value == AvatarState.SPEAKING) {
          _avatarState.value = AvatarState.IDLE
          _currentOperation.value = "Systems nominal."
        }
      }
    }
  }

  private suspend fun handleDirectControls(prompt: String): Boolean {
    val lower = prompt.lowercase()

    // Wake / Sleep commands
    if (lower == "wake up" || lower == "hey jarvis" || lower == "resume") {
      _avatarState.value = AvatarState.IDLE
      _currentOperation.value = "JARVIS is awake and listening."
      voiceManager.speak("At your service, Sir.")
      return true
    }

    if (lower == "go to sleep" || lower == "standby" || lower == "sleep mode") {
      voiceManager.stopSpeaking()
      _avatarState.value = AvatarState.SLEEP
      _currentOperation.value = "Standby power mode."
      voiceManager.speak("Entering standby mode, Sir.")
      return true
    }

    if (lower == "privacy mode" || lower == "enable privacy") {
      if (!_isPrivacyMode.value) togglePrivacyMode()
      return true
    }

    // Specific intent triggers
    if (lower.contains("open browser") || lower.contains("open chrome")) {
      _avatarState.value = AvatarState.EXECUTING
      _currentOperation.value = "Launching Web Browser..."
      toolExecutor.executeTool("open_browser", "https://www.google.com")
      voiceManager.speak("Opening browser, Sir.")
      _avatarState.value = AvatarState.SUCCESS
      return true
    }

    if (lower.contains("open youtube")) {
      _avatarState.value = AvatarState.EXECUTING
      _currentOperation.value = "Launching YouTube..."
      toolExecutor.executeTool("open_youtube", "")
      voiceManager.speak("Opening YouTube, Sir.")
      _avatarState.value = AvatarState.SUCCESS
      return true
    }

    if (lower.contains("toggle flashlight") || lower.contains("flashlight") || lower.contains("torch")) {
      _avatarState.value = AvatarState.EXECUTING
      val report = toolExecutor.executeTool("toggle_flashlight", "")
      voiceManager.speak(report.result)
      _avatarState.value = AvatarState.SUCCESS
      return true
    }

    if (lower.contains("morning routine") || lower.contains("start morning routine")) {
      runMorningRoutine()
      return true
    }

    if (lower.contains("work mode") || lower.contains("activate work protocol")) {
      runWorkProtocol()
      return true
    }

    return false
  }

  fun runMorningRoutine() {
    viewModelScope.launch(Dispatchers.IO) {
      _avatarState.value = AvatarState.EXECUTING
      _currentOperation.value = "Executing Morning Briefing Protocol..."

      val steps = listOf(
        PlanStep(1, "TELEMETRY", "Reading battery & hardware status", isCompleted = false),
        PlanStep(2, "CALENDAR", "Checking current date and chronometer", isCompleted = false),
        PlanStep(3, "TASKS", "Filtering pending high-priority directives", isCompleted = false),
        PlanStep(4, "BRIEFING", "Synthesizing executive morning speech", isCompleted = false)
      )
      _activePlanSteps.value = steps
      delay(400)

      _activePlanSteps.value = steps.mapIndexed { idx, s -> if (idx == 0) s.copy(isCompleted = true) else s }
      delay(400)
      _activePlanSteps.value = steps.mapIndexed { idx, s -> if (idx <= 1) s.copy(isCompleted = true) else s }
      delay(400)
      _activePlanSteps.value = steps.mapIndexed { idx, s -> if (idx <= 2) s.copy(isCompleted = true) else s }
      delay(400)
      _activePlanSteps.value = steps.map { it.copy(isCompleted = true) }

      val t = telemetryManager.getLiveTelemetry()
      val pendingTasks = tasks.value.filter { !it.isCompleted }
      val briefing = "Good morning, Sir. Systems are online and operating at 100% nominal efficiency. Battery level is at ${t.batteryPercent}%, RAM load is ${t.usedRamPercent}%, and you have ${pendingTasks.size} active priority directives awaiting your attention today."

      _latestResponseText.value = briefing
      _avatarState.value = AvatarState.SPEAKING
      repository.insertChatMessage("JARVIS", briefing)
      repository.insertLog("Morning Routine", "SUCCESS", "Completed morning executive briefing.")
      soundFxManager.playSuccess()
      voiceManager.speak(briefing) {
        _avatarState.value = AvatarState.IDLE
      }
    }
  }

  fun runWorkProtocol() {
    viewModelScope.launch(Dispatchers.IO) {
      _avatarState.value = AvatarState.EXECUTING
      _currentOperation.value = "Activating Work Protocol..."

      val steps = listOf(
        PlanStep(1, "FOCUS", "Initializing engineering profile", isCompleted = true),
        PlanStep(2, "WORKSPACE", "Readying developer subroutines", isCompleted = true),
        PlanStep(3, "DIRECTIVES", "Sorting high-priority tasks", isCompleted = true)
      )
      _activePlanSteps.value = steps

      val speech = "Work Protocol engaged, Sir. Distraction filters are active, and all computational resources have been allocated to your priority workflow."
      _latestResponseText.value = speech
      _avatarState.value = AvatarState.SPEAKING
      repository.insertChatMessage("JARVIS", speech)
      repository.insertLog("Work Protocol", "SUCCESS", "Engaged focus and workspace protocol.")
      soundFxManager.playSuccess()
      voiceManager.speak(speech) {
        _avatarState.value = AvatarState.IDLE
      }
    }
  }

  fun runAutomation(automation: AutomationEntity) {
    viewModelScope.launch(Dispatchers.IO) {
      _avatarState.value = AvatarState.EXECUTING
      _currentOperation.value = "Running ${automation.name}..."

      val rawSteps = try {
        val jsonArray = JSONArray(automation.stepsJson)
        val list = mutableListOf<String>()
        for (i in 0 until jsonArray.length()) {
          list.add(jsonArray.getString(i))
        }
        list
      } catch (e: Exception) {
        listOf("Initiating routine", "Executing actions", "Finalizing protocol")
      }

      val planSteps = rawSteps.mapIndexed { idx, desc ->
        PlanStep(idx + 1, "STEP ${idx + 1}", desc, isCompleted = false)
      }
      _activePlanSteps.value = planSteps

      for (i in planSteps.indices) {
        delay(500)
        _activePlanSteps.value = _activePlanSteps.value.mapIndexed { idx, step ->
          if (idx <= i) step.copy(isCompleted = true) else step
        }
      }

      val resultText = "Routine '${automation.name}' executed successfully, Sir. All ${rawSteps.size} subroutines completed."
      _latestResponseText.value = resultText
      _avatarState.value = AvatarState.SUCCESS
      repository.insertChatMessage("JARVIS", resultText)
      repository.insertLog("Automation", "SUCCESS", "Executed ${automation.name}")
      soundFxManager.playSuccess()
      voiceManager.speak(resultText) {
        _avatarState.value = AvatarState.IDLE
      }
    }
  }

  // Memory Operations
  fun addMemory(category: MemoryCategory, key: String, content: String, tags: String = "") {
    viewModelScope.launch(Dispatchers.IO) {
      repository.insertMemory(category, key, content, tags)
      repository.insertLog("Memory System", "STORE", "Stored memory in $category: $key")
      soundFxManager.playSuccess()
    }
  }

  fun updateMemory(memory: MemoryEntity) {
    viewModelScope.launch(Dispatchers.IO) {
      repository.updateMemory(memory)
    }
  }

  fun deleteMemory(memory: MemoryEntity) {
    viewModelScope.launch(Dispatchers.IO) {
      repository.deleteMemory(memory)
      repository.insertLog("Memory System", "DELETE", "Purged memory: ${memory.keyName}")
    }
  }

  fun clearAllMemories() {
    viewModelScope.launch(Dispatchers.IO) {
      repository.clearMemories()
      repository.insertLog("Memory System", "PURGE", "Purged all memory records.")
    }
  }

  // Task Operations
  fun addTask(title: String, description: String = "", priority: TaskPriority = TaskPriority.MEDIUM, dueDate: String = "") {
    viewModelScope.launch(Dispatchers.IO) {
      repository.insertTask(title, description, priority, dueDate)
      repository.insertLog("Task Manager", "CREATE", "Created task: $title")
      soundFxManager.playSuccess()
    }
  }

  fun toggleTaskCompletion(task: TaskEntity) {
    viewModelScope.launch(Dispatchers.IO) {
      repository.updateTask(task.copy(isCompleted = !task.isCompleted))
      soundFxManager.playClick()
    }
  }

  fun deleteTask(task: TaskEntity) {
    viewModelScope.launch(Dispatchers.IO) {
      repository.deleteTask(task)
    }
  }

  // Reminder Operations
  fun addReminder(title: String, time: String, isRecurring: Boolean = false, interval: String = "Daily") {
    viewModelScope.launch(Dispatchers.IO) {
      repository.insertReminder(title, time, isRecurring, interval)
      repository.insertLog("Reminder Engine", "SCHEDULE", "Scheduled reminder: $title at $time")
      soundFxManager.playSuccess()
    }
  }

  fun deleteReminder(reminder: ReminderEntity) {
    viewModelScope.launch(Dispatchers.IO) {
      repository.deleteReminder(reminder)
    }
  }

  // Note Operations
  fun addNote(title: String, content: String, tag: String = "General") {
    viewModelScope.launch(Dispatchers.IO) {
      repository.insertNote(title, content, tag)
      repository.insertLog("Notes System", "CREATE", "Created note: $title")
      soundFxManager.playSuccess()
    }
  }

  fun deleteNote(note: NoteEntity) {
    viewModelScope.launch(Dispatchers.IO) {
      repository.deleteNote(note)
    }
  }

  // Clear Chat
  fun clearChat() {
    viewModelScope.launch(Dispatchers.IO) {
      repository.clearChat()
    }
  }

  private fun startTelemetryTicker() {
    telemetryTickerJob?.cancel()
    telemetryTickerJob = viewModelScope.launch(Dispatchers.Default) {
      while (isActive) {
        val updated = telemetryManager.getLiveTelemetry()
        _telemetry.value = updated
        delay(2000)
      }
    }
  }

  override fun onCleared() {
    super.onCleared()
    telemetryTickerJob?.cancel()
    voiceManager.shutdown()
    soundFxManager.release()
  }
}
