package com.kiryha.noting

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.kiryha.noting.data.source.local.NoteDatabase
import com.kiryha.noting.data.NoteRepository
import com.kiryha.noting.data.source.network.NetworkDataSource
import com.kiryha.noting.presentation.navigation.SetupNavGraph
import com.kiryha.noting.presentation.viewmodel.NoteViewModel
import com.kiryha.noting.theme.NotingTheme
import com.kiryha.noting.utils.PreferencesManager
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

class MainActivity : ComponentActivity() {

    val supabase = createSupabaseClient(
        supabaseUrl = "https://wdfurlzsegcgywhyrybn.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6IndkZnVybHpzZWdjZ3l3aHlyeWJuIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjAxODI4MDcsImV4cCI6MjA3NTc1ODgwN30.SYOyoxRJWP1aqO0YB5azYQ7K3NhZJwpKRRELSq2twws"
    ) {
        install(Auth)
        install(Postgrest)
    }

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
                    val networkSource = NetworkDataSource(supabase)
                    val repository = NoteRepository(db.noteDao, networkSource, applicationContext)
                    return NoteViewModel(repository) as T
                }
            }
        }
    )

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var currentTheme by remember { mutableStateOf(PreferencesManager.getThemeMode(this)) }
            NotingTheme(
                themeMode = currentTheme
            ) {
                val navController = rememberNavController()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SetupNavGraph(
                        navController = navController,
                        viewModel = viewModel,
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