package com.kiryha.noting.utils


import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.absoluteValue

enum class SwipeDirection {
    Left,
    Right,
    Both
}

fun Modifier.swipeToAction(
    direction: SwipeDirection,
    threshold: Dp = 60.dp,
    onSwipe: () -> Unit
): Modifier = composed {
    val density = LocalDensity.current
    val swipeThreshold = with(density) { threshold.toPx() }
    var dragOffset by remember { mutableStateOf(0f) }

    pointerInput(Unit) {
        detectHorizontalDragGestures(
            onDragEnd = {
                val totalDrag = dragOffset
                if (totalDrag.absoluteValue > swipeThreshold) {
                    val isLeftSwipe = totalDrag < 0
                    val isRightSwipe = totalDrag > 0

                    when (direction) {
                        SwipeDirection.Left -> if (isLeftSwipe) onSwipe()
                        SwipeDirection.Right -> if (isRightSwipe) onSwipe()
                        SwipeDirection.Both -> {
                            if (isLeftSwipe || isRightSwipe) onSwipe()
                        }
                    }
                }
                dragOffset = 0f
            },
            onHorizontalDrag = { change, dragAmount ->
                change.consume()
                dragOffset += dragAmount
            }
        )
    }
}