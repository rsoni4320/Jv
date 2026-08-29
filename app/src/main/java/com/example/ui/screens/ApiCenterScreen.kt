package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Api
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AIProviderType
import com.example.ui.JarvisViewModel
import com.example.ui.components.HolographicCard
import com.example.ui.theme.JarvisAmber
import com.example.ui.theme.JarvisBackground
import com.example.ui.theme.JarvisBorderSubtle
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisCyanBright
import com.example.ui.theme.JarvisGreen
import com.example.ui.theme.JarvisRed
import com.example.ui.theme.JarvisTextPrimary
import com.example.ui.theme.JarvisTextSecondary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun ApiCenterScreen(
  viewModel: JarvisViewModel,
  modifier: Modifier = Modifier
) {
  val scope = rememberCoroutineScope()
  var currentProvider by remember { mutableStateOf(viewModel.aiBrain.activeProvider) }
  var apiKeyInput by remember { mutableStateOf(viewModel.aiBrain.customApiKey) }
  var modelInput by remember { mutableStateOf(viewModel.aiBrain.customModel) }
  var endpointInput by remember { mutableStateOf(viewModel.aiBrain.customEndpoint) }

  var testStatus by remember { mutableStateOf<String?>(null) }
  var isTesting by remember { mutableStateOf(false) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(JarvisBackground)
      .padding(horizontal = 14.dp)
      .verticalScroll(rememberScrollState())
  ) {
    Spacer(modifier = Modifier.height(10.dp))

    // Header
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(
        imageVector = Icons.Default.Api,
        contentDescription = null,
        tint = JarvisCyan,
        modifier = Modifier.size(24.dp)
      )
      Spacer(modifier = Modifier.width(8.dp))
      Column {
        Text(
          text = "API & NEURAL ENGINE CENTER",
          fontFamily = FontFamily.Monospace,
          fontWeight = FontWeight.Bold,
          fontSize = 14.sp,
          color = JarvisCyan
        )
        Text(
          text = "Multi-provider configuration and model selector",
          fontFamily = FontFamily.SansSerif,
          fontSize = 11.sp,
          color = JarvisTextSecondary
        )
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Providers List
    AIProviderType.entries.forEach { provider ->
      val isSelected = currentProvider == provider
      HolographicCard(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 4.dp),
        borderColor = if (isSelected) JarvisCyan else JarvisBorderSubtle
      ) {
        Column {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              IconButton(
                onClick = {
                  currentProvider = provider
                  viewModel.aiBrain.activeProvider = provider
                  if (modelInput.isBlank()) {
                    modelInput = provider.defaultModel
                  }
                },
                modifier = Modifier.size(28.dp)
              ) {
                Icon(
                  imageVector = if (isSelected) Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,
                  contentDescription = null,
                  tint = if (isSelected) JarvisCyan else JarvisTextSecondary
                )
              }

              Spacer(modifier = Modifier.width(6.dp))

              Column {
                Text(
                  text = provider.displayName,
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.sp,
                  color = JarvisTextPrimary
                )
                Text(
                  text = "DEFAULT: ${provider.defaultModel}",
                  fontFamily = FontFamily.Monospace,
                  fontSize = 10.sp,
                  color = JarvisCyanBright
                )
              }
            }

            if (isSelected) {
              Text(
                text = "ACTIVE",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                color = JarvisGreen
              )
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Provider Config Section
    HolographicCard(modifier = Modifier.fillMaxWidth()) {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
          text = "CREDENTIALS & OVERRIDES (${currentProvider.displayName})",
          fontFamily = FontFamily.Monospace,
          fontWeight = FontWeight.Bold,
          fontSize = 12.sp,
          color = JarvisCyan
        )

        OutlinedTextField(
          value = apiKeyInput,
          onValueChange = {
            apiKeyInput = it
            viewModel.aiBrain.customApiKey = it
          },
          label = { Text("API Key (or configure via Secrets panel)", color = JarvisCyan) },
          placeholder = { Text("sk-...", color = JarvisTextSecondary) },
          singleLine = true,
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = JarvisCyan,
            unfocusedBorderColor = JarvisBorderSubtle,
            focusedTextColor = JarvisTextPrimary,
            unfocusedTextColor = JarvisTextPrimary
          ),
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = modelInput,
          onValueChange = {
            modelInput = it
            viewModel.aiBrain.customModel = it
          },
          label = { Text("Model Identifier Override", color = JarvisCyan) },
          placeholder = { Text(currentProvider.defaultModel, color = JarvisTextSecondary) },
          singleLine = true,
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = JarvisCyan,
            unfocusedBorderColor = JarvisBorderSubtle,
            focusedTextColor = JarvisTextPrimary,
            unfocusedTextColor = JarvisTextPrimary
          ),
          modifier = Modifier.fillMaxWidth()
        )

        if (currentProvider == AIProviderType.CUSTOM || currentProvider == AIProviderType.OLLAMA) {
          OutlinedTextField(
            value = endpointInput,
            onValueChange = {
              endpointInput = it
              viewModel.aiBrain.customEndpoint = it
            },
            label = { Text("Custom Base URL Endpoint", color = JarvisCyan) },
            placeholder = { Text("https://api.custom.ai/v1", color = JarvisTextSecondary) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = JarvisCyan,
              unfocusedBorderColor = JarvisBorderSubtle,
              focusedTextColor = JarvisTextPrimary,
              unfocusedTextColor = JarvisTextPrimary
            ),
            modifier = Modifier.fillMaxWidth()
          )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Button(
          onClick = {
            isTesting = true
            testStatus = null
            scope.launch {
              val telemetry = viewModel.telemetry.value
              val testResponse = viewModel.aiBrain.processQuery(
                userPrompt = "JARVIS system ping: Confirm neural connection status in one concise sentence.",
                telemetry = telemetry
              )
              isTesting = false
              testStatus = if (!testResponse.isOffline) {
                "LINK SUCCESS: ${testResponse.providerUsed} responded: \"${testResponse.replyText.take(80)}\""
              } else {
                "OFFLINE MODE: ${testResponse.replyText.take(80)}"
              }
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = JarvisCyan),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          if (isTesting) {
            CircularProgressIndicator(color = JarvisBackground, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
            Spacer(modifier = Modifier.width(6.dp))
            Text("PINGING PROVIDER...", color = JarvisBackground, fontFamily = FontFamily.Monospace)
          } else {
            Text("TEST NEURAL CONNECTION", color = JarvisBackground, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
          }
        }

        if (testStatus != null) {
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = testStatus!!,
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            color = if (testStatus!!.startsWith("LINK SUCCESS")) JarvisGreen else JarvisAmber
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(20.dp))
  }
}
