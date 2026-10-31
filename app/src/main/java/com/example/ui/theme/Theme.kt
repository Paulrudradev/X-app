package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val NothingCmfColorScheme = darkColorScheme(
    primary = CmfOrange,
    onPrimary = Color.Black,
    primaryContainer = CmfSurface,
    onPrimaryContainer = CmfTextPrimary,
    secondary = EmeraldGreen,
    onSecondary = Color.Black,
    tertiary = SkyBlue,
    background = CmfDark,
    onBackground = CmfTextPrimary,
    surface = CmfCard,
    onSurface = CmfTextPrimary,
    surfaceVariant = CmfSurface,
    onSurfaceVariant = CmfMuted,
    outline = CmfBorder
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = NothingCmfColorScheme,
        typography = Typography,
        content = content
    )
}
