package com.krishnajeena.persona.data_layer

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

enum class AppTheme(val displayName: String) {
    WARM_SUNSET("Warm Sunset"),
    MIDNIGHT_PURPLE("Midnight Purple"),
    OCEAN_TEAL("Ocean Teal");

    companion object {
        fun fromString(value: String): AppTheme {
            return values().find { it.name == value } ?: WARM_SUNSET
        }
    }
}

@Singleton
class ThemeManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("persona_theme_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_THEME = "app_theme"
        private const val KEY_DARK_MODE = "dark_mode"
    }

    fun saveTheme(theme: AppTheme) {
        prefs.edit().putString(KEY_THEME, theme.name).apply()
    }

    fun getTheme(): AppTheme {
        val themeName = prefs.getString(KEY_THEME, AppTheme.WARM_SUNSET.name) ?: AppTheme.WARM_SUNSET.name
        return AppTheme.fromString(themeName)
    }

    fun saveDarkMode(isDark: Boolean) {
        prefs.edit().putBoolean(KEY_DARK_MODE, isDark).apply()
    }

    fun isDarkMode(): Boolean {
        return prefs.getBoolean(KEY_DARK_MODE, false)
    }
}
