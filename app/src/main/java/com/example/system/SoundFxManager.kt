package com.example.system

import android.media.AudioManager
import android.media.ToneGenerator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SoundFxManager(
  private val scope: CoroutineScope
) {
  var isSoundEnabled: Boolean = true
  private var toneGen: ToneGenerator? = null

  init {
    try {
      toneGen = ToneGenerator(AudioManager.STREAM_MUSIC, 70)
    } catch (e: Exception) {
      // Ignored if audio device restricted
    }
  }

  fun playStartup() {
    if (!isSoundEnabled) return
    scope.launch(Dispatchers.Default) {
      try {
        toneGen?.startTone(ToneGenerator.TONE_PROP_BEEP, 80)
        delay(100)
        toneGen?.startTone(ToneGenerator.TONE_PROP_ACK, 120)
        delay(140)
        toneGen?.startTone(ToneGenerator.TONE_PROP_PROMPT, 160)
      } catch (e: Exception) {
        // Safe fallback
      }
    }
  }

  fun playListening() {
    if (!isSoundEnabled) return
    scope.launch(Dispatchers.Default) {
      try {
        toneGen?.startTone(ToneGenerator.TONE_PROP_PROMPT, 70)
      } catch (e: Exception) {}
    }
  }

  fun playSuccess() {
    if (!isSoundEnabled) return
    scope.launch(Dispatchers.Default) {
      try {
        toneGen?.startTone(ToneGenerator.TONE_PROP_ACK, 90)
        delay(100)
        toneGen?.startTone(ToneGenerator.TONE_PROP_BEEP2, 120)
      } catch (e: Exception) {}
    }
  }

  fun playWarning() {
    if (!isSoundEnabled) return
    scope.launch(Dispatchers.Default) {
      try {
        toneGen?.startTone(ToneGenerator.TONE_PROP_NACK, 180)
      } catch (e: Exception) {}
    }
  }

  fun playClick() {
    if (!isSoundEnabled) return
    scope.launch(Dispatchers.Default) {
      try {
        toneGen?.startTone(ToneGenerator.TONE_PROP_BEEP, 40)
      } catch (e: Exception) {}
    }
  }

  fun release() {
    toneGen?.release()
    toneGen = null
  }
}
