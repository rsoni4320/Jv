package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.data.local.MemoryEntity
import com.example.data.model.MemoryCategory
import com.example.ui.JarvisViewModel
import com.example.ui.components.HolographicCard
import com.example.ui.theme.JarvisAmber
import com.example.ui.theme.JarvisBackground
import com.example.ui.theme.JarvisBorderSubtle
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisCyanBright
import com.example.ui.theme.JarvisRed
import com.example.ui.theme.JarvisSurfaceElevated
import com.example.ui.theme.JarvisTextDim
import com.example.ui.theme.JarvisTextPrimary
import com.example.ui.theme.JarvisTextSecondary

@Composable
fun MemoryScreen(
  viewModel: JarvisViewModel,
  modifier: Modifier = Modifier
) {
  val memories by viewModel.memories.collectAsStateWithLifecycle()
  var selectedCategory by remember { mutableStateOf<MemoryCategory?>(null) }
  var showAddDialog by remember { mutableStateOf(false) }

  val filteredMemories = if (selectedCategory == null) {
    memories
  } else {
    memories.filter { it.category == selectedCategory!!.name }
  }

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

      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Psychology,
            contentDescription = null,
            tint = JarvisCyan,
            modifier = Modifier.size(24.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text(
              text = "NEURAL MEMORY BANK",
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp,
              color = JarvisCyan
            )
            Text(
              text = "${memories.size} Knowledge records persistent in core DB",
              fontFamily = FontFamily.SansSerif,
              fontSize = 11.sp,
              color = JarvisTextSecondary
            )
          }
        }

        IconButton(
          onClick = { viewModel.clearAllMemories() }
        ) {
          Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Purge All Memories",
            tint = JarvisTextDim
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Category Filter Chips
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        Surface(
          onClick = { selectedCategory = null },
          shape = RoundedCornerShape(8.dp),
          color = if (selectedCategory == null) JarvisCyan.copy(alpha = 0.3f) else Color(0x1A00F0FF)
        ) {
          Text(
            text = "ALL (${memories.size})",
            fontFamily = FontFamily.Monospace,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = if (selectedCategory == null) JarvisCyanBright else JarvisTextSecondary,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
          )
        }

        MemoryCategory.entries.forEach { cat ->
          val count = memories.count { it.category == cat.name }
          Surface(
            onClick = { selectedCategory = cat },
            shape = RoundedCornerShape(8.dp),
            color = if (selectedCategory == cat) JarvisCyan.copy(alpha = 0.3f) else Color(0x1A00F0FF)
          ) {
            Text(
              text = "${cat.displayName.uppercase()} ($count)",
              fontFamily = FontFamily.Monospace,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = if (selectedCategory == cat) JarvisCyanBright else JarvisTextSecondary,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // List of Memory Cards
      LazyColumn(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        items(filteredMemories, key = { it.id }) { memory ->
          MemoryCardItem(
            memory = memory,
            onDelete = { viewModel.deleteMemory(memory) }
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))
    }

    // Floating Action Button
    FloatingActionButton(
      onClick = { showAddDialog = true },
      containerColor = JarvisCyan,
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(20.dp)
    ) {
      Icon(
        imageVector = Icons.Default.Add,
        contentDescription = "Add Memory Entity",
        tint = JarvisBackground
      )
    }
  }

  // Add Memory Dialog
  if (showAddDialog) {
    var keyInput by remember { mutableStateOf("") }
    var contentInput by remember { mutableStateOf("") }
    var catSelected by remember { mutableStateOf(MemoryCategory.USER_PROFILE) }

    AlertDialog(
      onDismissRequest = { showAddDialog = false },
      containerColor = JarvisSurfaceElevated,
      title = {
        Text("STORE NEW MEMORY ENTITY", fontFamily = FontFamily.Monospace, fontSize = 13.sp, color = JarvisCyan)
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = keyInput,
            onValueChange = { keyInput = it },
            label = { Text("Memory Key / Descriptor", color = JarvisCyan) },
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = JarvisCyan,
              unfocusedBorderColor = JarvisBorderSubtle,
              focusedTextColor = JarvisTextPrimary,
              unfocusedTextColor = JarvisTextPrimary
            )
          )

          OutlinedTextField(
            value = contentInput,
            onValueChange = { contentInput = it },
            label = { Text("Memory Fact / Content", color = JarvisCyan) },
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
            if (keyInput.isNotBlank() && contentInput.isNotBlank()) {
              viewModel.addMemory(catSelected, keyInput, contentInput)
              showAddDialog = false
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = JarvisCyan)
        ) {
          Text("COMMIT", color = JarvisBackground, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
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
fun MemoryCardItem(
  memory: MemoryEntity,
  onDelete: () -> Unit
) {
  HolographicCard(modifier = Modifier.fillMaxWidth()) {
    Column {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "[${memory.category}]",
            fontFamily = FontFamily.Monospace,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = JarvisCyan
          )
          Text(
            text = memory.keyName,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = JarvisTextPrimary
          )
        }

        IconButton(
          onClick = onDelete,
          modifier = Modifier.size(28.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Delete Memory",
            tint = JarvisRed.copy(alpha = 0.7f),
            modifier = Modifier.size(16.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = memory.content,
        fontFamily = FontFamily.SansSerif,
        fontSize = 12.sp,
        lineHeight = 17.sp,
        color = JarvisTextSecondary
      )
    }
  }
}
