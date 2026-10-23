package com.termnotes.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

data class TermPalette(val background: Color, val panel: Color, val accent: Color, val muted: Color, val text: Color, val border: Color, val isLight: Boolean)

fun paletteFor(themeName: String): TermPalette = when (themeName) {
    "Amber CRT" -> TermPalette(Color(0xFF0A0700), Color(0xFF171006), Color(0xFFFFB000), Color(0xFFB58A38), Color(0xFFFFE7B0), Color(0xFF49330D), false)
    "Matrix Blue" -> TermPalette(Color(0xFF030712), Color(0xFF0B1220), Color(0xFF38BDF8), Color(0xFF7B9EB8), Color(0xFFE0F2FE), Color(0xFF18364A), false)
    "Retro White" -> TermPalette(Color(0xFFF4F1E8), Color(0xFFFFFFFF), Color(0xFF242424), Color(0xFF555555), Color(0xFF202020), Color(0xFFD0CCC1), true)
    else -> TermPalette(Color.Black, Color(0xFF0A0A0A), TermGreen, TermMuted, TermText, Color(0xFF183318), false)
}

@Composable
fun TermNotesTheme(themeName: String = "Hacker Green", content: @Composable () -> Unit) {
    val p = paletteFor(themeName)
    val scheme = if (p.isLight) lightColorScheme(primary = p.accent, onPrimary = Color.White, secondary = p.accent, onSecondary = Color.White, background = p.background, onBackground = p.text, surface = p.panel, onSurface = p.text, surfaceVariant = p.panel, onSurfaceVariant = p.muted, outline = p.border, error = Color(0xFFB3261E), onError = Color.White)
    else darkColorScheme(primary = p.accent, onPrimary = p.background, secondary = p.accent, onSecondary = p.background, background = p.background, onBackground = p.text, surface = p.panel, onSurface = p.text, surfaceVariant = p.panel, onSurfaceVariant = p.muted, outline = p.border, error = Color(0xFFFF6B6B), onError = p.background)
    MaterialTheme(colorScheme = scheme, typography = TermTypography, content = content)
}
