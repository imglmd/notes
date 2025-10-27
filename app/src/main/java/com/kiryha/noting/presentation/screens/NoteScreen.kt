package com.kiryha.noting.presentation.screens

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
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
import com.kiryha.noting.presentation.navigation.EXPLODE_BOUNDS_KEY
import com.kiryha.noting.presentation.viewmodel.NoteViewModel
import com.kiryha.noting.theme.Red
import com.kiryha.noting.utils.SwipeDirection
import com.kiryha.noting.utils.swipeToAction
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.NoteScreen(
    noteId: Int? = null,
    navController: NavController,
    viewModel: NoteViewModel,
    animatedVisibilityScope: AnimatedVisibilityScope
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
            keyboardController?.hide()
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
            keyboardController?.hide()
            viewModel.upsertNote(note)
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            NotingTopAppBar(
                titleText = if (noteId == null) "New Note" else "Edit Note",
                showBackButton = true,
                onBackClick = onExit,
                backgroundColor = MaterialTheme.colorScheme.primaryContainer
            )
        },
        modifier = Modifier.swipeToAction(
            direction = SwipeDirection.Right,
            onSwipe = onExit
        ).sharedBounds(
            sharedContentState = rememberSharedContentState(key = EXPLODE_BOUNDS_KEY),
            animatedVisibilityScope = animatedVisibilityScope
        ),
        contentWindowInsets = WindowInsets()
    ) { innerPadding ->
        when (status) {
            is NoteStatus.Failure -> {
                Text(
                    text = "Error loading note",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp).padding(innerPadding)
                )
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .imePadding()
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp)
                        .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Bottom)),
                    ) {
                    TextField(
                        value = noteText,
                        onValueChange = { noteText = it },
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            cursorColor = MaterialTheme.colorScheme.onSecondaryContainer,

                        ),
                        modifier = Modifier
                            .clip(RoundedCornerShape(5.dp))
                            .weight(1f)
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .focusRequester(focusRequester),
                        textStyle = MaterialTheme.typography.bodyLarge
                    )
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