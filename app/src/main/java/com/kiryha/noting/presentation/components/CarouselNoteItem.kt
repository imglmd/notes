package com.kiryha.noting.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.kiryha.noting.domain.model.Note
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun CarouselNoteItem(
    note: Note,
    onNoteClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onPinClick: () -> Unit
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
            .background(MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(16.dp))
            .pointerInput(true) {
                detectTapGestures(
                    onTap = { onNoteClick() },
                    onLongPress = {
                        isContextMenuVisible = true
                        pressOffset = DpOffset(it.x.toDp(), it.y.toDp())
                    }
                )
            }.height(160.dp).width(140.dp),
    ) {
        Column(Modifier.fillMaxSize().padding(6.dp)) {
            Text(
                modifier = Modifier.weight(1f),
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
        DropdownMenu(
            expanded = isContextMenuVisible,
            onDismissRequest = { isContextMenuVisible = false },
            offset = pressOffset.copy(
                y = pressOffset.y - itemHeight,
            ),
            shape = RoundedCornerShape(20.dp),
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ) {
            DropdownMenuItem(
                text = { Text("Edit", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onPrimaryContainer) },
                onClick = {
                    onEditClick()
                    isContextMenuVisible = false
                },
            )
            DropdownMenuItem(
                text = {
                    Text(if (note.isPinned) "Unpin" else "Pin",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer)
                },
                onClick = {
                    onPinClick()
                    isContextMenuVisible = false
                },
            )
            DropdownMenuItem(
                text = { Text("Delete", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onPrimaryContainer) },
                onClick = {
                    onDeleteClick()
                    isContextMenuVisible = false
                },
            )

        }
    }

}