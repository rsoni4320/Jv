package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.MemoryCategory
import com.example.data.model.TaskPriority

@Entity(tableName = "memories")
data class MemoryEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val category: String = MemoryCategory.PERSONAL_KNOWLEDGE.name,
  val keyName: String,
  val content: String,
  val timestamp: Long = System.currentTimeMillis(),
  val tags: String = ""
)

@Entity(tableName = "tasks")
data class TaskEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val title: String,
  val description: String = "",
  val isCompleted: Boolean = false,
  val priority: String = TaskPriority.MEDIUM.name,
  val dueDate: String = "",
  val createdTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "reminders")
data class ReminderEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val title: String,
  val triggerTime: String,
  val isFired: Boolean = false,
  val isRecurring: Boolean = false,
  val repeatInterval: String = "Daily"
)

@Entity(tableName = "notes")
data class NoteEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val title: String,
  val content: String,
  val updatedTimestamp: Long = System.currentTimeMillis(),
  val isPinned: Boolean = false,
  val categoryTag: String = "General"
)

@Entity(tableName = "automations")
data class AutomationEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val name: String,
  val description: String,
  val iconName: String = "routine",
  val stepsJson: String = "[]",
  val isEnabled: Boolean = true,
  val scheduleTime: String = "",
  val triggerType: String = "MANUAL"
)

@Entity(tableName = "activity_logs")
data class ActivityLogEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val timestamp: Long = System.currentTimeMillis(),
  val toolName: String,
  val status: String,
  val actionSummary: String,
  val details: String = ""
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val sender: String, // "USER" | "JARVIS" | "SYSTEM"
  val text: String,
  val timestamp: Long = System.currentTimeMillis(),
  val toolExecutionJson: String = "",
  val planStepsJson: String = "",
  val imageUri: String? = null
)

@Entity(tableName = "custom_commands")
data class CustomCommandEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val triggerPhrase: String,
  val actionDescription: String,
  val actionsJson: String = "[]",
  val isEnabled: Boolean = true
)
