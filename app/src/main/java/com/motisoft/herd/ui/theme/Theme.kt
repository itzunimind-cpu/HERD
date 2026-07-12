package com.motisoft.herd.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// Single light scheme only - the design is not built for dark mode.
private val HerdColorScheme = lightColorScheme(
    primary = Terracotta,
    onPrimary = Cream,
    secondary = Sage,
    onSecondary = DarkBrown,
    background = Cream,
    onBackground = DarkBrown,
    surface = White,
    onSurface = DarkBrown,
    surfaceVariant = White,
    onSurfaceVariant = SupportingText,
    outline = DarkBrown,
    error = Terracotta,
)

@Composable
fun HerdTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = HerdColorScheme,
        typography = HerdTypography,
        shapes = HerdShapes,
        content = content,
    )
}
