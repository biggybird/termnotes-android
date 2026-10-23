package com.termnotes.ui

import android.content.Context

/** Small, offline-only preference store. No network or third-party storage is used. */
object AppPreferences {
    private const val FILE = "termnotes_preferences"
    private const val KEY_THEME = "theme"
    private const val KEY_FONT = "font"
    private const val KEY_CRT = "crt_enabled"
    private const val KEY_SOUND = "sound_enabled"

    fun theme(context: Context): String = store(context).getString(KEY_THEME, "Hacker Green") ?: "Hacker Green"
    fun font(context: Context): String = store(context).getString(KEY_FONT, "System Monospace") ?: "System Monospace"
    fun crt(context: Context): Boolean = store(context).getBoolean(KEY_CRT, false)
    fun sound(context: Context): Boolean = store(context).getBoolean(KEY_SOUND, false)

    fun setTheme(context: Context, value: String) { store(context).edit().putString(KEY_THEME, value).apply() }
    fun setFont(context: Context, value: String) { store(context).edit().putString(KEY_FONT, value).apply() }
    fun setCrt(context: Context, value: Boolean) { store(context).edit().putBoolean(KEY_CRT, value).apply() }
    fun setSound(context: Context, value: Boolean) { store(context).edit().putBoolean(KEY_SOUND, value).apply() }

    private fun store(context: Context) = context.getSharedPreferences(FILE, Context.MODE_PRIVATE)
}
