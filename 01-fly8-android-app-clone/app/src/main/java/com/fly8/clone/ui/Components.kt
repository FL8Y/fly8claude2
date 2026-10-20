package com.fly8.clone.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/** A standard FLY8 panel card with a subtle outline. The clean surface used across every screen. */
@Composable
fun Panel(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val base = modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(18.dp))
        .background(Fly8Panel.copy(alpha = 0.55f))
        .border(1.dp, Fly8Line.copy(alpha = 0.6f), RoundedCornerShape(18.dp))
    Box(if (onClick != null) base.clickable { onClick() } else base) {
        Column(Modifier.padding(16.dp)) { content() }
    }
}

@Composable
fun SectionLabel(text: String) {
    Text(
        text.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = Fly8Muted,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
    )
}

/** A soft horizontal progress meter (storage / RAM) with an animated fill. */
@Composable
fun Meter(fraction: Float, accent: Color = Fly8Primary, modifier: Modifier = Modifier) {
    val f = fraction.coerceIn(0f, 1f)
    Box(
        modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(Fly8Secondary.copy(alpha = 0.5f))
    ) {
        Box(
            Modifier
                .fillMaxWidth(f)
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(Brush.horizontalGradient(listOf(accent.copy(alpha = 0.7f), accent)))
        )
    }
}

/** A gentle shimmer used on preview placeholders — the "clean generic animation". */
@Composable
fun ShimmerBox(modifier: Modifier = Modifier, corner: Int = 12) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val alpha by transition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(tween(1300), RepeatMode.Reverse),
        label = "alpha"
    )
    Box(
        modifier
            .clip(RoundedCornerShape(corner.dp))
            .background(
                Brush.linearGradient(
                    listOf(Fly8Secondary.copy(alpha = alpha), Fly8Panel.copy(alpha = alpha * 0.8f))
                )
            )
    )
}

/** A small pill chip showing a status with a dot. */
@Composable
fun StatusChip(text: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(100))
            .background(color.copy(alpha = 0.14f))
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Box(Modifier.size(7.dp).clip(RoundedCornerShape(100)).background(color))
        Spacer(Modifier.size(6.dp))
        Text(text, color = color, style = MaterialTheme.typography.labelSmall)
    }
}

/** Expand/collapse wrapper with a clean fade + slide. */
@Composable
fun Reveal(visible: Boolean, content: @Composable () -> Unit) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(220)) + expandVertically(tween(220)),
        exit = fadeOut(tween(160)) + shrinkVertically(tween(160))
    ) { content() }
}

@Composable
fun ScreenTitle(text: String, trailing: (@Composable () -> Unit)? = null) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(
            text,
            style = MaterialTheme.typography.titleLarge,
            color = Fly8Text,
            modifier = Modifier.weight(1f)
        )
        trailing?.invoke()
    }
    Spacer(Modifier.height(4.dp))
}

@Composable
fun PillButton(text: String, onClick: () -> Unit, enabled: Boolean = true) {
    val c = if (enabled) Fly8Primary else Fly8Muted
    Text(
        text,
        color = Fly8Text,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier
            .clip(RoundedCornerShape(100))
            .background(c.copy(alpha = if (enabled) 0.9f else 0.3f))
            .then(if (enabled) Modifier.clickable { onClick() } else Modifier)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
fun KeyValue(key: String, value: String, valueColor: Color = Fly8Text) {
    Row(Modifier.fillMaxWidth().padding(vertical = 3.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(key, color = Fly8Muted, style = MaterialTheme.typography.bodySmall)
        Text(value, color = valueColor, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
    }
}
