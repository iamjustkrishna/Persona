package com.krishnajeena.persona.model

import androidx.lifecycle.ViewModel
import com.krishnajeena.persona.data_layer.AppTheme
import com.krishnajeena.persona.data_layer.ThemeManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class ThemeState(
    val currentTheme: AppTheme = AppTheme.WARM_SUNSET,
    val isDarkMode: Boolean = false,
    val useSystemTheme: Boolean = false
)

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val themeManager: ThemeManager
) : ViewModel() {

    private val _themeState = MutableStateFlow(
        ThemeState(
            currentTheme = themeManager.getTheme(),
            isDarkMode = themeManager.isDarkMode(),
            useSystemTheme = themeManager.useSystemTheme()
        )
    )
    val themeState: StateFlow<ThemeState> = _themeState.asStateFlow()

    fun setTheme(theme: AppTheme) {
        themeManager.saveTheme(theme)
        _themeState.value = _themeState.value.copy(currentTheme = theme)
    }

    fun setDarkMode(isDark: Boolean) {
        themeManager.saveDarkMode(isDark)
        themeManager.saveUseSystemTheme(false)
        _themeState.value = _themeState.value.copy(
            isDarkMode = isDark,
            useSystemTheme = false
        )
    }

    fun toggleDarkMode() {
        val newDarkMode = !_themeState.value.isDarkMode
        setDarkMode(newDarkMode)
    }

    fun setUseSystemTheme(useSystem: Boolean) {
        themeManager.saveUseSystemTheme(useSystem)
        _themeState.value = _themeState.value.copy(useSystemTheme = useSystem)
    }

    fun updateSystemDarkMode(isSystemDark: Boolean) {
        if (_themeState.value.useSystemTheme) {
            themeManager.saveDarkMode(isSystemDark)
            _themeState.value = _themeState.value.copy(isDarkMode = isSystemDark)
        }
    }
}
