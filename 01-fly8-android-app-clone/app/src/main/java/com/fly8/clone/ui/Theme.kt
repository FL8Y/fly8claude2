package com.fly8.clone.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// FLY8 brand palette.
val Fly8Background = Color(0xFF000001)
val Fly8Surface = Color(0xFF010205)
val Fly8Panel = Color(0xFF01132A)
val Fly8Primary = Color(0xFF176E94)
val Fly8Secondary = Color(0xFF01304C)
val Fly8Text = Color(0xFFFCFDFD)
val Fly8Pink = Color(0xFFF5DADA)
val Fly8Muted = Color(0xFF626773)
val Fly8Line = Color(0xFF12324A)
val Fly8Good = Color(0xFF5AC9A8)

private val scheme = darkColorScheme(
    primary = Fly8Primary,
    onPrimary = Fly8Text,
    secondary = Fly8Secondary,
    onSecondary = Fly8Text,
    background = Fly8Background,
    onBackground = Fly8Text,
    surface = Fly8Surface,
    onSurface = Fly8Text,
    surfaceVariant = Fly8Panel,
    onSurfaceVariant = Fly8Muted,
    outline = Fly8Line,
    error = Fly8Pink,
    tertiary = Fly8Good
)

private val shapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp)
)

private val typography = Typography(
    titleLarge = Typography().titleLarge.copy(fontWeight = FontWeight.Bold, fontSize = 28.sp),
    titleMedium = Typography().titleMedium.copy(fontWeight = FontWeight.SemiBold),
    labelSmall = Typography().labelSmall.copy(fontSize = 11.sp, letterSpacing = 0.5.sp)
)

@Composable
fun Fly8Theme(content: @Composable () -> Unit) {
    @Suppress("UNUSED_EXPRESSION") isSystemInDarkTheme() // FLY8 is always dark.
    MaterialTheme(colorScheme = scheme, shapes = shapes, typography = typography, content = content)
}
