package com.kiryha.noting.di

import androidx.room.Room
import com.kiryha.noting.data.AuthRepositoryImpl
import com.kiryha.noting.data.NoteRepositoryImpl
import com.kiryha.noting.data.source.local.DeletedNoteDao
import com.kiryha.noting.data.source.local.NoteDao
import com.kiryha.noting.data.source.local.NoteDatabase
import com.kiryha.noting.data.source.network.NetworkDataSource
import com.kiryha.noting.domain.AuthRepository
import com.kiryha.noting.domain.NoteRepository
import com.kiryha.noting.domain.usecase.notes.ClearLocalDataUseCase
import com.kiryha.noting.domain.usecase.notes.GroupNotesByMonthUseCase
import com.kiryha.noting.domain.usecase.auth.LoadUserUseCase
import com.kiryha.noting.domain.usecase.notes.SyncNotesUseCase
import com.kiryha.noting.domain.usecase.auth.ValidateEmail
import com.kiryha.noting.domain.usecase.auth.ValidatePassword
import com.kiryha.noting.domain.usecase.auth.ValidateUsername
import com.kiryha.noting.presentation.screens.auth.AuthViewModel
import com.kiryha.noting.presentation.screens.notes.NoteViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

val networkModule: Module = module {
    single<SupabaseClient> {
        createSupabaseClient(
            supabaseUrl = "https://wdfurlzsegcgywhyrybn.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6IndkZnVybHpzZWdjZ3l3aHlyeWJuIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjAxODI4MDcsImV4cCI6MjA3NTc1ODgwN30.SYOyoxRJWP1aqO0YB5azYQ7K3NhZJwpKRRELSq2twws"
        ) {
            install(Auth)
            install(Postgrest)
        }
    }

    single { NetworkDataSource(supabase = get()) }
}

val databaseModule: Module = module {
    single {
        Room.databaseBuilder(
            context = androidContext(),
            klass = NoteDatabase::class.java,
            name = "noting.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    single<NoteDao> {
        get<NoteDatabase>().noteDao
    }

    single<DeletedNoteDao> {
        get<NoteDatabase>().deletedNoteDao
    }
}

val repositoryModule: Module = module {
    single<NoteRepository> {
        NoteRepositoryImpl(
            noteDao = get(),
            deletedNoteDao = get(),
            networkSource = get(),
            authRepository = get(),
            context = androidContext(),
        )
    }

    single<AuthRepository> {
        AuthRepositoryImpl(
            networkSource = get()
        )
    }
}

val useCaseModule: Module = module {
    factory { ValidateEmail() }
    factory { ValidatePassword() }
    factory { ValidateUsername() }

    factory {
        ClearLocalDataUseCase(
            repository = get()
        )
    }

    factory {
        LoadUserUseCase(
            repository = get()
        )
    }

    factory {
        SyncNotesUseCase(
            noteRepository = get()
        )
    }
    factory { GroupNotesByMonthUseCase() }
}

val viewModelModule: Module = module {
    viewModel {
        AuthViewModel(
            repository = get(),
            syncNotesUseCase = get(),
            loadUserUseCase = get(),
            validateEmail = get(),
            validatePassword = get(),
            validateUsername = get(),
            clearLocalDataUseCase = get(),
        )
    }

    viewModel {
        NoteViewModel(
            repository = get(),
            syncNotesUseCase = get(),
            groupNotesByMonthUseCase = get()
        )
    }
}

val appModules = listOf(
    networkModule,
    databaseModule,
    repositoryModule,
    useCaseModule,
    viewModelModule
)