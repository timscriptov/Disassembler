package com.mcal.disassembler.data

import android.content.Context
import androidx.preference.PreferenceManager

class Preferences(context: Context) {
    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)

    var nightMode: Boolean
        get() = preferences.getBoolean("night_mode", false)
        set(flag) {
            preferences.edit().putBoolean("night_mode", flag).apply()
        }
    var regex: Boolean
        get() = preferences.getBoolean("use_regex", false)
        set(flag) {
            preferences.edit().putBoolean("use_regex", flag).apply()
        }
}