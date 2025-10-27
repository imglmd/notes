package com.kiryha.noting.di

import android.content.Context
import androidx.room.Room
import com.kiryha.noting.data.AuthRepository
import com.kiryha.noting.data.NoteRepository
import com.kiryha.noting.data.source.local.DeletedNoteDao
import com.kiryha.noting.data.source.local.NoteDao
import com.kiryha.noting.data.source.local.NoteDatabase
import com.kiryha.noting.data.source.network.NetworkDataSource
import com.kiryha.noting.domain.usecase.ValidateEmail
import com.kiryha.noting.domain.usecase.ValidatePassword
import com.kiryha.noting.domain.usecase.ValidateUsername
import com.kiryha.noting.presentation.screens.auth.AuthViewModel
import com.kiryha.noting.presentation.screens.notes.NoteViewModel
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule = module {
    single {
        createSupabaseClient(
            supabaseUrl = "https://wdfurlzsegcgywhyrybn.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6IndkZnVybHpzZWdjZ3l3aHlyeWJuIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjAxODI4MDcsImV4cCI6MjA3NTc1ODgwN30.SYOyoxRJWP1aqO0YB5azYQ7K3NhZJwpKRRELSq2twws"
        ) {
            install(Auth)
            install(Postgrest)
        }
    }

    single {
        Room.databaseBuilder(
            androidContext(),
            NoteDatabase::class.java,
            "noting.db"
        ).build()
    }

    single<NoteDao> { get<NoteDatabase>().noteDao }
    single<DeletedNoteDao> { get<NoteDatabase>().deletedNoteDao }

    single { NetworkDataSource(get()) }

    single { NoteRepository(get(), get(), get(), get(), androidContext()) }
    single { AuthRepository(get())}

    viewModel { NoteViewModel(get(),) }
    viewModel { AuthViewModel(get(), get()) }
}