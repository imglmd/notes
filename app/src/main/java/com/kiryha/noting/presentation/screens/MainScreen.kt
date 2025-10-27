package com.kiryha.noting.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.kiryha.noting.domain.model.NoteListItem
import com.kiryha.noting.domain.status.NoteStatus
import com.kiryha.noting.presentation.components.HorizontalButton
import com.kiryha.noting.presentation.components.NoteItem
import com.kiryha.noting.presentation.components.NotingTopAppBar
import com.kiryha.noting.presentation.navigation.EXPLODE_BOUNDS_KEY
import com.kiryha.noting.presentation.navigation.NoteScreen
import com.kiryha.noting.presentation.navigation.SettingScreen
import com.kiryha.noting.presentation.viewmodel.NoteViewModel
import com.kiryha.noting.utils.SwipeDirection
import com.kiryha.noting.utils.swipeToAction
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.MainScreen(
    navController: NavController,
    viewModel: NoteViewModel,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    val groupedNotes by viewModel.groupedNotes.collectAsState()
    val status by viewModel.status.collectAsState()
    val searchText by viewModel.searchText.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val coroutineScope = rememberCoroutineScope()
    var isRefreshing by remember { mutableStateOf(false) }
    val onRefresh: () -> Unit = {
        isRefreshing = true
        coroutineScope.launch {
            viewModel.syncNotes()
            delay(2000)
            isRefreshing = false
        }
    }

    val gridState = rememberLazyStaggeredGridState()
    val isSearchBarVisible by remember {
        derivedStateOf {
            gridState.firstVisibleItemIndex == 0
        }
    }
    Scaffold(
        topBar = {
            NotingTopAppBar(
                titleText = "Notes",
                showSettingsButton = true,
                onSettingsClick = { navController.navigate(SettingScreen) }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {navController.navigate(NoteScreen())},
                containerColor = MaterialTheme.colorScheme.background,
                modifier = Modifier.imePadding().sharedBounds(
                    sharedContentState = rememberSharedContentState(key = EXPLODE_BOUNDS_KEY),
                    animatedVisibilityScope = animatedVisibilityScope
                )
            ) {
                Text(
                    text = "Add Note",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 10.dp)
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
        ) {
            AnimatedVisibility(
                visible = !isSearchBarVisible and isSearching,
                enter = slideInVertically(
                    initialOffsetY = { fullHeight -> -fullHeight },
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300)),
                exit = slideOutVertically(
                    targetOffsetY = { fullHeight -> -fullHeight },
                    animationSpec = tween(300)
                ) + fadeOut(animationSpec = tween(300)),
                modifier = Modifier.zIndex(10f)
            ) {
                NoteSearchBar(
                    searchText,
                    viewModel,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(top = 5.dp)
                        .shadow(
                            elevation = 20.dp,
                            shape = RoundedCornerShape(20.dp),
                            clip = true
                        )
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                PullToRefreshBox(
                    onRefresh = onRefresh,
                    isRefreshing = isRefreshing,
                ) {
                    LazyVerticalStaggeredGrid(
                        state = gridState,
                        columns = StaggeredGridCells.Fixed(2),
                        verticalItemSpacing = 4.dp,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                        content = {
                            item(
                                key = "search_bar",
                                span = StaggeredGridItemSpan.FullLine
                            ) {
                                NoteSearchBar(searchText, viewModel)
                            }
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
            }
        }

        /*HorizontalButton(
            onClick = { navController.navigate(NoteScreen()) },
            innerPadding = innerPadding,
            text = "New Note"
        )*/
    }
}


@Composable
fun NoteSearchBar(
    searchText: String,
    viewModel: NoteViewModel,
    modifier: Modifier = Modifier
) {
    CompositionLocalProvider(
        LocalTextSelectionColors provides TextSelectionColors(
            handleColor = MaterialTheme.colorScheme.secondary,
            backgroundColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f)
        )
    ) {
        BasicTextField(
            value = searchText,
            onValueChange = viewModel::onSearchTextChange,
            modifier = modifier.height(40.dp),
            textStyle = MaterialTheme.typography.labelLarge.copy(color = MaterialTheme.colorScheme.primary),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.secondary),
            singleLine = true,
            decorationBox = { innerTextField ->
                Row(
                    Modifier.background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(20.dp))
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Search, tint = MaterialTheme.colorScheme.secondary, contentDescription = null)
                    Spacer(Modifier.width(16.dp))
                    innerTextField()
                }
            },
        )
    }
}