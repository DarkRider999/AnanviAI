package com.aiuniverse.app.presentation.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.aiuniverse.app.presentation.theme.CosmicViolet
import com.aiuniverse.app.presentation.theme.ElectricCyan
import com.aiuniverse.app.presentation.theme.NeonEmerald

@Composable
fun AudioVisualizerWave(
    isRecording: Boolean,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "audio_bars")

    val h1 by transition.animateFloat(
        initialValue = 8f,
        targetValue = if (isRecording) 36f else 8f,
        animationSpec = infiniteRepeatable(tween(400, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "h1"
    )
    val h2 by transition.animateFloat(
        initialValue = 12f,
        targetValue = if (isRecording) 48f else 12f,
        animationSpec = infiniteRepeatable(tween(300, delayMillis = 100, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "h2"
    )
    val h3 by transition.animateFloat(
        initialValue = 6f,
        targetValue = if (isRecording) 42f else 6f,
        animationSpec = infiniteRepeatable(tween(350, delayMillis = 50, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "h3"
    )
    val h4 by transition.animateFloat(
        initialValue = 10f,
        targetValue = if (isRecording) 32f else 10f,
        animationSpec = infiniteRepeatable(tween(450, delayMillis = 150, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "h4"
    )

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.width(4.dp).height(h1.dp).clip(RoundedCornerShape(2.dp)).background(CosmicViolet))
        Box(modifier = Modifier.width(4.dp).height(h2.dp).clip(RoundedCornerShape(2.dp)).background(ElectricCyan))
        Box(modifier = Modifier.width(4.dp).height(h3.dp).clip(RoundedCornerShape(2.dp)).background(NeonEmerald))
        Box(modifier = Modifier.width(4.dp).height(h4.dp).clip(RoundedCornerShape(2.dp)).background(CosmicViolet))
    }
}
