package com.harvestpay.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.harvestpay.app.data.ThemePreference
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF256B3B),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFB8F2C6),
    onPrimaryContainer = Color(0xFF00210C),
    secondary = Color(0xFF725B00),
    secondaryContainer = Color(0xFFFFE16B),
    tertiary = Color(0xFF386668),
    background = Color(0xFFF9F8F3),
    surface = Color(0xFFFFFBFF),
    surfaceVariant = Color(0xFFE0E4DC),
    error = Color(0xFFBA1A1A),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF8FE3A3),
    onPrimary = Color(0xFF00210A),
    primaryContainer = Color(0xFF123B1D),
    onPrimaryContainer = Color(0xFFB2F4C1),
    secondary = Color(0xFFF0CB52),
    onSecondary = Color(0xFF3B2F00),
    secondaryContainer = Color(0xFF4E3E00),
    onSecondaryContainer = Color(0xFFFFE68A),
    tertiary = Color(0xFF8FD3CE),
    onTertiary = Color(0xFF003735),
    background = Color(0xFF030604),
    onBackground = Color(0xFFEAF1EA),
    surface = Color(0xFF060A07),
    onSurface = Color(0xFFEAF1EA),
    surfaceVariant = Color(0xFF182019),
    onSurfaceVariant = Color(0xFFBEC9BF),
    surfaceContainerLowest = Color(0xFF010302),
    surfaceContainerLow = Color(0xFF070C08),
    surfaceContainer = Color(0xFF0B110C),
    surfaceContainerHigh = Color(0xFF101812),
    surfaceContainerHighest = Color(0xFF172019),
    outline = Color(0xFF849087),
    outlineVariant = Color(0xFF354039),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
)

@Composable
fun HarvestPayTheme(preference: ThemePreference, content: @Composable () -> Unit) {
    val dark = when (preference) {
        ThemePreference.SYSTEM -> isSystemInDarkTheme()
        ThemePreference.LIGHT -> false
        ThemePreference.DARK -> true
    }
    MaterialTheme(
        colorScheme = if (dark) DarkColors else LightColors,
        typography = androidx.compose.material3.Typography(),
        content = content,
    )
}
