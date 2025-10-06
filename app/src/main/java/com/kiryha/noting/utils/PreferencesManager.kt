package com.kiryha.noting.utils

import android.content.Context
import androidx.core.content.edit
import com.kiryha.noting.theme.ThemeMode

object PreferencesManager {
    private const val PREFS_NAME = "NotesPrefs"
    private const val KEY_THEME_MODE = "theme_mode"

    fun saveThemeMode(context: Context, themeMode: ThemeMode) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit{
            putString(KEY_THEME_MODE, themeMode.name)
        }
    }

    fun getThemeMode(context: Context): ThemeMode {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val themeName = prefs.getString(KEY_THEME_MODE, ThemeMode.System.name)
        return try {
            ThemeMode.valueOf(themeName ?: ThemeMode.System.name)
        } catch (e: IllegalArgumentException) {
            ThemeMode.System
        }
    }
}