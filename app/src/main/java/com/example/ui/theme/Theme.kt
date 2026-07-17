package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryDark,
    secondary = SecondaryDark,
    tertiary = TertiaryDark,
    background = BackgroundDark,
    surface = SurfaceDark,
    onBackground = OnBackgroundDark,
    onSurface = OnSurfaceDark
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryLight,
    secondary = SecondaryLight,
    tertiary = TertiaryLight,
    background = BackgroundLight,
    surface = SurfaceLight,
    onBackground = OnBackgroundLight,
    onSurface = OnSurfaceLight
)

fun getCustomColorScheme(preset: String, isDark: Boolean): ColorScheme {
    val primary = when (preset) {
        "emerald" -> if (isDark) Color(0xFF34D399) else Color(0xFF059669)
        "ocean" -> if (isDark) Color(0xFF38BDF8) else Color(0xFF0284C7)
        "rose" -> if (isDark) Color(0xFFF472B6) else Color(0xFFDB2777)
        "sunset" -> if (isDark) Color(0xFFFBBF24) else Color(0xFFD97706)
        "amethyst" -> if (isDark) Color(0xFFA78BFA) else Color(0xFF7C3AED)
        else -> if (isDark) PrimaryDark else PrimaryLight // "indigo"
    }
    
    val secondary = when (preset) {
        "emerald" -> if (isDark) Color(0xFF6EE7B7) else Color(0xFF10B981)
        "ocean" -> if (isDark) Color(0xFF67E8F9) else Color(0xFF06B6D4)
        "rose" -> if (isDark) Color(0xFFFB7185) else Color(0xFFF43F5E)
        "sunset" -> if (isDark) Color(0xFFFDE047) else Color(0xFFF59E0B)
        "amethyst" -> if (isDark) Color(0xFFC4B5FD) else Color(0xFF8B5CF6)
        else -> if (isDark) SecondaryDark else SecondaryLight // "indigo"
    }

    return if (isDark) {
        darkColorScheme(
            primary = primary,
            secondary = secondary,
            tertiary = TertiaryDark,
            background = BackgroundDark,
            surface = SurfaceDark,
            onBackground = OnBackgroundDark,
            onSurface = OnSurfaceDark
        )
    } else {
        lightColorScheme(
            primary = primary,
            secondary = secondary,
            tertiary = TertiaryLight,
            background = BackgroundLight,
            surface = SurfaceLight,
            onBackground = OnBackgroundLight,
            onSurface = OnSurfaceLight
        )
    }
}

@Composable
fun MyApplicationTheme(
    preset: String = "indigo",
    themeMode: String = "system",
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val useDark = when (themeMode) {
        "light" -> false
        "dark" -> true
        else -> darkTheme
    }

    val colorScheme = getCustomColorScheme(preset, useDark)

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
