package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
  entities = [
    MemoryEntity::class,
    TaskEntity::class,
    ReminderEntity::class,
    NoteEntity::class,
    AutomationEntity::class,
    ActivityLogEntity::class,
    ChatMessageEntity::class,
    CustomCommandEntity::class
  ],
  version = 1,
  exportSchema = false
)
abstract class JarvisDatabase : RoomDatabase() {
  abstract fun jarvisDao(): JarvisDao

  companion object {
    @Volatile
    private var INSTANCE: JarvisDatabase? = null

    fun getDatabase(context: Context): JarvisDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          JarvisDatabase::class.java,
          "jarvis_core_db"
        )
          .fallbackToDestructiveMigration()
          .build()
        INSTANCE = instance
        instance
      }
    }
  }
}
