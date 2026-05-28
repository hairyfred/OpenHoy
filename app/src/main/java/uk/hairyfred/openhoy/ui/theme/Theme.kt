package uk.hairyfred.openhoy.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val OpenHoyColors = lightColorScheme(
    primary = FeltGreen,
    onPrimary = Color.White,
    primaryContainer = SubtleSurface,
    onPrimaryContainer = Color.White,
    background = FeltGreen,
    onBackground = OnFelt,
    surface = FeltGreenDark,
    onSurface = OnFelt,
    surfaceVariant = SubtleSurface,
    onSurfaceVariant = OnFelt,
)

@Composable
fun OpenHoyTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = OpenHoyColors,
        typography = OpenHoyTypography,
        content = content,
    )
}
