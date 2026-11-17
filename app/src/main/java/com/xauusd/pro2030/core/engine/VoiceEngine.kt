package com.xauusd.pro2030.core.engine

/** Speech output is intentionally an adapter boundary until Android TTS lifecycle is wired. */
class VoiceEngine(private val speaker: Speaker? = null) {
    fun interface Speaker { fun speak(text: String): Boolean }
    fun speakSignal(text: String): Boolean {
        if (text.isBlank()) return false
        return speaker?.speak(text) ?: false
    }
}
