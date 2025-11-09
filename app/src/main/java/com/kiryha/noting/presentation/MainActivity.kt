package com.kiryha.noting.presentation

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.kiryha.noting.presentation.navigation.SetupNavGraph
import com.kiryha.noting.presentation.screens.auth.AuthViewModel
import com.kiryha.noting.presentation.screens.notes.NoteViewModel
import com.kiryha.noting.presentation.theme.NotingTheme
import com.kiryha.noting.utils.PreferencesManager
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    private val noteViewModel: NoteViewModel by viewModel()
    private val authViewModel: AuthViewModel by viewModel()

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()
        setContent {
            var currentTheme by remember { mutableStateOf(PreferencesManager.getThemeMode(this@MainActivity)) }
            NotingTheme(
                themeMode = currentTheme
            ) {
                val navController = rememberNavController()

                Scaffold(modifier = Modifier.Companion.fillMaxSize()) { innerPadding ->
                    SetupNavGraph(
                        navController = navController,
                        noteViewModel = noteViewModel,
                        authViewModel = authViewModel,
                        currentTheme = currentTheme,
                        onThemeChanged = { newTheme ->
                            currentTheme = newTheme
                            PreferencesManager.saveThemeMode(this@MainActivity, newTheme)
                        }
                    )
                }
            }
        }
    }
}