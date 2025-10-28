package com.kiryha.noting.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.kiryha.noting.domain.model.Note
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun PinnedNoteItem(
    note: Note,
    onNoteClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onPinClick: () -> Unit,
    round: Int = 16
) {
    var isContextMenuVisible by rememberSaveable { mutableStateOf(false) }
    var pressOffset by remember { mutableStateOf(DpOffset.Zero) }
    var itemHeight by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current

    val displayDate = remember(note.date) {
        try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = inputFormat.parse(note.date)
            val outputFormat = SimpleDateFormat("MM.dd")
            outputFormat.format(date)
        } catch (e: Exception) {
            note.date
        }
    }

    Box(
        modifier = Modifier
            .onSizeChanged { itemHeight = with(density) { it.height.toDp() } }
            .padding(horizontal = 2.dp)
            .background(MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(round.dp))
            .pointerInput(true) {
                detectTapGestures(
                    onTap = { onNoteClick() },
                    onLongPress = {
                        isContextMenuVisible = true
                        pressOffset = DpOffset(it.x.toDp(), it.y.toDp())
                    }
                )
            }.widthIn(max = 140.dp).heightIn(max = 140.dp),
    ) {
        Column(Modifier.fillMaxHeight().padding(top = 6.dp, bottom = 4.dp).padding(horizontal = 9.dp)) {
            Text(
                modifier = Modifier.weight(1f).bottomFade(),
                text = note.text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Text(
                text = displayDate,
                modifier = Modifier.align(Alignment.End),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary,
            )
        }
        NoteContextMenu(
            expanded = isContextMenuVisible,
            onDismissRequest = { isContextMenuVisible = false },
            offset = pressOffset.copy(y = pressOffset.y - itemHeight),
            isPinned = note.isPinned,
            onPinClick = onPinClick,
            onEditClick = onEditClick,
            onDeleteClick = onDeleteClick
        )
    }

}


fun Modifier.bottomFade(fadeHeight: Dp = 24.dp): Modifier = this.then(
    Modifier
        .graphicsLayer() { alpha = 0.99f }
        .drawWithContent {
            drawContent()
            val fadePx = fadeHeight.toPx()
            val colors = listOf(Color.Black, Color.Transparent)
            drawRect(
                brush = Brush.verticalGradient(
                    colors = colors,
                    startY = size.height - fadePx,
                    endY = size.height
                ),
                blendMode = BlendMode.DstIn
            )
        }
)