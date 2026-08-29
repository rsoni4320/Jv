package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface JarvisDao {
  // Memories
  @Query("SELECT * FROM memories ORDER BY timestamp DESC")
  fun getAllMemories(): Flow<List<MemoryEntity>>

  @Query("SELECT * FROM memories WHERE category = :category ORDER BY timestamp DESC")
  fun getMemoriesByCategory(category: String): Flow<List<MemoryEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertMemory(memory: MemoryEntity): Long

  @Update
  suspend fun updateMemory(memory: MemoryEntity)

  @Delete
  suspend fun deleteMemory(memory: MemoryEntity)

  @Query("DELETE FROM memories")
  suspend fun clearAllMemories()

  // Tasks
  @Query("SELECT * FROM tasks ORDER BY isCompleted ASC, id DESC")
  fun getAllTasks(): Flow<List<TaskEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertTask(task: TaskEntity): Long

  @Update
  suspend fun updateTask(task: TaskEntity)

  @Delete
  suspend fun deleteTask(task: TaskEntity)

  // Reminders
  @Query("SELECT * FROM reminders ORDER BY id DESC")
  fun getAllReminders(): Flow<List<ReminderEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertReminder(reminder: ReminderEntity): Long

  @Update
  suspend fun updateReminder(reminder: ReminderEntity)

  @Delete
  suspend fun deleteReminder(reminder: ReminderEntity)

  // Notes
  @Query("SELECT * FROM notes ORDER BY isPinned DESC, updatedTimestamp DESC")
  fun getAllNotes(): Flow<List<NoteEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertNote(note: NoteEntity): Long

  @Update
  suspend fun updateNote(note: NoteEntity)

  @Delete
  suspend fun deleteNote(note: NoteEntity)

  // Automations
  @Query("SELECT * FROM automations ORDER BY id ASC")
  fun getAllAutomations(): Flow<List<AutomationEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAutomation(automation: AutomationEntity): Long

  @Update
  suspend fun updateAutomation(automation: AutomationEntity)

  @Delete
  suspend fun deleteAutomation(automation: AutomationEntity)

  // Activity Logs
  @Query("SELECT * FROM activity_logs ORDER BY timestamp DESC LIMIT 100")
  fun getRecentLogs(): Flow<List<ActivityLogEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertLog(log: ActivityLogEntity): Long

  @Query("DELETE FROM activity_logs")
  suspend fun clearLogs()

  // Chat Messages
  @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
  fun getAllChatMessages(): Flow<List<ChatMessageEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertChatMessage(message: ChatMessageEntity): Long

  @Query("DELETE FROM chat_messages")
  suspend fun clearChatMessages()

  // Custom Commands
  @Query("SELECT * FROM custom_commands ORDER BY id ASC")
  fun getAllCustomCommands(): Flow<List<CustomCommandEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCustomCommand(command: CustomCommandEntity): Long

  @Delete
  suspend fun deleteCustomCommand(command: CustomCommandEntity)
}
