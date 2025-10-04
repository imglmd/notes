package com.kiryha.noting.presentation.screens

import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.kiryha.noting.NoteScreen
import com.kiryha.noting.domain.status.NoteStatus

import com.kiryha.noting.SettingScreen
import com.kiryha.noting.presentation.viewmodel.NoteViewModel
import com.kiryha.noting.presentation.components.HorizontalButton
import com.kiryha.noting.presentation.components.NoteItem
import com.kiryha.noting.presentation.components.NotingTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavController,
    viewModel: NoteViewModel,
    modifier: Modifier = Modifier.fillMaxSize()
) {
    val notes by viewModel.notes.collectAsState()
    val status by viewModel.status.collectAsState()
    val selectedNote by viewModel.selectedNote.collectAsState()

    Scaffold(
        topBar = {
            NotingTopAppBar(
                titleText = "Notes",
                showSettingsButton = true,
                onSettingsClick = { navController.navigate(SettingScreen) }
            )
        },

    ) { innerPadding ->
        when (status) {
            is NoteStatus.Failure -> {
                Text(
                    text = "Error loading notes",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }
            is NoteStatus.Deleted -> {
                LaunchedEffect(status) {
                    viewModel.loadNotes()
                }
            }
            else -> {
                // No action needed for Success
            }
        }

        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            verticalItemSpacing = 4.dp,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(innerPadding).padding(horizontal = 15.dp),
            content = {
                items(notes.item){ note ->
                    NoteItem(
                        note = note,
                        onNoteClick = { navController.navigate(NoteScreen(note.id)) }
                    )
                }
                item { Spacer(Modifier.height(100.dp)) }
                item { Spacer(Modifier.height(100.dp)) }
            }
        )
        HorizontalButton(
            onClick = { navController.navigate(NoteScreen()) },
            innerPadding = innerPadding,
            text = "New Note"
        )

    }
}