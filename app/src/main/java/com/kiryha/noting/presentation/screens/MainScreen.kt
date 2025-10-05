package com.kiryha.noting.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.kiryha.noting.domain.model.NoteListItem
import com.kiryha.noting.domain.status.NoteStatus
import com.kiryha.noting.presentation.components.HorizontalButton
import com.kiryha.noting.presentation.components.NoteItem
import com.kiryha.noting.presentation.components.NotingTopAppBar
import com.kiryha.noting.presentation.navigation.NoteScreen
import com.kiryha.noting.presentation.navigation.SettingScreen
import com.kiryha.noting.presentation.viewmodel.NoteViewModel
import com.kiryha.noting.utils.SwipeDirection
import com.kiryha.noting.utils.swipeToAction
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavController,
    viewModel: NoteViewModel,
    modifier: Modifier = Modifier.fillMaxSize()
) {
    val groupedNotes by viewModel.groupedNotes.collectAsState()
    val status by viewModel.status.collectAsState()
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
        modifier = Modifier.swipeToAction(
            direction = SwipeDirection.Left,
            onSwipe = { navController.navigate(NoteScreen())}
        )
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
            else -> {}
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
                .padding(horizontal = 15.dp)
        ) {
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                verticalItemSpacing = 4.dp,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(1f),
                content = {
                    item(span = StaggeredGridItemSpan.FullLine) { NoteSearchBar(searchText, viewModel) }
                    groupedNotes.item.forEach { listItem ->
                        when (listItem) {
                            is NoteListItem.MonthHeader -> {
                                item(
                                    key = "header_${listItem.key}",
                                    span = StaggeredGridItemSpan.FullLine
                                ) {
                                    Text(
                                        text = listItem.month,
                                        style = MaterialTheme.typography.titleLarge,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 25.dp, bottom = 5.dp, start = 4.dp)
                                    )
                                }
                            }
                            is NoteListItem.NoteItem -> {
                                item(key = "note_${listItem.note.id}") {
                                    NoteItem(
                                        note = listItem.note,
                                        onNoteClick = { navController.navigate(NoteScreen(listItem.note.id)) },
                                        onEditClick = { navController.navigate(NoteScreen(listItem.note.id)) },
                                        onDeleteClick = {
                                            viewModel.deleteNote(listItem.note.id)
                                            scope.launch {
                                                snackbarHostState.showSnackbar("Заметка удалена")
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                    item(span = StaggeredGridItemSpan.FullLine) { Spacer(Modifier.height(100.dp)) }
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


@Composable
fun NoteSearchBar(searchText: String, viewModel: NoteViewModel) {
    TextField(
        value = searchText,
        onValueChange = viewModel::onSearchTextChange,
        modifier = Modifier
            .fillMaxWidth()
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
}