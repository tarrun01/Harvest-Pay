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
    primary = Color(0xFF9DD7AA),
    onPrimary = Color(0xFF06391A),
    primaryContainer = Color(0xFF0E5127),
    onPrimaryContainer = Color(0xFFB8F2C6),
    secondary = Color(0xFFE8C448),
    secondaryContainer = Color(0xFF574500),
    tertiary = Color(0xFFA0CFD1),
    background = Color(0xFF111411),
    surface = Color(0xFF191C19),
    surfaceVariant = Color(0xFF414941),
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
