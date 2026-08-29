package com.example.system

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Locale

class VoiceManager(
  private val context: Context,
  private val scope: CoroutineScope
) : TextToSpeech.OnInitListener {

  private var tts: TextToSpeech? = null
  private var speechRecognizer: SpeechRecognizer? = null
  private var isTtsReady = false

  var speechRate: Float = 1.05f
  var pitchRate: Float = 1.18f // Refined slightly higher pitch for crisp female AI persona
  var isAutoSpeakEnabled: Boolean = true

  private val _isListening = MutableStateFlow(false)
  val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

  private val _isSpeaking = MutableStateFlow(false)
  val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

  private val _audioAmplitude = MutableStateFlow(0f)
  val audioAmplitude: StateFlow<Float> = _audioAmplitude.asStateFlow()

  private val _recognizedTranscript = MutableStateFlow("")
  val recognizedTranscript: StateFlow<String> = _recognizedTranscript.asStateFlow()

  private var amplitudeSimJob: Job? = null

  init {
    tts = TextToSpeech(context.applicationContext, this)
  }

  override fun onInit(status: Int) {
    if (status == TextToSpeech.SUCCESS) {
      isTtsReady = true
      tts?.language = Locale.US
      tts?.setSpeechRate(speechRate)
      tts?.setPitch(pitchRate)

      // Try to find female voice if available
      try {
        val voices = tts?.voices
        val femaleVoice = voices?.find { voice ->
          voice.name.contains("female", ignoreCase = true) ||
            voice.name.contains("en-us-x-sfg", ignoreCase = true) ||
            voice.name.contains("en-gb", ignoreCase = true)
        }
        if (femaleVoice != null) {
          tts?.voice = femaleVoice
        }
      } catch (e: Exception) {
        // Fallback to pitch modulation
      }

      tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
        override fun onStart(utteranceId: String?) {
          _isSpeaking.value = true
          startAmplitudeSimulation()
        }

        override fun onDone(utteranceId: String?) {
          _isSpeaking.value = false
          stopAmplitudeSimulation()
        }

        override fun onError(utteranceId: String?) {
          _isSpeaking.value = false
          stopAmplitudeSimulation()
        }
      })
    }
  }

  fun speak(text: String, onDone: (() -> Unit)? = null) {
    if (!isAutoSpeakEnabled) {
      onDone?.invoke()
      return
    }

    if (isTtsReady && tts != null) {
      tts?.setSpeechRate(speechRate)
      tts?.setPitch(pitchRate)
      val utteranceId = "jarvis_speech_${System.currentTimeMillis()}"
      tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
    }
  }

  fun stopSpeaking() {
    tts?.stop()
    _isSpeaking.value = false
    stopAmplitudeSimulation()
  }

  fun startListening(
    onResult: (String) -> Unit,
    onError: ((String) -> Unit)? = null
  ) {
    if (!SpeechRecognizer.isRecognitionAvailable(context)) {
      onError?.invoke("Speech recognition subsystem unavailable on this device.")
      return
    }

    stopSpeaking()

    scope.launch(Dispatchers.Main) {
      try {
        speechRecognizer?.destroy()
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
          putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
          putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
          putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
          putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }

        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
          override fun onReadyForSpeech(params: Bundle?) {
            _isListening.value = true
            _recognizedTranscript.value = "Listening..."
          }

          override fun onBeginningOfSpeech() {
            _recognizedTranscript.value = "Detecting voice activity..."
          }

          override fun onRmsChanged(rmsdB: Float) {
            val normalized = ((rmsdB + 2f) / 12f).coerceIn(0f, 1f)
            _audioAmplitude.value = normalized
          }

          override fun onBufferReceived(buffer: ByteArray?) {}

          override fun onEndOfSpeech() {
            _isListening.value = false
            _audioAmplitude.value = 0f
          }

          override fun onError(error: Int) {
            _isListening.value = false
            _audioAmplitude.value = 0f
            val message = when (error) {
              SpeechRecognizer.ERROR_NO_MATCH -> "No speech detected."
              SpeechRecognizer.ERROR_AUDIO -> "Audio recording error."
              SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Microphone permission required."
              else -> "Speech recognition paused."
            }
            _recognizedTranscript.value = message
            onError?.invoke(message)
          }

          override fun onResults(results: Bundle?) {
            _isListening.value = false
            _audioAmplitude.value = 0f
            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            val recognizedText = matches?.firstOrNull() ?: ""
            if (recognizedText.isNotBlank()) {
              _recognizedTranscript.value = recognizedText
              onResult(recognizedText)
            }
          }

          override fun onPartialResults(partialResults: Bundle?) {
            val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            val partial = matches?.firstOrNull() ?: ""
            if (partial.isNotBlank()) {
              _recognizedTranscript.value = partial
            }
          }

          override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        speechRecognizer?.startListening(intent)
      } catch (e: Exception) {
        _isListening.value = false
        onError?.invoke(e.message ?: "Failed to initiate microphone stream.")
      }
    }
  }

  fun stopListening() {
    try {
      speechRecognizer?.stopListening()
    } catch (e: Exception) {
      // Ignored
    }
    _isListening.value = false
    _audioAmplitude.value = 0f
  }

  private fun startAmplitudeSimulation() {
    amplitudeSimJob?.cancel()
    amplitudeSimJob = scope.launch(Dispatchers.Default) {
      while (isActive && _isSpeaking.value) {
        val randAmp = (0.2f + (0.8f * Math.random().toFloat()))
        _audioAmplitude.value = randAmp
        delay(60)
      }
      _audioAmplitude.value = 0f
    }
  }

  private fun stopAmplitudeSimulation() {
    amplitudeSimJob?.cancel()
    _audioAmplitude.value = 0f
  }

  fun shutdown() {
    tts?.stop()
    tts?.shutdown()
    speechRecognizer?.destroy()
    stopAmplitudeSimulation()
  }
}
