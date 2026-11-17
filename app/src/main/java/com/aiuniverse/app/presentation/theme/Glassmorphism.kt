package com.aiuniverse.app.presentation.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Custom Compose Modifier providing ultra-modern frosted glass effect with
 * subtle luminous gradient edge borders.
 */
fun Modifier.glassmorphic(
    backgroundColor: Color = Color(0x181F293D),
    borderColor: Color = Color(0x288EA6D1),
    borderWidth: Dp = 1.dp,
    shape: Shape = RoundedCornerShape(16.dp)
): Modifier = composed {
    this
        .clip(shape)
        .background(backgroundColor)
        .border(
            width = borderWidth,
            brush = Brush.verticalGradient(
                colors = listOf(
                    borderColor,
                    borderColor.copy(alpha = 0.05f)
                )
            ),
            shape = shape
        )
}

fun Modifier.cosmicGlow(
    glowColor: Color = CosmicViolet,
    cornerRadius: Dp = 16.dp
): Modifier = composed {
    this.border(
        width = 1.dp,
        brush = Brush.horizontalGradient(
            colors = listOf(
                glowColor.copy(alpha = 0.6f),
                ElectricCyan.copy(alpha = 0.3f),
                glowColor.copy(alpha = 0.1f)
            )
        ),
        shape = RoundedCornerShape(cornerRadius)
    )
}
