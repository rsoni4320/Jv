package com.example.ai

import android.graphics.Bitmap
import android.util.Base64
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

object GeminiClient {
  private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/"

  private val okHttpClient = OkHttpClient.Builder()
    .connectTimeout(60, TimeUnit.SECONDS)
    .readTimeout(60, TimeUnit.SECONDS)
    .writeTimeout(60, TimeUnit.SECONDS)
    .build()

  suspend fun generateContent(
    prompt: String,
    systemInstruction: String? = null,
    modelName: String = "gemini-3.5-flash",
    apiKeyOverride: String? = null,
    bitmap: Bitmap? = null
  ): Result<String> = withContext(Dispatchers.IO) {
    val apiKey = if (!apiKeyOverride.isNullOrBlank()) {
      apiKeyOverride
    } else {
      try {
        BuildConfig.GEMINI_API_KEY
      } catch (e: Exception) {
        ""
      }
    }

    if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
      return@withContext Result.failure(
        IllegalStateException("API key is not configured. Please add your Gemini API key in the Secrets panel or API Center.")
      )
    }

    val url = "$BASE_URL$modelName:generateContent?key=$apiKey"

    val requestJson = JSONObject()
    val contentsArray = JSONArray()
    val contentObj = JSONObject()
    val partsArray = JSONArray()

    // Add prompt part
    val textPart = JSONObject().put("text", prompt)
    partsArray.put(textPart)

    // Add image part if provided
    if (bitmap != null) {
      val outputStream = ByteArrayOutputStream()
      bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
      val base64Image = Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)

      val imagePart = JSONObject().put(
        "inlineData",
        JSONObject()
          .put("mimeType", "image/jpeg")
          .put("data", base64Image)
      )
      partsArray.put(imagePart)
    }

    contentObj.put("parts", partsArray)
    contentsArray.put(contentObj)
    requestJson.put("contents", contentsArray)

    // System instructions
    if (!systemInstruction.isNullOrBlank()) {
      val sysObj = JSONObject()
      val sysParts = JSONArray().put(JSONObject().put("text", systemInstruction))
      sysObj.put("parts", sysParts)
      requestJson.put("systemInstruction", sysObj)
    }

    val requestBody = requestJson.toString().toRequestBody("application/json".toMediaType())
    val request = Request.Builder()
      .url(url)
      .post(requestBody)
      .build()

    try {
      val response = okHttpClient.newCall(request).execute()
      val responseBodyString = response.body?.string() ?: ""

      if (!response.isSuccessful) {
        val errorMsg = try {
          val errorJson = JSONObject(responseBodyString)
          errorJson.optJSONObject("error")?.optString("message") ?: "HTTP error ${response.code}"
        } catch (e: Exception) {
          "HTTP error ${response.code}: $responseBodyString"
        }
        return@withContext Result.failure(Exception(errorMsg))
      }

      val json = JSONObject(responseBodyString)
      val candidates = json.optJSONArray("candidates")
      if (candidates != null && candidates.length() > 0) {
        val firstCandidate = candidates.getJSONObject(0)
        val content = firstCandidate.optJSONObject("content")
        val parts = content?.optJSONArray("parts")
        if (parts != null && parts.length() > 0) {
          val text = parts.getJSONObject(0).optString("text", "")
          if (text.isNotBlank()) {
            return@withContext Result.success(text)
          }
        }
      }

      Result.failure(Exception("Empty response received from AI subsystem."))
    } catch (e: Exception) {
      Result.failure(e)
    }
  }
}
