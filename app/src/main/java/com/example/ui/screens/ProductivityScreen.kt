package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.NoteEntity
import com.example.data.local.ReminderEntity
import com.example.data.local.TaskEntity
import com.example.data.model.TaskPriority
import com.example.ui.JarvisViewModel
import com.example.ui.components.HolographicCard
import com.example.ui.theme.JarvisAmber
import com.example.ui.theme.JarvisBackground
import com.example.ui.theme.JarvisBorderSubtle
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisCyanBright
import com.example.ui.theme.JarvisGreen
import com.example.ui.theme.JarvisRed
import com.example.ui.theme.JarvisSurfaceElevated
import com.example.ui.theme.JarvisTextPrimary
import com.example.ui.theme.JarvisTextSecondary

@Composable
fun ProductivityScreen(
  viewModel: JarvisViewModel,
  modifier: Modifier = Modifier
) {
  var selectedTab by remember { mutableIntStateOf(0) }
  val tabs = listOf("TASKS", "REMINDERS", "NOTES")

  val tasks by viewModel.tasks.collectAsStateWithLifecycle()
  val reminders by viewModel.reminders.collectAsStateWithLifecycle()
  val notes by viewModel.notes.collectAsStateWithLifecycle()

  var showAddDialog by remember { mutableStateOf(false) }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(JarvisBackground)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 14.dp)
    ) {
      Spacer(modifier = Modifier.height(10.dp))

      // Tab Header
      TabRow(
        selectedTabIndex = selectedTab,
        containerColor = Color.Transparent,
        contentColor = JarvisCyan,
        indicator = { tabPositions ->
          TabRowDefaults.SecondaryIndicator(
            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
            color = JarvisCyan
          )
        }
      ) {
        tabs.forEachIndexed { index, title ->
          Tab(
            selected = selectedTab == index,
            onClick = { selectedTab = index },
            text = {
              Text(
                text = title,
                fontFamily = FontFamily.Monospace,
                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                fontSize = 12.sp,
                color = if (selectedTab == index) JarvisCyanBright else JarvisTextSecondary
              )
            }
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Content based on tab
      when (selectedTab) {
        0 -> TasksTabContent(
          tasks = tasks,
          onToggle = { viewModel.toggleTaskCompletion(it) },
          onDelete = { viewModel.deleteTask(it) }
        )

        1 -> RemindersTabContent(
          reminders = reminders,
          onDelete = { viewModel.deleteReminder(it) }
        )

        2 -> NotesTabContent(
          notes = notes,
          onDelete = { viewModel.deleteNote(it) }
        )
      }
    }

    // FAB to Add Item
    FloatingActionButton(
      onClick = { showAddDialog = true },
      containerColor = JarvisCyan,
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(20.dp)
    ) {
      Icon(Icons.Default.Add, contentDescription = "Add Item", tint = JarvisBackground)
    }
  }

  if (showAddDialog) {
    var titleInput by remember { mutableStateOf("") }
    var detailInput by remember { mutableStateOf("") }

    AlertDialog(
      onDismissRequest = { showAddDialog = false },
      containerColor = JarvisSurfaceElevated,
      title = {
        Text("ADD ${tabs[selectedTab]} ITEM", fontFamily = FontFamily.Monospace, fontSize = 13.sp, color = JarvisCyan)
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = titleInput,
            onValueChange = { titleInput = it },
            label = { Text("Title", color = JarvisCyan) },
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = JarvisCyan,
              unfocusedBorderColor = JarvisBorderSubtle,
              focusedTextColor = JarvisTextPrimary,
              unfocusedTextColor = JarvisTextPrimary
            )
          )

          OutlinedTextField(
            value = detailInput,
            onValueChange = { detailInput = it },
            label = { Text(if (selectedTab == 1) "Time (e.g. 09:00 AM)" else "Description / Body", color = JarvisCyan) },
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = JarvisCyan,
              unfocusedBorderColor = JarvisBorderSubtle,
              focusedTextColor = JarvisTextPrimary,
              unfocusedTextColor = JarvisTextPrimary
            )
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (titleInput.isNotBlank()) {
              when (selectedTab) {
                0 -> viewModel.addTask(titleInput, detailInput)
                1 -> viewModel.addReminder(titleInput, detailInput.ifBlank { "Tomorrow 09:00 AM" })
                2 -> viewModel.addNote(titleInput, detailInput)
              }
              showAddDialog = false
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = JarvisCyan)
        ) {
          Text("CREATE", color = JarvisBackground, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showAddDialog = false }) {
          Text("CANCEL", color = JarvisTextSecondary, fontFamily = FontFamily.Monospace)
        }
      }
    )
  }
}

@Composable
fun TasksTabContent(
  tasks: List<TaskEntity>,
  onToggle: (TaskEntity) -> Unit,
  onDelete: (TaskEntity) -> Unit
) {
  LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
    items(tasks, key = { it.id }) { task ->
      HolographicCard(modifier = Modifier.fillMaxWidth()) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
          ) {
            IconButton(
              onClick = { onToggle(task) },
              modifier = Modifier.size(32.dp)
            ) {
              Icon(
                imageVector = if (task.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                contentDescription = null,
                tint = if (task.isCompleted) JarvisGreen else JarvisCyan
              )
            }

            Spacer(modifier = Modifier.width(6.dp))

            Column {
              Text(
                text = task.title,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = if (task.isCompleted) JarvisTextSecondary else JarvisTextPrimary
              )
              if (task.description.isNotBlank()) {
                Text(
                  text = task.description,
                  fontFamily = FontFamily.SansSerif,
                  fontSize = 11.sp,
                  color = JarvisTextSecondary
                )
              }
            }
          }

          IconButton(
            onClick = { onDelete(task) },
            modifier = Modifier.size(28.dp)
          ) {
            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = JarvisRed.copy(alpha = 0.7f), modifier = Modifier.size(16.dp))
          }
        }
      }
    }
  }
}

@Composable
fun RemindersTabContent(
  reminders: List<ReminderEntity>,
  onDelete: (ReminderEntity) -> Unit
) {
  LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
    items(reminders, key = { it.id }) { reminder ->
      HolographicCard(modifier = Modifier.fillMaxWidth()) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.Alarm, contentDescription = null, tint = JarvisAmber, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = reminder.title,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = JarvisTextPrimary
              )
              Text(
                text = "TRIGGER: ${reminder.triggerTime} (${reminder.repeatInterval})",
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                color = JarvisAmber
              )
            }
          }

          IconButton(
            onClick = { onDelete(reminder) },
            modifier = Modifier.size(28.dp)
          ) {
            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = JarvisRed.copy(alpha = 0.7f), modifier = Modifier.size(16.dp))
          }
        }
      }
    }
  }
}

@Composable
fun NotesTabContent(
  notes: List<NoteEntity>,
  onDelete: (NoteEntity) -> Unit
) {
  LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
    items(notes, key = { it.id }) { note ->
      HolographicCard(modifier = Modifier.fillMaxWidth()) {
        Column {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = note.title,
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp,
              color = JarvisCyan
            )

            IconButton(
              onClick = { onDelete(note) },
              modifier = Modifier.size(28.dp)
            ) {
              Icon(Icons.Default.Delete, contentDescription = "Delete", tint = JarvisRed.copy(alpha = 0.7f), modifier = Modifier.size(16.dp))
            }
          }

          Spacer(modifier = Modifier.height(4.dp))

          Text(
            text = note.content,
            fontFamily = FontFamily.SansSerif,
            fontSize = 12.sp,
            color = JarvisTextSecondary
          )
        }
      }
    }
  }
}
