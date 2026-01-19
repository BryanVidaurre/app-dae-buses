package com.example.pda.ui.utils

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.*

class VoiceAssistant(context: Context) {
    private var tts: TextToSpeech? = null
    private var isReady = false

    init {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val localeChile = Locale("es", "CL")
                val result = tts?.setLanguage(localeChile)

                if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                    isReady = true
                    tts?.setSpeechRate(0.9f)
                    tts?.setPitch(1.1f)
                } else {
                    tts?.setLanguage(Locale("es", "ES"))
                }
            }
        }
    }

    fun speak(text: String) {
        if (isReady) {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    fun stop() {
        tts?.stop()
        tts?.shutdown()
    }
}