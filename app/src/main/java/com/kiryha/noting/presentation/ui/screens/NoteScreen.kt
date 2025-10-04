package com.kiryha.noting.presentation.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kiryha.noting.presentation.ui.components.NotingTopAppBar

@Composable
fun NoteScreen(
    modifier: Modifier = Modifier,
    isEdit: Boolean = false,
) {
    NotingTopAppBar(
        titleText = if (isEdit) "Edit Note" else "New Note",
        showBackButton = true
    )
}