package com.kiryha.noting.presentation.screens

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.kiryha.noting.domain.model.Note
import com.kiryha.noting.domain.status.NoteStatus
import com.kiryha.noting.presentation.components.NotingTopAppBar
import com.kiryha.noting.presentation.navigation.MainScreen
import com.kiryha.noting.presentation.viewmodel.NoteViewModel
import com.kiryha.noting.utils.SwipeDirection
import com.kiryha.noting.utils.swipeToAction
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.absoluteValue

@Composable
fun NoteScreen(
    noteId: Int? = null,
    navController: NavController,
    viewModel: NoteViewModel,
    modifier: Modifier = Modifier,
    ) {
    val selectedNote by viewModel.selectedNote.collectAsState()
    val status by viewModel.status.collectAsState()

    var noteText by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")


    LaunchedEffect(noteId, selectedNote) {
        if (noteId != null && noteId != -1) {
            viewModel.getNote(noteId)
            if (selectedNote.item.id == noteId) {
                noteText = selectedNote.item.text
            }
        }
    }

    Scaffold(
        topBar = {
            NotingTopAppBar(
                titleText = if (noteId == null) "New Note" else "Edit Note",
                showBackButton = true,
                onBackClick = { navController.popBackStack()}
            )
        },
        modifier = Modifier.swipeToAction(
            direction = SwipeDirection.Right,
            onSwipe = { navController.popBackStack() }
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
                        .wrapContentSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(40.dp)),
                    ) {
                    TextField(
                        value = noteText,
                        onValueChange = { noteText = it },
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            unfocusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(400.dp)
                            .padding()
                            .clip(RoundedCornerShape(5.dp))
                            .verticalScroll(rememberScrollState()),
                        maxLines = Int.MAX_VALUE,
                        textStyle = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(Modifier.height(5.dp))
                    Button(
                        onClick = {
                            if (isSaving) return@Button

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
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 70.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        shape = RoundedCornerShape(0.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                    ) {
                        Text(text = if (noteId == null) "Save Note" else "Update Note", style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}