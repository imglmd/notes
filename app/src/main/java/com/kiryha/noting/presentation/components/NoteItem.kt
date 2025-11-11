package com.kiryha.noting.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.kiryha.noting.R
import com.kiryha.noting.domain.model.Note
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.NoteItem(
    note: Note,
    onNoteClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onPinClick: () -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope
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
            .sharedBounds(
                sharedContentState = rememberSharedContentState(key = note.id),
                animatedVisibilityScope = animatedVisibilityScope,

            )
            .background(
                MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(16.dp)
            )
            .pointerInput(true) {
                detectTapGestures(
                    onTap = { onNoteClick() },
                    onLongPress = {
                        isContextMenuVisible = true
                        pressOffset = DpOffset(it.x.toDp(), it.y.toDp())
                    }
                )
            }
            .padding(top = 11.dp, bottom = 3.dp)
            .padding(horizontal = 11.dp),
    ) {
        Column(Modifier.fillMaxWidth()) {
            Text(
                text = note.text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = displayDate,
                modifier = Modifier.align(Alignment.End),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary,
            )
        }

        AnimatedVisibility(
            visible = note.isPinned,
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut(),
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(
                        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.bookmark),
                    contentDescription = "Pinned",
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier
                        .size(16.dp)
                )
            }
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