package com.andrejkling.ckg.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp

// Same background as the iOS Books, Music and Club tabs (TrianglesOverLappingEmpty / ClubBackground):
// a navy base with light rays drawn from each corner.
private val RayBase = Color(0xFF100031)
private val RayColor = Color(0xFF0BFFC1).copy(alpha = 0.2f)

@Composable
fun CKGBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(modifier = modifier.fillMaxSize().background(RayBase)) {
        Canvas(modifier = Modifier.fillMaxSize()) { drawRays() }
        content()
    }
}

private fun DrawScope.drawRays() {
    val w = size.width
    val h = size.height
    for (k in 0..5) {
        // iOS: width = max(maxWidth - 50k, 0), height = 50 + 50k (points)
        val triW = maxOf(w - 50.dp.toPx() * k, 0f)
        val triH = (50f + 50f * k).dp.toPx()

        // Bottom-left
        drawPath(Path().apply { moveTo(0f, h - triH); lineTo(triW, h); lineTo(0f, h); close() }, RayColor)
        // Top-left
        drawPath(Path().apply { moveTo(0f, triH); lineTo(triW, 0f); lineTo(0f, 0f); close() }, RayColor)
        // Bottom-right
        drawPath(Path().apply { moveTo(w, h - triH); lineTo(w - triW, h); lineTo(w, h); close() }, RayColor)
        // Top-right
        drawPath(Path().apply { moveTo(w, triH); lineTo(w - triW, 0f); lineTo(w, 0f); close() }, RayColor)
    }
}
