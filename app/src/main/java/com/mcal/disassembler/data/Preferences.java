package com.mcal.disassembler.data;

import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.mcal.disassembler.App;

import org.jetbrains.annotations.Contract;

public class Preferences {
    private static SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(App.getContext());

    public Preferences() {
        preferences = PreferenceManager.getDefaultSharedPreferences(App.getContext());
    }

    public static boolean isNightModeEnabled() {
        return preferences.getBoolean("night_mode", false);
    }

    public static void setNightModeEnabled(boolean flag) {
        preferences.edit().putBoolean("night_mode", flag).apply();
    }
}
