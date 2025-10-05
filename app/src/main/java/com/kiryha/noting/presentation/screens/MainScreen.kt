package com.kiryha.noting.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.kiryha.noting.domain.status.NoteStatus
import com.kiryha.noting.presentation.components.HorizontalButton
import com.kiryha.noting.presentation.components.NoteItem
import com.kiryha.noting.presentation.components.NotingTopAppBar
import com.kiryha.noting.presentation.navigation.NoteScreen
import com.kiryha.noting.presentation.navigation.SettingScreen
import com.kiryha.noting.presentation.viewmodel.NoteViewModel
import kotlinx.coroutines.launch

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
    val searchText by viewModel.searchText.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
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

            }
        }
        Column(Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(horizontal = 15.dp)) {
            TextField(
                value = searchText,
                onValueChange = viewModel::onSearchTextChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp)
                    .clip(RoundedCornerShape(100)),
                placeholder = { Text("Search")},
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    unfocusedPlaceholderColor = MaterialTheme.colorScheme.secondary,
                    focusedPlaceholderColor = MaterialTheme.colorScheme.secondary,
                    focusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    unfocusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                singleLine = true
            )
            Spacer(Modifier.height(20.dp))
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                verticalItemSpacing = 4.dp,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(1f),
                content = {
                    items(notes.item) { note ->
                        NoteItem(
                            note = note,
                            onNoteClick = { navController.navigate(NoteScreen(note.id)) },
                            onEditClick = { navController.navigate(NoteScreen(note.id)) }, // Переход на экран редактирования
                            onDeleteClick = {
                                viewModel.deleteNote(note.id) // Удаление заметки
                                scope.launch {
                                    snackbarHostState.showSnackbar("Заметка удалена")
                                }
                            }
                        )
                    }
                    item { Spacer(Modifier.height(100.dp)) }
                    item { Spacer(Modifier.height(100.dp)) }
                }
            )
        }


        HorizontalButton(
            onClick = { navController.navigate(NoteScreen()) },
            innerPadding = innerPadding,
            text = "New Note"
        )

    }
}