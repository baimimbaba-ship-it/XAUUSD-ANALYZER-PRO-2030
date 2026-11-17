package com.xauusd.pro2030.core.settings

class NotificationSettings {
    var enabled: Boolean = true
    var soundEnabled: Boolean = true
    var voiceEnabled: Boolean = false

    fun resetDefaults() {
        enabled = true
        soundEnabled = true
        voiceEnabled = false
    }
}
