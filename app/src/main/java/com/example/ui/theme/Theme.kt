package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkGamingColorScheme = darkColorScheme(
    primary = NeonGreenPrimary,
    onPrimary = NeonGreenOnPrimary,
    primaryContainer = NeonGreenContainer,
    onPrimaryContainer = NeonGreenOnContainer,
    secondary = CyberCyan,
    onSecondary = Color(0xFF00363E),
    secondaryContainer = CyberCyanContainer,
    onSecondaryContainer = CyberCyanOnContainer,
    tertiary = NeonGreenBright,
    onTertiary = Color(0xFF003A0C),
    background = CyberBlack,
    onBackground = TextHighEmphasis,
    surface = CyberSurface,
    onSurface = TextHighEmphasis,
    surfaceVariant = CyberSurfaceVariant,
    onSurfaceVariant = TextMediumEmphasis,
    outline = CyberOutline,
    outlineVariant = CyberOutlineVariant,
    error = CyberRed,
    onError = Color.White
)

private val LightGamingColorScheme = lightColorScheme(
    primary = Color(0xFF008945),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFA1FBBF),
    onPrimaryContainer = Color(0xFF00210A),
    secondary = Color(0xFF006876),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFA1EFFF),
    onSecondaryContainer = Color(0xFF001F25),
    tertiary = Color(0xFF2E7D32),
    onTertiary = Color.White,
    background = Color(0xFFF3F7F4),
    onBackground = Color(0xFF131A15),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF131A15),
    surfaceVariant = Color(0xFFE0EAE3),
    onSurfaceVariant = Color(0xFF3C4942),
    outline = Color(0xFFB5C5BC),
    outlineVariant = Color(0xFFD3E0D8),
    error = Color(0xFFBA1A1A),
    onError = Color.White
)

@Composable
fun WordCounterTheme(
    darkTheme: Boolean = true, // Default to sleek gaming dark mode
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkGamingColorScheme else LightGamingColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
