package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val StudioDarkColorScheme = darkColorScheme(
    primary = StudioTeal,
    onPrimary = StudioInk,
    primaryContainer = StudioTealContainer,
    onPrimaryContainer = StudioTeal,
    secondary = StudioAmber,
    onSecondary = StudioInk,
    secondaryContainer = StudioAmberContainer,
    onSecondaryContainer = StudioAmber,
    tertiary = StudioTealDim,
    onTertiary = StudioTextPrimary,
    background = StudioInk,
    onBackground = StudioTextPrimary,
    surface = StudioPanel,
    onSurface = StudioTextPrimary,
    surfaceVariant = StudioPanelElevated,
    onSurfaceVariant = StudioTextMuted,
    surfaceContainerLowest = StudioInk,
    surfaceContainerLow = StudioPanel,
    surfaceContainer = StudioPanel,
    surfaceContainerHigh = StudioPanelElevated,
    surfaceContainerHighest = StudioSurfaceHighest,
    outline = StudioBorder,
    outlineVariant = StudioBorderSubtle,
    surfaceTint = StudioTeal,
    scrim = StudioInk.copy(alpha = 0.85f),
    error = StudioError,
    onError = StudioInk,
    errorContainer = StudioErrorContainer,
    onErrorContainer = StudioError
)

private val StudioShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(14.dp),
    large = RoundedCornerShape(18.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

@Composable
fun CdpS100Theme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = StudioDarkColorScheme,
        typography = StudioTypography,
        shapes = StudioShapes,
        content = content
    )
}
