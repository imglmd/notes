package com.kiryha.noting.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.kiryha.noting.presentation.screens.MainScreen
import com.kiryha.noting.presentation.screens.NoteScreen
import com.kiryha.noting.presentation.screens.SettingScreen
import com.kiryha.noting.presentation.viewmodel.NoteViewModel
import kotlinx.serialization.Serializable

@Serializable
object MainScreen

@Serializable
data class NoteScreen(
    val noteId: Int? = null
)

@Serializable
object SettingScreen

@Composable
fun SetupNavGraph(
    navController: NavHostController,
    viewModel: NoteViewModel
) {
    NavHost(
        navController = navController,
        startDestination = MainScreen
    ) {
        composable<MainScreen> {
            MainScreen(navController = navController, viewModel = viewModel)
        }
        composable<NoteScreen> {
            val args = it.toRoute<NoteScreen>()
            NoteScreen(navController = navController, noteId = args.noteId, viewModel = viewModel)
        }
        composable<SettingScreen> {
            SettingScreen(navController = navController)
        }
    }
}