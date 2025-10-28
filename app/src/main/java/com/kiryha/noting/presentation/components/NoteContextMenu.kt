package com.kiryha.noting.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.kiryha.noting.R
import com.kiryha.noting.domain.model.Note

@Composable
fun NoteContextMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    offset: DpOffset,
    isPinned: Boolean,
    onPinClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        offset = offset,
        shape = RoundedCornerShape(20.dp),
        containerColor = MaterialTheme.colorScheme.primaryContainer,
    ) {
        ContextMenuItem(
            text = if (isPinned) "Unpin" else "Pin",
            iconRes = if (isPinned) R.drawable.unpin else R.drawable.bookmark,
            onClick = {
                onPinClick()
                onDismissRequest()
            }
        )
        ContextMenuItem(
            text = "Edit",
            iconRes = R.drawable.edit,
            onClick = {
                onEditClick()
                onDismissRequest()
            }
        )
        Divider(
            modifier = Modifier.padding(horizontal = 12.dp),
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
            thickness = 0.7.dp
        )
        ContextMenuItem(
            text = "Delete",
            iconRes = R.drawable.trash,
            onClick = {
                onDeleteClick()
                onDismissRequest()
            }
        )
    }
}

@Composable
fun ContextMenuItem(
    text: String,
    iconRes: Int,
    onClick: () -> Unit
) {
    DropdownMenuItem(
        text = {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.primary
            )
        },
        onClick = onClick,
        leadingIcon = {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
            )
        },
    )
}