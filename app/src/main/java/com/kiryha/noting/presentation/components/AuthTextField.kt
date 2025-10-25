package com.kiryha.noting.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun AuthTextField(
    label: String,
    value: String,
    errorMessage: String? = null,
    onValueChange: (String) -> Unit
) {
    Row {
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(start = 15.dp),
        )
        Text(
            text = errorMessage ?: " ",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Right,
            modifier = Modifier.padding(start = 15.dp).weight(1f),
        )
    }

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.height(55.dp).fillMaxWidth().padding(vertical = 5.dp),
        textStyle = MaterialTheme.typography.labelLarge.copy(color = MaterialTheme.colorScheme.primary),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        singleLine = true,

        decorationBox = { innerTextField ->
            Row(
                Modifier.background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(100))
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                innerTextField()
            }
        },
    )
}