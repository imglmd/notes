package com.kiryha.noting

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.kiryha.noting.data.model.Note
import com.kiryha.noting.presentation.ui.screens.MainScreen
import com.kiryha.noting.presentation.ui.screens.NoteScreen
import com.kiryha.noting.presentation.ui.screens.SettingScreen
import com.kiryha.noting.presentation.ui.theme.NotingTheme
import kotlinx.serialization.Serializable

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NotingTheme {
                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = MainScreen
                    ) {
                        composable<MainScreen> {
                            MainScreen(navController = navController)
                        }
                        composable<NoteScreen> {
                            val args = it.toRoute<NoteScreen>()
                            NoteScreen(navController = navController, isEdit = args.isEdit)
                        }
                        composable<SettingScreen> {
                            SettingScreen(navController = navController)
                        }
                    }
                }
            }
        }
    }
}


@Serializable
object MainScreen

@Serializable
data class NoteScreen(
    val isEdit: Boolean
)

@Serializable
object SettingScreen