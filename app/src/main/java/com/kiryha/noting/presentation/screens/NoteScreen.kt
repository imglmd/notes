package com.kiryha.noting.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.kiryha.noting.domain.model.Note
import com.kiryha.noting.domain.status.NoteStatus
import com.kiryha.noting.presentation.components.NotingTopAppBar
import com.kiryha.noting.presentation.viewmodel.NoteViewModel
import com.kiryha.noting.utils.SwipeDirection
import com.kiryha.noting.utils.swipeToAction
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun NoteScreen(
    noteId: Int? = null,
    navController: NavController,
    viewModel: NoteViewModel,
    ) {
    val selectedNote by viewModel.selectedNote.collectAsState()
    val status by viewModel.status.collectAsState()

    var noteText by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(noteId, selectedNote) {
        if (noteId != null && noteId != -1) {
            viewModel.getNoteById(noteId)
            if (selectedNote.item.id == noteId) {
                noteText = selectedNote.item.text
            }
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    val onExit: () -> Unit = lambda@{
        if (isSaving) return@lambda

        isSaving = true

        val trimmedText = noteText.trim()
        if (trimmedText.isEmpty()) {
            if (noteId != null && noteId != -1) {
                viewModel.deleteNote(noteId)
            }
            navController.popBackStack()
        } else {
            val note = if (noteId == null) {
                Note(
                    text = trimmedText,
                    date = LocalDateTime.now().format(dateFormatter)
                )
            } else {
                Note(
                    id = noteId,
                    text = trimmedText,
                    date = selectedNote.item.date.ifEmpty {
                        LocalDateTime.now().format(dateFormatter)
                    }
                )
            }
            viewModel.upsertNote(note)
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            NotingTopAppBar(
                titleText = if (noteId == null) "New Note" else "Edit Note",
                showBackButton = true,
                onBackClick = onExit
            )
        },
        modifier = Modifier.swipeToAction(
            direction = SwipeDirection.Right,
            onSwipe = onExit
        )
    ) { innerPadding ->
        when (status) {
            is NoteStatus.Failure -> {
                Text(
                    text = "Error loading note",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp),
                    ) {
                    TextField(
                        value = noteText,
                        onValueChange = { noteText = it },
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = MaterialTheme.colorScheme.background,
                            focusedContainerColor = MaterialTheme.colorScheme.background,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            cursorColor = MaterialTheme.colorScheme.onSecondaryContainer,

                        ),
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)
                            .clip(RoundedCornerShape(5.dp))
                            .verticalScroll(rememberScrollState())
                            .focusRequester(focusRequester),
                        textStyle = MaterialTheme.typography.bodyLarge
                    )
                    Box(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                    ){
                        Column {
                            HorizontalDivider(color = MaterialTheme.colorScheme.secondary)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                NoteScreenButton(
                                    onClick = {
                                        if (noteId != null && noteId != -1) {
                                            viewModel.deleteNote(noteId)
                                        }
                                        navController.popBackStack()
                                    }, imageVector = Icons.Outlined.Delete)
                                NoteScreenButton(onClick = {}, imageVector = Icons.Outlined.Add)
                                NoteScreenButton(onClick = {}, imageVector = Icons.Outlined.Add)
                                NoteScreenButton(onClick = onExit, imageVector = Icons.Outlined.Done)

                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NoteScreenButton(
    onClick: () -> Unit,
    imageVector: ImageVector
) {
    IconButton(
        onClick = onClick,
    ) {
        Icon(
            imageVector = imageVector,
            tint = MaterialTheme.colorScheme.secondary,
            contentDescription = null
        )
    }
}