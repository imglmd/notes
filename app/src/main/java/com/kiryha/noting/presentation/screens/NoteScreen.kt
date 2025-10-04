package com.kiryha.noting.presentation.screens

import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.DatePickerDefaults.dateFormatter
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.kiryha.noting.MainScreen
import com.kiryha.noting.domain.status.NoteStatus
import com.kiryha.noting.domain.model.Note
import com.kiryha.noting.presentation.viewmodel.NoteViewModel
import com.kiryha.noting.presentation.components.NotingTopAppBar
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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

    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

    LaunchedEffect(noteId) {
        if (noteId != null && noteId != -1) {
            viewModel.getNote(noteId)
        }
    }

    LaunchedEffect(selectedNote) {
        if (noteId != null && selectedNote.item.id == noteId) {
            noteText = selectedNote.item.text
        }
    }

    Scaffold(
        topBar = {
            NotingTopAppBar(
                titleText = if (noteId == null) "New Note" else "Edit Note",
                showBackButton = true,
                onBackClick = { navController.navigate(MainScreen) }
            )
        }
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
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(400.dp)
                            .padding()
                            .clip(RoundedCornerShape(5.dp))
                            .verticalScroll(rememberScrollState()),
                        maxLines = Int.MAX_VALUE,
                    )
                    Spacer(Modifier.height(5.dp))
                    Button(
                        onClick = {
                            val note = if (noteId == null) {
                                Note(
                                    text = noteText.trim(),
                                    date = LocalDateTime.now().format(dateFormatter)
                                )
                            } else {
                                Note(id = noteId, text = noteText.trim(), date = selectedNote.item.date)
                            }
                            if (noteText.isEmpty()) {
                                viewModel.deleteNote(note.id)
                            } else {
                                viewModel.upsertNote(note)
                            }
                            navController.navigate(MainScreen)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 70.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        shape = RoundedCornerShape(0.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
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