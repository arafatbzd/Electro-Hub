package com.example.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

data class IndustrialThemeColors(
    val isDark: Boolean,
    val accentPrimary: Color,
    val accentSecondary: Color,
    val accentSuccess: Color,
    val accentWarning: Color,
    val accentError: Color,
    val cardBackground: Color,
    val cardBorder: Color,
    val tagPrimaryBg: Color,
    val tagSecondaryBg: Color,
    val tagSuccessBg: Color,
    val tagWarningBg: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color
)

val DarkIndustrialThemeColors = IndustrialThemeColors(
    isDark = true,
    accentPrimary = ElectricCyan,
    accentSecondary = BlueprintBlue,
    accentSuccess = MachineGreen,
    accentWarning = SafetyAmber,
    accentError = EmergencyRed,
    cardBackground = Slate900,
    cardBorder = Slate800,
    tagPrimaryBg = ElectricCyanDim,
    tagSecondaryBg = Color(0x330284C7),
    tagSuccessBg = MachineGreenDim,
    tagWarningBg = SafetyAmberDim,
    textPrimary = Color.White,
    textSecondary = Slate300,
    textMuted = Slate400
)

val LightIndustrialThemeColors = IndustrialThemeColors(
    isDark = false,
    accentPrimary = BlueprintBlue, // #0284C7 - crisp CAD blue
    accentSecondary = Color(0xFF0369A1),
    accentSuccess = Color(0xFF15803D), // #15803D - high contrast forest green
    accentWarning = Color(0xFFC2410C), // #C2410C - rich burnt amber / safety orange
    accentError = Color(0xFFDC2626), // #DC2626 - clear dark red
    cardBackground = Color.White,
    cardBorder = Color(0xFFE2E8F0),
    tagPrimaryBg = Color(0xFFE0F2FE),
    tagSecondaryBg = Color(0xFFF1F5F9),
    tagSuccessBg = Color(0xFFDCFCE7),
    tagWarningBg = Color(0xFFFFF7ED),
    textPrimary = Color(0xFF0F172A),
    textSecondary = Color(0xFF334155),
    textMuted = Color(0xFF64748B)
)

val LocalIndustrialColors = staticCompositionLocalOf { DarkIndustrialThemeColors }
val LocalIsDarkTheme = staticCompositionLocalOf { true }

private val IndustrialDarkColorScheme = darkColorScheme(
    primary = ElectricCyan,
    onPrimary = Slate950,
    primaryContainer = Slate850,
    onPrimaryContainer = ElectricCyan,
    secondary = SafetyAmber,
    onSecondary = Slate950,
    secondaryContainer = Slate800,
    onSecondaryContainer = SafetyAmberLight,
    tertiary = BlueprintBlue,
    onTertiary = Color.White,
    background = Slate950,
    onBackground = Slate100,
    surface = Slate900,
    onSurface = Color.White,
    surfaceVariant = Slate850,
    onSurfaceVariant = Slate300,
    outline = Slate700,
    outlineVariant = Slate800,
    error = EmergencyRed,
    onError = Color.White
)

private val IndustrialLightColorScheme = lightColorScheme(
    primary = BlueprintBlue, // #0284C7
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0F2FE),
    onPrimaryContainer = Color(0xFF0369A1),
    secondary = Color(0xFFC2410C), // #C2410C - deep high-contrast industrial amber
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFF7ED),
    onSecondaryContainer = Color(0xFF9A3412),
    tertiary = Color(0xFF0369A1),
    onTertiary = Color.White,
    background = Color(0xFFF8FAFC), // Daylight crisp engineering paper background
    onBackground = Color(0xFF0F172A), // Dark slate text
    surface = Color.White, // Crisp pure white cards
    onSurface = Color(0xFF0F172A), // High contrast slate heading & text
    surfaceVariant = Color(0xFFF1F5F9), // Light technical surface
    onSurfaceVariant = Color(0xFF475569), // Muted dark slate for subtitles
    outline = Color(0xFFCBD5E1), // Clean crisp border in light mode
    outlineVariant = Color(0xFFE2E8F0),
    error = Color(0xFFDC2626),
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) IndustrialDarkColorScheme else IndustrialLightColorScheme
    val customColors = if (darkTheme) DarkIndustrialThemeColors else LightIndustrialThemeColors

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.surface.toArgb()
            window.navigationBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    CompositionLocalProvider(
        LocalIsDarkTheme provides darkTheme,
        LocalIndustrialColors provides customColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
