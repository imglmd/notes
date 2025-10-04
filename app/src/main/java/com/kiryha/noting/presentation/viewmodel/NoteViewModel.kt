package com.kiryha.noting.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kiryha.noting.data.repository.NoteRepository
import com.kiryha.noting.domain.model.Note
import com.kiryha.noting.domain.status.NoteStatus
import com.kiryha.noting.domain.status.ResultWithStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NoteViewModel(private val repository: NoteRepository): ViewModel() {
    private val _notes = MutableStateFlow<ResultWithStatus<List<Note>>>(
        ResultWithStatus(
            emptyList(),
            NoteStatus.Success
        )
    )
    val notes: StateFlow<ResultWithStatus<List<Note>>> get() = _notes

    private val _status = MutableStateFlow<NoteStatus>(NoteStatus.Success)
    val status: StateFlow<NoteStatus> get() = _status

    private val _selectedNote = MutableStateFlow<ResultWithStatus<Note>>(
        ResultWithStatus(
            Note(
                id = -1,
                text = "",
                date = ""
            ), NoteStatus.Success
        )
    )
    val selectedNote: StateFlow<ResultWithStatus<Note>> get() = _selectedNote

    init {
        loadNotes()
    }

    fun loadNotes() {
        viewModelScope.launch {
            val result = repository.getNotes()
            _notes.value = result
            _status.value = result.status
        }
    }



    fun getNote(id: Int) {
        viewModelScope.launch {
            val result = repository.getNote(id)
            _selectedNote.value = result
            _status.value = result.status
        }
    }

    fun upsertNote(note: Note) {
        viewModelScope.launch {
            val result = repository.upsertNote(note)
            _notes.value = result
            _status.value = result.status
        }
    }

    fun deleteNote(id: Int) {
        viewModelScope.launch {
            val result = repository.deleteNote(id)
            _notes.value = result
            _status.value = result.status
        }
    }
}