package com.kiryha.noting.presentation.ui

import androidx.lifecycle.ViewModel
import com.kiryha.noting.data.model.Note
import com.kiryha.noting.data.model.NoteDAO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NoteViewModel(): ViewModel() {
    private val workingWithNotes = NoteDAO()
    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes.asStateFlow()
    init {
        _notes.value = workingWithNotes.getNotes().item
    }
}