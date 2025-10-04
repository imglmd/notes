package com.kiryha.noting

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import androidx.room.Room
import com.kiryha.noting.data.database.NoteDatabase
import com.kiryha.noting.data.repository.NoteRepository
import com.kiryha.noting.presentation.viewmodel.NoteViewModel
import com.kiryha.noting.presentation.screens.MainScreen
import com.kiryha.noting.presentation.screens.NoteScreen
import com.kiryha.noting.presentation.screens.SettingScreen
import com.kiryha.noting.theme.NotingTheme
import kotlinx.serialization.Serializable
import kotlin.getValue

class MainActivity : ComponentActivity() {

    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            NoteDatabase::class.java,
            "notes.db"
        ).build()
    }

    private val viewModel by viewModels<NoteViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return NoteViewModel(NoteRepository(db.noteDao)) as T
                }
            }
        }
    )

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
            }
        }
    }
}


@Serializable
object MainScreen

@Serializable
data class NoteScreen(
    val noteId: Int? = null
)

@Serializable
object SettingScreen