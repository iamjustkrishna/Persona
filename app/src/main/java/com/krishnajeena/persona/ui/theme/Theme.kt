package com.krishnajeena.persona.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.krishnajeena.persona.data_layer.AppTheme

// Warm Sunset Schemes
private val warmSunsetLightScheme = lightColorScheme(
    primary = WarmSunsetLight.primary,
    onPrimary = WarmSunsetLight.onPrimary,
    primaryContainer = WarmSunsetLight.primaryContainer,
    onPrimaryContainer = WarmSunsetLight.onPrimaryContainer,
    secondary = WarmSunsetLight.secondary,
    onSecondary = WarmSunsetLight.onSecondary,
    secondaryContainer = WarmSunsetLight.secondaryContainer,
    onSecondaryContainer = WarmSunsetLight.onSecondaryContainer,
    tertiary = WarmSunsetLight.tertiary,
    onTertiary = WarmSunsetLight.onTertiary,
    tertiaryContainer = WarmSunsetLight.tertiaryContainer,
    onTertiaryContainer = WarmSunsetLight.onTertiaryContainer,
    background = WarmSunsetLight.background,
    onBackground = WarmSunsetLight.onBackground,
    surface = WarmSunsetLight.surface,
    onSurface = WarmSunsetLight.onSurface,
    surfaceVariant = WarmSunsetLight.surfaceVariant,
    onSurfaceVariant = WarmSunsetLight.onSurfaceVariant,
    outline = WarmSunsetLight.outline,
    outlineVariant = WarmSunsetLight.outlineVariant,
    error = WarmSunsetLight.error
)

private val warmSunsetDarkScheme = darkColorScheme(
    primary = WarmSunsetDark.primary,
    onPrimary = WarmSunsetDark.onPrimary,
    primaryContainer = WarmSunsetDark.primaryContainer,
    onPrimaryContainer = WarmSunsetDark.onPrimaryContainer,
    secondary = WarmSunsetDark.secondary,
    onSecondary = WarmSunsetDark.onSecondary,
    secondaryContainer = WarmSunsetDark.secondaryContainer,
    onSecondaryContainer = WarmSunsetDark.onSecondaryContainer,
    tertiary = WarmSunsetDark.tertiary,
    onTertiary = WarmSunsetDark.onTertiary,
    tertiaryContainer = WarmSunsetDark.tertiaryContainer,
    onTertiaryContainer = WarmSunsetDark.onTertiaryContainer,
    background = WarmSunsetDark.background,
    onBackground = WarmSunsetDark.onBackground,
    surface = WarmSunsetDark.surface,
    onSurface = WarmSunsetDark.onSurface,
    surfaceVariant = WarmSunsetDark.surfaceVariant,
    onSurfaceVariant = WarmSunsetDark.onSurfaceVariant,
    outline = WarmSunsetDark.outline,
    outlineVariant = WarmSunsetDark.outlineVariant,
    error = WarmSunsetDark.error
)

// Midnight Purple Schemes
private val midnightPurpleLightScheme = lightColorScheme(
    primary = MidnightPurpleLight.primary,
    onPrimary = MidnightPurpleLight.onPrimary,
    primaryContainer = MidnightPurpleLight.primaryContainer,
    onPrimaryContainer = MidnightPurpleLight.onPrimaryContainer,
    secondary = MidnightPurpleLight.secondary,
    onSecondary = MidnightPurpleLight.onSecondary,
    secondaryContainer = MidnightPurpleLight.secondaryContainer,
    onSecondaryContainer = MidnightPurpleLight.onSecondaryContainer,
    tertiary = MidnightPurpleLight.tertiary,
    onTertiary = MidnightPurpleLight.onTertiary,
    tertiaryContainer = MidnightPurpleLight.tertiaryContainer,
    onTertiaryContainer = MidnightPurpleLight.onTertiaryContainer,
    background = MidnightPurpleLight.background,
    onBackground = MidnightPurpleLight.onBackground,
    surface = MidnightPurpleLight.surface,
    onSurface = MidnightPurpleLight.onSurface,
    surfaceVariant = MidnightPurpleLight.surfaceVariant,
    onSurfaceVariant = MidnightPurpleLight.onSurfaceVariant,
    outline = MidnightPurpleLight.outline,
    outlineVariant = MidnightPurpleLight.outlineVariant,
    error = MidnightPurpleLight.error
)

private val midnightPurpleDarkScheme = darkColorScheme(
    primary = MidnightPurpleDark.primary,
    onPrimary = MidnightPurpleDark.onPrimary,
    primaryContainer = MidnightPurpleDark.primaryContainer,
    onPrimaryContainer = MidnightPurpleDark.onPrimaryContainer,
    secondary = MidnightPurpleDark.secondary,
    onSecondary = MidnightPurpleDark.onSecondary,
    secondaryContainer = MidnightPurpleDark.secondaryContainer,
    onSecondaryContainer = MidnightPurpleDark.onSecondaryContainer,
    tertiary = MidnightPurpleDark.tertiary,
    onTertiary = MidnightPurpleDark.onTertiary,
    tertiaryContainer = MidnightPurpleDark.tertiaryContainer,
    onTertiaryContainer = MidnightPurpleDark.onTertiaryContainer,
    background = MidnightPurpleDark.background,
    onBackground = MidnightPurpleDark.onBackground,
    surface = MidnightPurpleDark.surface,
    onSurface = MidnightPurpleDark.onSurface,
    surfaceVariant = MidnightPurpleDark.surfaceVariant,
    onSurfaceVariant = MidnightPurpleDark.onSurfaceVariant,
    outline = MidnightPurpleDark.outline,
    outlineVariant = MidnightPurpleDark.outlineVariant,
    error = MidnightPurpleDark.error
)

// Ocean Teal Schemes
private val oceanTealLightScheme = lightColorScheme(
    primary = OceanTealLight.primary,
    onPrimary = OceanTealLight.onPrimary,
    primaryContainer = OceanTealLight.primaryContainer,
    onPrimaryContainer = OceanTealLight.onPrimaryContainer,
    secondary = OceanTealLight.secondary,
    onSecondary = OceanTealLight.onSecondary,
    secondaryContainer = OceanTealLight.secondaryContainer,
    onSecondaryContainer = OceanTealLight.onSecondaryContainer,
    tertiary = OceanTealLight.tertiary,
    onTertiary = OceanTealLight.onTertiary,
    tertiaryContainer = OceanTealLight.tertiaryContainer,
    onTertiaryContainer = OceanTealLight.onTertiaryContainer,
    background = OceanTealLight.background,
    onBackground = OceanTealLight.onBackground,
    surface = OceanTealLight.surface,
    onSurface = OceanTealLight.onSurface,
    surfaceVariant = OceanTealLight.surfaceVariant,
    onSurfaceVariant = OceanTealLight.onSurfaceVariant,
    outline = OceanTealLight.outline,
    outlineVariant = OceanTealLight.outlineVariant,
    error = OceanTealLight.error
)

private val oceanTealDarkScheme = darkColorScheme(
    primary = OceanTealDark.primary,
    onPrimary = OceanTealDark.onPrimary,
    primaryContainer = OceanTealDark.primaryContainer,
    onPrimaryContainer = OceanTealDark.onPrimaryContainer,
    secondary = OceanTealDark.secondary,
    onSecondary = OceanTealDark.onSecondary,
    secondaryContainer = OceanTealDark.secondaryContainer,
    onSecondaryContainer = OceanTealDark.onSecondaryContainer,
    tertiary = OceanTealDark.tertiary,
    onTertiary = OceanTealDark.onTertiary,
    tertiaryContainer = OceanTealDark.tertiaryContainer,
    onTertiaryContainer = OceanTealDark.onTertiaryContainer,
    background = OceanTealDark.background,
    onBackground = OceanTealDark.onBackground,
    surface = OceanTealDark.surface,
    onSurface = OceanTealDark.onSurface,
    surfaceVariant = OceanTealDark.surfaceVariant,
    onSurfaceVariant = OceanTealDark.onSurfaceVariant,
    outline = OceanTealDark.outline,
    outlineVariant = OceanTealDark.outlineVariant,
    error = OceanTealDark.error
)

// Legacy schemes (for backwards compatibility)
private val lightScheme = lightColorScheme(
    primary = PrimaryIndigo,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0E7FF), // Soft Indigo wash
    onPrimaryContainer = PrimaryIndigo,
    secondary = SecondarySlate,
    onSecondary = Color.White,
    tertiary = TertiaryGold,
    background = backgroundLight,
    onBackground = onSurfaceLight,
    surface = surfaceLight,
    onSurface = onSurfaceLight,
    surfaceVariant = surfaceVariantLight,
    onSurfaceVariant = SecondarySlate,
    outline = Color(0xFFCBD5E1),
    outlineVariant = Color(0xFFE2E8F0)
)

private val darkScheme = darkColorScheme(
    primary = Color(0xFF818CF8), // Lighter Indigo for dark mode
    onPrimary = Color(0xFF1E293B),
    primaryContainer = Color(0xFF312E81),
    onPrimaryContainer = Color(0xFFE0E7FF),
    secondary = Color(0xFF94A3B8),
    background = backgroundDark,
    onBackground = onSurfaceDark,
    surface = surfaceDark,
    onSurface = onSurfaceDark,
    surfaceVariant = surfaceVariantDark,
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = Color(0xFF475569),
    outlineVariant = Color(0xFF334155)
)

private val mediumContrastLightColorScheme = lightColorScheme(
    primary = primaryLightMediumContrast,
    onPrimary = onPrimaryLightMediumContrast,
    primaryContainer = primaryContainerLightMediumContrast,
    onPrimaryContainer = onPrimaryContainerLightMediumContrast,
    secondary = secondaryLightMediumContrast,
    onSecondary = onSecondaryLightMediumContrast,
    secondaryContainer = secondaryContainerLightMediumContrast,
    onSecondaryContainer = onSecondaryContainerLightMediumContrast,
    tertiary = tertiaryLightMediumContrast,
    onTertiary = onTertiaryLightMediumContrast,
    tertiaryContainer = tertiaryContainerLightMediumContrast,
    onTertiaryContainer = onTertiaryContainerLightMediumContrast,
    error = errorLightMediumContrast,
    onError = onErrorLightMediumContrast,
    errorContainer = errorContainerLightMediumContrast,
    onErrorContainer = onErrorContainerLightMediumContrast,
    background = backgroundLightMediumContrast,
    onBackground = onBackgroundLightMediumContrast,
    surface = surfaceLightMediumContrast,
    onSurface = onSurfaceLightMediumContrast,
    surfaceVariant = surfaceVariantLightMediumContrast,
    onSurfaceVariant = onSurfaceVariantLightMediumContrast,
    outline = outlineLightMediumContrast,
    outlineVariant = outlineVariantLightMediumContrast,
    scrim = scrimLightMediumContrast,
    inverseSurface = inverseSurfaceLightMediumContrast,
    inverseOnSurface = inverseOnSurfaceLightMediumContrast,
    inversePrimary = inversePrimaryLightMediumContrast,
    surfaceDim = surfaceDimLightMediumContrast,
    surfaceBright = surfaceBrightLightMediumContrast,
    surfaceContainerLowest = surfaceContainerLowestLightMediumContrast,
    surfaceContainerLow = surfaceContainerLowLightMediumContrast,
    surfaceContainer = surfaceContainerLightMediumContrast,
    surfaceContainerHigh = surfaceContainerHighLightMediumContrast,
    surfaceContainerHighest = surfaceContainerHighestLightMediumContrast,
)

private val highContrastLightColorScheme = lightColorScheme(
    primary = primaryLightHighContrast,
    onPrimary = onPrimaryLightHighContrast,
    primaryContainer = primaryContainerLightHighContrast,
    onPrimaryContainer = onPrimaryContainerLightHighContrast,
    secondary = secondaryLightHighContrast,
    onSecondary = onSecondaryLightHighContrast,
    secondaryContainer = secondaryContainerLightHighContrast,
    onSecondaryContainer = onSecondaryContainerLightHighContrast,
    tertiary = tertiaryLightHighContrast,
    onTertiary = onTertiaryLightHighContrast,
    tertiaryContainer = tertiaryContainerLightHighContrast,
    onTertiaryContainer = onTertiaryContainerLightHighContrast,
    error = errorLightHighContrast,
    onError = onErrorLightHighContrast,
    errorContainer = errorContainerLightHighContrast,
    onErrorContainer = onErrorContainerLightHighContrast,
    background = backgroundLightHighContrast,
    onBackground = onBackgroundLightHighContrast,
    surface = surfaceLightHighContrast,
    onSurface = onSurfaceLightHighContrast,
    surfaceVariant = surfaceVariantLightHighContrast,
    onSurfaceVariant = onSurfaceVariantLightHighContrast,
    outline = outlineLightHighContrast,
    outlineVariant = outlineVariantLightHighContrast,
    scrim = scrimLightHighContrast,
    inverseSurface = inverseSurfaceLightHighContrast,
    inverseOnSurface = inverseOnSurfaceLightHighContrast,
    inversePrimary = inversePrimaryLightHighContrast,
    surfaceDim = surfaceDimLightHighContrast,
    surfaceBright = surfaceBrightLightHighContrast,
    surfaceContainerLowest = surfaceContainerLowestLightHighContrast,
    surfaceContainerLow = surfaceContainerLowLightHighContrast,
    surfaceContainer = surfaceContainerLightHighContrast,
    surfaceContainerHigh = surfaceContainerHighLightHighContrast,
    surfaceContainerHighest = surfaceContainerHighestLightHighContrast,
)

private val mediumContrastDarkColorScheme = darkColorScheme(
    primary = primaryDarkMediumContrast,
    onPrimary = onPrimaryDarkMediumContrast,
    primaryContainer = primaryContainerDarkMediumContrast,
    onPrimaryContainer = onPrimaryContainerDarkMediumContrast,
    secondary = secondaryDarkMediumContrast,
    onSecondary = onSecondaryDarkMediumContrast,
    secondaryContainer = secondaryContainerDarkMediumContrast,
    onSecondaryContainer = onSecondaryContainerDarkMediumContrast,
    tertiary = tertiaryDarkMediumContrast,
    onTertiary = onTertiaryDarkMediumContrast,
    tertiaryContainer = tertiaryContainerDarkMediumContrast,
    onTertiaryContainer = onTertiaryContainerDarkMediumContrast,
    error = errorDarkMediumContrast,
    onError = onErrorDarkMediumContrast,
    errorContainer = errorContainerDarkMediumContrast,
    onErrorContainer = onErrorContainerDarkMediumContrast,
    background = backgroundDarkMediumContrast,
    onBackground = onBackgroundDarkMediumContrast,
    surface = surfaceDarkMediumContrast,
    onSurface = onSurfaceDarkMediumContrast,
    surfaceVariant = surfaceVariantDarkMediumContrast,
    onSurfaceVariant = onSurfaceVariantDarkMediumContrast,
    outline = outlineDarkMediumContrast,
    outlineVariant = outlineVariantDarkMediumContrast,
    scrim = scrimDarkMediumContrast,
    inverseSurface = inverseSurfaceDarkMediumContrast,
    inverseOnSurface = inverseOnSurfaceDarkMediumContrast,
    inversePrimary = inversePrimaryDarkMediumContrast,
    surfaceDim = surfaceDimDarkMediumContrast,
    surfaceBright = surfaceBrightDarkMediumContrast,
    surfaceContainerLowest = surfaceContainerLowestDarkMediumContrast,
    surfaceContainerLow = surfaceContainerLowDarkMediumContrast,
    surfaceContainer = surfaceContainerDarkMediumContrast,
    surfaceContainerHigh = surfaceContainerHighDarkMediumContrast,
    surfaceContainerHighest = surfaceContainerHighestDarkMediumContrast,
)

private val highContrastDarkColorScheme = darkColorScheme(
    primary = primaryDarkHighContrast,
    onPrimary = onPrimaryDarkHighContrast,
    primaryContainer = primaryContainerDarkHighContrast,
    onPrimaryContainer = onPrimaryContainerDarkHighContrast,
    secondary = secondaryDarkHighContrast,
    onSecondary = onSecondaryDarkHighContrast,
    secondaryContainer = secondaryContainerDarkHighContrast,
    onSecondaryContainer = onSecondaryContainerDarkHighContrast,
    tertiary = tertiaryDarkHighContrast,
    onTertiary = onTertiaryDarkHighContrast,
    tertiaryContainer = tertiaryContainerDarkHighContrast,
    onTertiaryContainer = onTertiaryContainerDarkHighContrast,
    error = errorDarkHighContrast,
    onError = onErrorDarkHighContrast,
    errorContainer = errorContainerDarkHighContrast,
    onErrorContainer = onErrorContainerDarkHighContrast,
    background = backgroundDarkHighContrast,
    onBackground = onBackgroundDarkHighContrast,
    surface = surfaceDarkHighContrast,
    onSurface = onSurfaceDarkHighContrast,
    surfaceVariant = surfaceVariantDarkHighContrast,
    onSurfaceVariant = onSurfaceVariantDarkHighContrast,
    outline = outlineDarkHighContrast,
    outlineVariant = outlineVariantDarkHighContrast,
    scrim = scrimDarkHighContrast,
    inverseSurface = inverseSurfaceDarkHighContrast,
    inverseOnSurface = inverseOnSurfaceDarkHighContrast,
    inversePrimary = inversePrimaryDarkHighContrast,
    surfaceDim = surfaceDimDarkHighContrast,
    surfaceBright = surfaceBrightDarkHighContrast,
    surfaceContainerLowest = surfaceContainerLowestDarkHighContrast,
    surfaceContainerLow = surfaceContainerLowDarkHighContrast,
    surfaceContainer = surfaceContainerDarkHighContrast,
    surfaceContainerHigh = surfaceContainerHighDarkHighContrast,
    surfaceContainerHighest = surfaceContainerHighestDarkHighContrast,
)

@Immutable
data class ColorFamily(
    val color: Color,
    val onColor: Color,
    val colorContainer: Color,
    val onColorContainer: Color
)

val unspecified_scheme = ColorFamily(
    Color.Unspecified, Color.Unspecified, Color.Unspecified, Color.Unspecified
)

@Composable
fun PersonaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    appTheme: AppTheme = AppTheme.WARM_SUNSET,
    content: @Composable () -> Unit
) {
    val colorScheme = when (appTheme) {
        AppTheme.WARM_SUNSET -> if (darkTheme) warmSunsetDarkScheme else warmSunsetLightScheme
        AppTheme.MIDNIGHT_PURPLE -> if (darkTheme) midnightPurpleDarkScheme else midnightPurpleLightScheme
        AppTheme.OCEAN_TEAL -> if (darkTheme) oceanTealDarkScheme else oceanTealLightScheme
    }

    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            window?.let {
                // This is the CRITICAL line for edge-to-edge
                WindowCompat.setDecorFitsSystemWindows(it, false)

                it.statusBarColor = Color.Transparent.toArgb()
                it.navigationBarColor = Color.Transparent.toArgb()

                val controller = WindowCompat.getInsetsController(it, view)
                controller.isAppearanceLightStatusBars = !darkTheme
                controller.isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}

