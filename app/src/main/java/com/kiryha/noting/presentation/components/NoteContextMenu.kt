package com.kiryha.noting.presentation.components

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
        DropdownMenuItem(
            text = {
                Text(
                    if (isPinned) "Unpin" else "Pin",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            },
            leadingIcon = {
                Icon(
                    painter = if (isPinned) painterResource(R.drawable.unpin) else painterResource(R.drawable.bookmark),
                    contentDescription = null, modifier = Modifier.size(22.dp))
            },
            onClick = {
                onPinClick()
                onDismissRequest()
            },
        )
        DropdownMenuItem(
            text = {
                Text(
                    "Edit",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            },
            leadingIcon = {
                Icon(painter = painterResource(R.drawable.edit), contentDescription = null, modifier = Modifier.size(22.dp))
            },
            onClick = {
                onEditClick()
                onDismissRequest()
            },
        )
        DropdownMenuItem(
            text = {
                Text(
                    "Delete",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            },
            leadingIcon = {
                Icon(painter = painterResource(R.drawable.trash), contentDescription = null, modifier = Modifier.size(22.dp))
            },
            onClick = {
                onDeleteClick()
                onDismissRequest()
            },
        )
    }
}