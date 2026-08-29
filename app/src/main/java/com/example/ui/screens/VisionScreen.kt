package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.ImageSearch
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.JarvisViewModel
import com.example.ui.components.HolographicCard
import com.example.ui.theme.JarvisAmber
import com.example.ui.theme.JarvisBackground
import com.example.ui.theme.JarvisBorderGlow
import com.example.ui.theme.JarvisBorderSubtle
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisCyanBright
import com.example.ui.theme.JarvisGreen
import com.example.ui.theme.JarvisSurfaceElevated
import com.example.ui.theme.JarvisTextPrimary
import com.example.ui.theme.JarvisTextSecondary

@Composable
fun VisionScreen(
  viewModel: JarvisViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var selectedBitmap by remember { mutableStateOf<Bitmap?>(null) }
  var visionPrompt by remember { mutableStateOf("Perform full optical analysis and scene breakdown.") }
  var isAnalyzing by remember { mutableStateOf(false) }
  var analysisResult by remember { mutableStateOf<String?>(null) }

  // Gallery Picker
  val imagePickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.GetContent()
  ) { uri: Uri? ->
    uri?.let {
      try {
        val bitmap = if (Build.VERSION.SDK_INT < 28) {
          @Suppress("DEPRECATION")
          MediaStore.Images.Media.getBitmap(context.contentResolver, it)
        } else {
          val source = ImageDecoder.createSource(context.contentResolver, it)
          ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
            decoder.isMutableRequired = true
          }
        }
        selectedBitmap = bitmap
        analysisResult = null
      } catch (e: Exception) {
        // Ignored
      }
    }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(JarvisBackground)
      .padding(horizontal = 14.dp)
      .verticalScroll(rememberScrollState()),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Spacer(modifier = Modifier.height(10.dp))

    // Header
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(
        imageVector = Icons.Default.CameraAlt,
        contentDescription = null,
        tint = JarvisCyan,
        modifier = Modifier.size(24.dp)
      )
      Spacer(modifier = Modifier.width(8.dp))
      Column {
        Text(
          text = "OPTICAL NEURAL SENSOR",
          fontFamily = FontFamily.Monospace,
          fontWeight = FontWeight.Bold,
          fontSize = 14.sp,
          color = JarvisCyan
        )
        Text(
          text = "Real-time Multimodal Vision & OCR Subsystem",
          fontFamily = FontFamily.SansSerif,
          fontSize = 11.sp,
          color = JarvisTextSecondary
        )
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Image Preview Area or Placeholder
    if (selectedBitmap != null) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(240.dp)
          .clip(RoundedCornerShape(12.dp))
          .border(1.dp, JarvisBorderGlow, RoundedCornerShape(12.dp))
          .background(Color(0xFF030A14)),
        contentAlignment = Alignment.Center
      ) {
        Image(
          bitmap = selectedBitmap!!.asImageBitmap(),
          contentDescription = "Selected optical feed",
          contentScale = ContentScale.Crop,
          modifier = Modifier.fillMaxSize()
        )

        // Overlay Holographic Reticle
        Box(
          modifier = Modifier
            .fillMaxSize()
            .border(2.dp, Color(0x3300F0FF))
        )
      }
    } else {
      HolographicCard(
        modifier = Modifier
          .fillMaxWidth()
          .height(180.dp)
          .clickable { imagePickerLauncher.launch("image/*") }
      ) {
        Column(
          modifier = Modifier.fillMaxSize(),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center
        ) {
          Icon(
            imageVector = Icons.Default.AddPhotoAlternate,
            contentDescription = "Select Image",
            tint = JarvisCyan,
            modifier = Modifier.size(48.dp)
          )
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "TAP TO LOAD OPTICAL TARGET",
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = JarvisCyanBright
          )
          Text(
            text = "Supports Photos, Documents, Screenshots, Diagrams",
            fontFamily = FontFamily.SansSerif,
            fontSize = 11.sp,
            color = JarvisTextSecondary
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Action Modes
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Button(
        onClick = {
          visionPrompt = "Extract and transcribe all text from this image with high precision (OCR)."
        },
        colors = ButtonDefaults.buttonColors(containerColor = Color(0x3300F0FF)),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.weight(1f)
      ) {
        Icon(Icons.Default.DocumentScanner, contentDescription = null, tint = JarvisCyan, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("OCR TEXT", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = JarvisCyan)
      }

      Button(
        onClick = {
          visionPrompt = "Identify and describe all objects, text, and environmental context in this scene."
        },
        colors = ButtonDefaults.buttonColors(containerColor = Color(0x3300F0FF)),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.weight(1f)
      ) {
        Icon(Icons.Default.ImageSearch, contentDescription = null, tint = JarvisCyan, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("ANALYZE", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = JarvisCyan)
      }

      Button(
        onClick = { imagePickerLauncher.launch("image/*") },
        colors = ButtonDefaults.buttonColors(containerColor = Color(0x330080FF)),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.weight(1f)
      ) {
        Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("CHANGE", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = Color.White)
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Prompt Input Field
    OutlinedTextField(
      value = visionPrompt,
      onValueChange = { visionPrompt = it },
      label = { Text("Optical Query Parameter", color = JarvisCyan) },
      colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = JarvisCyan,
        unfocusedBorderColor = JarvisBorderSubtle,
        focusedTextColor = JarvisTextPrimary,
        unfocusedTextColor = JarvisTextPrimary
      ),
      modifier = Modifier.fillMaxWidth()
    )

    Spacer(modifier = Modifier.height(10.dp))

    // Analyze Button
    Button(
      onClick = {
        if (selectedBitmap != null) {
          isAnalyzing = true
          viewModel.processUserInput(
            prompt = visionPrompt,
            bitmap = selectedBitmap
          )
        } else {
          viewModel.processUserInput(visionPrompt)
        }
      },
      colors = ButtonDefaults.buttonColors(containerColor = JarvisCyan),
      shape = RoundedCornerShape(8.dp),
      modifier = Modifier
        .fillMaxWidth()
        .height(48.dp)
    ) {
      if (isAnalyzing) {
        CircularProgressIndicator(color = JarvisBackground, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
        Spacer(modifier = Modifier.width(8.dp))
        Text("PROCESSING OPTICAL ARRAY...", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = JarvisBackground)
      } else {
        Icon(Icons.Default.Psychology, contentDescription = null, tint = JarvisBackground)
        Spacer(modifier = Modifier.width(8.dp))
        Text("EXECUTE OPTICAL REASONING", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = JarvisBackground)
      }
    }

    Spacer(modifier = Modifier.height(16.dp))
  }
}
