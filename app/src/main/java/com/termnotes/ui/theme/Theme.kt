package com.termnotes.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val TermColorScheme = darkColorScheme(
    primary = TermGreen,
    onPrimary = Color.Black,
    secondary = TermGreenDark,
    onSecondary = Color.Black,
    background = Color.Black,
    onBackground = TermText,
    surface = Color.Black,
    onSurface = TermText,
    surfaceVariant = TermPanel,
    onSurfaceVariant = TermMuted,
    error = Color(0xFFFF6B6B),
    onError = Color.Black
)

@Composable
fun TermNotesTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = TermColorScheme, typography = TermTypography, content = content)
}
