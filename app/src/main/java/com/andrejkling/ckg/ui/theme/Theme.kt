package com.andrejkling.ckg.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// CKG is always dark, like the iOS app (.preferredColorScheme(.dark))
private val CKGColorScheme = darkColorScheme(
    primary = Accent,
    onPrimary = Unity,
    secondary = BlueTop,
    onSecondary = Color.White,
    background = Unity,
    onBackground = Color.White,
    surface = Unity,
    onSurface = Color.White,
    error = SavedRed
)

@Composable
fun CKGTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = CKGColorScheme,
        typography = Typography,
        content = content
    )
}
