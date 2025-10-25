package com.kiryha.noting.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.kiryha.noting.presentation.components.NotingTopAppBar
import com.kiryha.noting.presentation.components.ProfileSection
import com.kiryha.noting.presentation.components.RadioButtonGroup
import com.kiryha.noting.presentation.viewmodel.AuthViewModel
import com.kiryha.noting.presentation.viewmodel.NoteViewModel
import com.kiryha.noting.theme.ThemeMode
import com.kiryha.noting.utils.PreferencesManager
import com.kiryha.noting.utils.SwipeDirection
import com.kiryha.noting.utils.swipeToAction

@Composable
fun SettingScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    onThemeChanged: (ThemeMode) -> Unit,
    noteViewModel: NoteViewModel,
    authViewModel: AuthViewModel
) {
    val context = LocalContext.current
    val currentTheme = PreferencesManager.getThemeMode(context)
    var saveLocation by remember { mutableStateOf("local") }

    Scaffold(
        topBar = {
            NotingTopAppBar(
                titleText = "",
                showBackButton = true,
                onBackClick = { navController.popBackStack() }
            )
        },
        modifier = Modifier.swipeToAction(
            direction = SwipeDirection.Right,
            onSwipe = { navController.popBackStack() }
        )
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            ProfileSection(navController, authViewModel)

            Spacer(Modifier.height(40.dp))

            Text(text = "Settings", style = MaterialTheme.typography.titleLarge)

            RadioButtonGroup(

                label = "data storage",
                options = listOf("cloud", "local"),
                selectedOption = saveLocation,
                onOptionSelected = { location -> saveLocation = location },
                optionToTextStyle = { option ->
                    when (option) {
                        "cloud" -> "Cloud (Supabase)" to null
                        "local" -> "Local Device" to null
                        else -> option to null
                    }
                    },
                )
            RadioButtonGroup(
                label = "theme",
                options = listOf(ThemeMode.Light, ThemeMode.System, ThemeMode.Dark),
                selectedOption = currentTheme,
                onOptionSelected = { newTheme ->
                    PreferencesManager.saveThemeMode(context, newTheme)
                    onThemeChanged(newTheme)
                },
                optionToTextStyle = { theme ->
                    when (theme) {
                        ThemeMode.Light -> "Светлая" to null
                        ThemeMode.System -> "Системная" to null
                        ThemeMode.Dark -> "Темная" to null
                    }
                }
            )
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "add test notes",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.clickable(
                        onClick = { noteViewModel.addTestNotes() },
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ).padding(horizontal = 15.dp, vertical = 10.dp)
                )
                Text(
                    "delete all notes",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.clickable(
                        onClick = { noteViewModel.clearAllNotes() },
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ).padding(horizontal = 15.dp, vertical = 10.dp)
                )
            }

        }
    }
}