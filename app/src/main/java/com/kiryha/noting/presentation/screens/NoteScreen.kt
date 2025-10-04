package com.kiryha.noting.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.kiryha.noting.MainScreen
import com.kiryha.noting.domain.status.NoteStatus
import com.kiryha.noting.domain.model.Note
import com.kiryha.noting.presentation.viewmodel.NoteViewModel
import com.kiryha.noting.presentation.components.NotingTopAppBar

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
                onBackClick = { navController.popBackStack() }
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
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp)
                ) {
                    OutlinedTextField(
                        value = noteText,
                        onValueChange = { noteText = it },

                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)
                            .padding(vertical = 8.dp),
                        maxLines = Int.MAX_VALUE
                    )
                    Button(
                        onClick = {
                            val note = if (noteId == null) {
                                Note(
                                    text = noteText,
                                    date = System.currentTimeMillis().toString()
                                )
                            } else {
                                Note(id = noteId, text = noteText, date = selectedNote.item.date)
                            }
                            viewModel.upsertNote(note)
                            navController.navigate(MainScreen)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 30.dp)
                            .heightIn(min = 70.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.background),
                        shape = RoundedCornerShape(20.dp)
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