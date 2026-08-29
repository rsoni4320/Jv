package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.ChatMessageEntity
import com.example.ui.JarvisViewModel
import com.example.ui.components.HolographicCard
import com.example.ui.theme.JarvisBackground
import com.example.ui.theme.JarvisBorderGlow
import com.example.ui.theme.JarvisBorderSubtle
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisCyanBright
import com.example.ui.theme.JarvisCyanDim
import com.example.ui.theme.JarvisGreen
import com.example.ui.theme.JarvisRed
import com.example.ui.theme.JarvisSurface
import com.example.ui.theme.JarvisSurfaceElevated
import com.example.ui.theme.JarvisTextDim
import com.example.ui.theme.JarvisTextPrimary
import com.example.ui.theme.JarvisTextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatScreen(
  viewModel: JarvisViewModel,
  modifier: Modifier = Modifier
) {
  val messages by viewModel.chatMessages.collectAsStateWithLifecycle()
  val isListening by viewModel.voiceManager.isListening.collectAsStateWithLifecycle()
  val isSpeaking by viewModel.voiceManager.isSpeaking.collectAsStateWithLifecycle()
  val listState = rememberLazyListState()
  var textInput by remember { mutableStateOf("") }

  LaunchedEffect(messages.size) {
    if (messages.isNotEmpty()) {
      listState.animateScrollToItem(messages.size - 1)
    }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(JarvisBackground)
      .padding(horizontal = 14.dp)
  ) {
    // Top Bar Actions
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = "NEURAL CONVERSATION LOGS",
          fontFamily = FontFamily.Monospace,
          fontWeight = FontWeight.Bold,
          fontSize = 12.sp,
          letterSpacing = 1.sp,
          color = JarvisCyanBright
        )
        Text(
          text = "Encrypted Local Memory Stream",
          fontFamily = FontFamily.SansSerif,
          fontSize = 10.sp,
          color = JarvisCyanDim
        )
      }

      IconButton(
        onClick = { viewModel.clearChat() },
        modifier = Modifier
          .size(34.dp)
          .clip(RoundedCornerShape(8.dp))
          .background(Color(0x26083344))
          .border(1.dp, Color(0x2600F0FF), RoundedCornerShape(8.dp))
      ) {
        Icon(
          imageVector = Icons.Default.Delete,
          contentDescription = "Purge Conversation History",
          tint = JarvisTextDim,
          modifier = Modifier.size(16.dp)
        )
      }
    }

    // Message List
    LazyColumn(
      state = listState,
      modifier = Modifier
        .weight(1f)
        .fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      items(messages, key = { it.id }) { message ->
        ChatMessageBubble(
          message = message,
          onSpeak = { text ->
            viewModel.voiceManager.speak(text)
          }
        )
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Frosted Input Bar
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(14.dp))
        .background(Color(0x2E083344), RoundedCornerShape(14.dp))
        .border(1.dp, Color(0x3300F0FF), RoundedCornerShape(14.dp))
        .padding(horizontal = 8.dp, vertical = 2.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      OutlinedTextField(
        value = textInput,
        onValueChange = { textInput = it },
        placeholder = {
          Text(
            text = "Type query or instruction...",
            fontFamily = FontFamily.SansSerif,
            fontSize = 13.sp,
            color = JarvisCyanDim
          )
        },
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = Color.Transparent,
          unfocusedBorderColor = Color.Transparent,
          cursorColor = JarvisCyan,
          focusedTextColor = JarvisTextPrimary,
          unfocusedTextColor = JarvisTextPrimary
        ),
        modifier = Modifier.weight(1f)
      )

      if (isListening || isSpeaking) {
        IconButton(
          onClick = { viewModel.stopSpeaking() },
          modifier = Modifier
            .size(36.dp)
            .background(JarvisRed.copy(alpha = 0.2f), CircleShape)
        ) {
          Icon(Icons.Default.Stop, contentDescription = "Stop", tint = JarvisRed)
        }
      } else {
        IconButton(
          onClick = { viewModel.startVoiceListening() },
          modifier = Modifier
            .size(36.dp)
            .background(JarvisCyan.copy(alpha = 0.15f), CircleShape)
        ) {
          Icon(Icons.Default.Mic, contentDescription = "Mic", tint = JarvisCyanBright)
        }
      }

      Spacer(modifier = Modifier.width(4.dp))

      IconButton(
        onClick = {
          if (textInput.isNotBlank()) {
            viewModel.processUserInput(textInput)
            textInput = ""
          }
        },
        modifier = Modifier
          .size(36.dp)
          .clip(CircleShape)
          .background(Color(0x3300F0FF))
      ) {
        Icon(Icons.Default.Send, contentDescription = "Send", tint = JarvisCyanBright, modifier = Modifier.size(16.dp))
      }
    }

    Spacer(modifier = Modifier.height(16.dp))
  }
}

@Composable
fun ChatMessageBubble(
  message: ChatMessageEntity,
  onSpeak: (String) -> Unit
) {
  val isUser = message.sender == "USER"
  val timeStr = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(message.timestamp))

  Column(
    modifier = Modifier.fillMaxWidth(),
    horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
      Text(
        text = if (isUser) "COMMANDER" else "J.A.R.V.I.S.",
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Bold,
        fontSize = 10.sp,
        color = if (isUser) JarvisCyanBright else JarvisGreen
      )
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = timeStr,
        fontFamily = FontFamily.Monospace,
        fontSize = 9.sp,
        color = JarvisCyanDim
      )
    }

    HolographicCard(
      modifier = Modifier.fillMaxWidth(0.92f),
      borderColor = if (isUser) Color(0x4D00F0FF) else Color(0x4D00FF88),
      backgroundColor = if (isUser) Color(0x33083344) else Color(0x2E062B3B)
    ) {
      Column {
        Text(
          text = message.text,
          fontFamily = FontFamily.SansSerif,
          fontSize = 13.sp,
          lineHeight = 19.sp,
          color = JarvisTextPrimary
        )

        if (message.toolExecutionJson.isNotBlank()) {
          Spacer(modifier = Modifier.height(6.dp))
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .background(Color(0x3300FF88), RoundedCornerShape(4.dp))
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = "TOOL EXECUTED: ${message.toolExecutionJson}",
              fontFamily = FontFamily.Monospace,
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              color = JarvisGreen
            )
          }
        }

        if (!isUser) {
          Spacer(modifier = Modifier.height(4.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
          ) {
            IconButton(
              onClick = { onSpeak(message.text) },
              modifier = Modifier.size(24.dp)
            ) {
              Icon(
                imageVector = Icons.Default.VolumeUp,
                contentDescription = "Vocalize Message",
                tint = JarvisCyan,
                modifier = Modifier.size(16.dp)
              )
            }
          }
        }
      }
    }
  }
}

