package com.kiryha.noting.presentation.screens.notes

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.kiryha.noting.domain.model.Note
import com.kiryha.noting.presentation.components.NotingTopAppBar
import com.kiryha.noting.presentation.navigation.SettingScreen

@Composable
fun SendNoteScreen(
    note: Note,
    navController: NavController,
    viewModel: NoteViewModel,
) {
    val username by mutableStateOf("")
    val targetUsername by mutableStateOf("")

    LaunchedEffect(username) {
        
    }

    Scaffold(
        topBar = {
            NotingTopAppBar(
                titleText = "Notes",
                showSettingsButton = true,
                onSettingsClick = { navController.navigate(SettingScreen) }
            )
        }
    ) {
        Column() {

        }
    }
}