package com.example.data.repository

import com.example.data.local.ActivityLogEntity
import com.example.data.local.AutomationEntity
import com.example.data.local.ChatMessageEntity
import com.example.data.local.CustomCommandEntity
import com.example.data.local.JarvisDao
import com.example.data.local.MemoryEntity
import com.example.data.local.NoteEntity
import com.example.data.local.ReminderEntity
import com.example.data.local.TaskEntity
import com.example.data.model.MemoryCategory
import com.example.data.model.TaskPriority
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class JarvisRepository(private val dao: JarvisDao) {

  val memories: Flow<List<MemoryEntity>> = dao.getAllMemories()
  val tasks: Flow<List<TaskEntity>> = dao.getAllTasks()
  val reminders: Flow<List<ReminderEntity>> = dao.getAllReminders()
  val notes: Flow<List<NoteEntity>> = dao.getAllNotes()
  val automations: Flow<List<AutomationEntity>> = dao.getAllAutomations()
  val recentLogs: Flow<List<ActivityLogEntity>> = dao.getRecentLogs()
  val chatMessages: Flow<List<ChatMessageEntity>> = dao.getAllChatMessages()
  val customCommands: Flow<List<CustomCommandEntity>> = dao.getAllCustomCommands()

  suspend fun insertMemory(category: MemoryCategory, key: String, content: String, tags: String = "") {
    dao.insertMemory(
      MemoryEntity(
        category = category.name,
        keyName = key,
        content = content,
        tags = tags
      )
    )
  }

  suspend fun updateMemory(memory: MemoryEntity) = dao.updateMemory(memory)
  suspend fun deleteMemory(memory: MemoryEntity) = dao.deleteMemory(memory)
  suspend fun clearMemories() = dao.clearAllMemories()

  suspend fun insertTask(title: String, description: String = "", priority: TaskPriority = TaskPriority.MEDIUM, dueDate: String = ""): Long {
    return dao.insertTask(
      TaskEntity(
        title = title,
        description = description,
        priority = priority.name,
        dueDate = dueDate
      )
    )
  }

  suspend fun updateTask(task: TaskEntity) = dao.updateTask(task)
  suspend fun deleteTask(task: TaskEntity) = dao.deleteTask(task)

  suspend fun insertReminder(title: String, triggerTime: String, isRecurring: Boolean = false, interval: String = "Daily"): Long {
    return dao.insertReminder(
      ReminderEntity(
        title = title,
        triggerTime = triggerTime,
        isRecurring = isRecurring,
        repeatInterval = interval
      )
    )
  }

  suspend fun updateReminder(reminder: ReminderEntity) = dao.updateReminder(reminder)
  suspend fun deleteReminder(reminder: ReminderEntity) = dao.deleteReminder(reminder)

  suspend fun insertNote(title: String, content: String, tag: String = "General"): Long {
    return dao.insertNote(
      NoteEntity(
        title = title,
        content = content,
        categoryTag = tag
      )
    )
  }

  suspend fun updateNote(note: NoteEntity) = dao.updateNote(note)
  suspend fun deleteNote(note: NoteEntity) = dao.deleteNote(note)

  suspend fun insertAutomation(automation: AutomationEntity) = dao.insertAutomation(automation)
  suspend fun updateAutomation(automation: AutomationEntity) = dao.updateAutomation(automation)
  suspend fun deleteAutomation(automation: AutomationEntity) = dao.deleteAutomation(automation)

  suspend fun insertLog(toolName: String, status: String, actionSummary: String, details: String = "") {
    dao.insertLog(
      ActivityLogEntity(
        toolName = toolName,
        status = status,
        actionSummary = actionSummary,
        details = details
      )
    )
  }

  suspend fun clearLogs() = dao.clearLogs()

  suspend fun insertChatMessage(sender: String, text: String, toolExecutionJson: String = "", planStepsJson: String = "", imageUri: String? = null) {
    dao.insertChatMessage(
      ChatMessageEntity(
        sender = sender,
        text = text,
        toolExecutionJson = toolExecutionJson,
        planStepsJson = planStepsJson,
        imageUri = imageUri
      )
    )
  }

  suspend fun clearChat() = dao.clearChatMessages()

  suspend fun insertCustomCommand(trigger: String, description: String, actionsJson: String = "[]") {
    dao.insertCustomCommand(
      CustomCommandEntity(
        triggerPhrase = trigger,
        actionDescription = description,
        actionsJson = actionsJson
      )
    )
  }

  suspend fun deleteCustomCommand(cmd: CustomCommandEntity) = dao.deleteCustomCommand(cmd)

  suspend fun seedDefaultsIfEmpty() {
    val existingAutomations = automations.firstOrNull()
    if (existingAutomations.isNullOrEmpty()) {
      dao.insertAutomation(
        AutomationEntity(
          name = "Morning Briefing",
          description = "Announces system health, battery level, current time, weather forecast, and active tasks.",
          iconName = "wb_sunny",
          stepsJson = "[\"Check Battery & Telemetry\", \"Fetch Weather Briefing\", \"Read Scheduled Reminders\", \"Summarize Priority Tasks\", \"Vocalize Executive Summary\"]",
          isEnabled = true,
          scheduleTime = "08:00 AM",
          triggerType = "TIME"
        )
      )
      dao.insertAutomation(
        AutomationEntity(
          name = "Work Protocol",
          description = "Configures workspace environment, prioritizes engineering tasks, and mutes unnecessary alerts.",
          iconName = "work",
          stepsJson = "[\"Open Dev Tools / Browser\", \"Filter High Priority Tasks\", \"Activate Do-Not-Disturb profile\", \"Log protocol start timestamp\"]",
          isEnabled = true,
          scheduleTime = "09:30 AM",
          triggerType = "MANUAL"
        )
      )
      dao.insertAutomation(
        AutomationEntity(
          name = "Security Sweep",
          description = "Validates active API connections, memory integrity, camera/mic permissions, and telemetry status.",
          iconName = "security",
          stepsJson = "[\"Audit Sensor Permissions\", \"Inspect Memory Footprint\", \"Ping AI Providers\", \"Verify Network Encryption\"]",
          isEnabled = true,
          scheduleTime = "",
          triggerType = "MANUAL"
        )
      )
      dao.insertAutomation(
        AutomationEntity(
          name = "Night Standby",
          description = "Dims HUD intensity, logs day summary, schedules tomorrow's alarms, and enters low power.",
          iconName = "bedtime",
          stepsJson = "[\"Save Session Context\", \"Archive Completed Tasks\", \"Dim Interface to Minimum\", \"Enable Passive Standby\"]",
          isEnabled = true,
          scheduleTime = "11:00 PM",
          triggerType = "TIME"
        )
      )
    }

    val existingMemories = memories.firstOrNull()
    if (existingMemories.isNullOrEmpty()) {
      dao.insertMemory(
        MemoryEntity(
          category = MemoryCategory.USER_PROFILE.name,
          keyName = "Honorific Designation",
          content = "Sir (User prefers polite, refined communication).",
          tags = "protocol, profile"
        )
      )
      dao.insertMemory(
        MemoryEntity(
          category = MemoryCategory.PREFERENCES.name,
          keyName = "Response Style",
          content = "Concise, highly structured, elegant sci-fi tone with actionable summaries.",
          tags = "personality, style"
        )
      )
      dao.insertMemory(
        MemoryEntity(
          category = MemoryCategory.PERSONAL_KNOWLEDGE.name,
          keyName = "Core Directive",
          content = "Operate as the central intelligent command center, digital companion, and automation controller.",
          tags = "mission, core"
        )
      )
    }

    val existingTasks = tasks.firstOrNull()
    if (existingTasks.isNullOrEmpty()) {
      dao.insertTask(
        TaskEntity(
          title = "Initialize Holographic Command Center",
          description = "Verify neural link, telemetry streams, and voice engine synthesis.",
          isCompleted = true,
          priority = TaskPriority.CRITICAL.name,
          dueDate = "Today"
        )
      )
      dao.insertTask(
        TaskEntity(
          title = "Configure Primary AI Provider",
          description = "Select Gemini or alternate provider in the API Center.",
          isCompleted = false,
          priority = TaskPriority.HIGH.name,
          dueDate = "ASAP"
        )
      )
      dao.insertTask(
        TaskEntity(
          title = "Calibrate Voice & Vision Sensors",
          description = "Test speech-to-text recognition and optical analysis modules.",
          isCompleted = false,
          priority = TaskPriority.MEDIUM.name,
          dueDate = "Tomorrow"
        )
      )
    }

    val existingChat = chatMessages.firstOrNull()
    if (existingChat.isNullOrEmpty()) {
      dao.insertChatMessage(
        ChatMessageEntity(
          sender = "JARVIS",
          text = "Good day, Sir. J.A.R.V.I.S. neural interface is online and operating at peak nominal capacity. All telemetry channels, memory banks, and automation controllers are at your command.",
          toolExecutionJson = "",
          planStepsJson = ""
        )
      )
    }
  }
}
