package com.andrejkling.ckg.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// The floating menu from the iOS FloatingTabbar: a round "+" at the bottom left.
// Tap it to open the four tabs; tap a tab or the dark area to close.
// Sizes scale with the screen height, so the open menu fits on small phones too.
private val TabAccent = Color(0xFFEC3C1A)     // selected tab colour (iOS)
private val TabBorder = Color(0xFF00FFFA)     // cyan ring (iOS)
private val TabNames = listOf("Books", "Music", "Aphorisms", "Club")

@Composable
fun FloatingTabBar(selected: Int, onSelect: (Int) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        // Bubble size and spacing: 81 dp and 24 dp on a normal phone, smaller on short screens
        val bubble: Dp = (maxHeight * 0.085f).coerceAtMost(81.dp)
        val gap: Dp = (maxHeight * 0.025f).coerceAtMost(24.dp)
        val labelSize = (bubble.value * 0.17f).sp

        if (expanded) {
            // Dark scrim: tapping it closes the menu
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xE600071F))
                    .clickable { expanded = false }
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .navigationBarsPadding()
                .padding(start = 21.dp, bottom = 21.dp),
            verticalArrangement = Arrangement.spacedBy(gap)
        ) {
            if (!expanded) {
                Bubble(size = bubble, onClick = { expanded = true }) {
                    Text("+", color = Color.White, fontSize = (bubble.value * 0.37f).sp, fontWeight = FontWeight.Light)
                }
            } else {
                Bubble(size = bubble, onClick = { expanded = false }) {
                    Text("✕", color = Color.White, fontSize = (bubble.value * 0.25f).sp)
                }
                TabNames.forEachIndexed { i, name ->
                    Bubble(size = bubble, onClick = {
                        onSelect(i)
                        expanded = false
                    }) {
                        Text(
                            text = name,
                            color = if (i == selected) TabAccent else Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = labelSize
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Bubble(size: Dp, onClick: () -> Unit, content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(Color(0xCC1A1E3D))
            .border(1.dp, TabBorder, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}
